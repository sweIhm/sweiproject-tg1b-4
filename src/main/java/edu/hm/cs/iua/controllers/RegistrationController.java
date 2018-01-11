package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.auth.InvalidTokenException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.login.UserNotValidatedException;
import edu.hm.cs.iua.exceptions.registration.EmailAlreadyTakenException;
import edu.hm.cs.iua.exceptions.registration.EmailTransmissionFailed;
import edu.hm.cs.iua.exceptions.registration.InvalidDataException;
import edu.hm.cs.iua.exceptions.registration.RegistrationException;
import edu.hm.cs.iua.exceptions.registration.UsernameAlreadyTakenException;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import edu.hm.cs.iua.utils.EmailClient;
import edu.hm.cs.iua.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final TokenGenerator generator = new TokenGenerator();
    private static final List<String> validDomains = new LinkedList<>();
    private static final String PROTOCOLL = "http://";
    public static final String CODE_PARAMETER = "&code=";

    static {
        validDomains.add("hm.edu");
        validDomains.add("calpoly.edu");
    }

    private static String confirmationEmail = "/templates/confirmationEmail.html";
    private static String resetPassEmail = "/templates/resetPasswordEmail.html";

    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @Value("${email.server}")
    private String emailServer;
    @Value("${email.port}")
    private String emailPort;
    @Value("${host.url}")
    private String hostAddress;

    @PostMapping @ResponseBody
    public void create(@RequestBody IUAUser input)
            throws RegistrationException {

        if (input.getName() == null || input.getName().trim().isEmpty())
            throw new InvalidDataException("Name invalid.");
        if (input.getEmail() == null || !isValidEmail(input.getEmail()))
            throw new InvalidDataException("Email invalid.");
        if (input.getPassword() == null || input.getPassword().trim().isEmpty())
            throw new InvalidDataException("Password invalid.");

        for (IUAUser user: userRepository.findAll()) {
            if (user.getEmail().equals(input.getEmail()))
                throw new EmailAlreadyTakenException();
            if (user.getName().equals(input.getName()))
                throw new UsernameAlreadyTakenException();
        }

        final IUAUser user = userRepository.save(new IUAUser(input.getName(), input.getEmail(), input.getPassword(), generator.nextToken()));
        try {
            sendAuthorisationCode(user.getEmail(), user.getId(), user.getConfirmationCode());
        } catch (MessagingException e) {
            userRepository.delete(user.getId());
            throw new EmailTransmissionFailed();
        }
    }

    @GetMapping("activate")
    public String activate(@RequestParam Long userId, @RequestParam String code) {

        final IUAUser user = userRepository.findOne(userId);
        if (user == null)
            return "activationUserNotFound";
        if (user.isValidated())
            return "activationSuccessful";
        if (user.getConfirmationCode().equals(code)) {
            user.setValidated(true);
            user.setConfirmationCode(null);
            userRepository.save(user);
            return "activationSuccessful";
        }
        return "activationInvalidCode";
    }

    @GetMapping("request_reset") @ResponseBody
    public void resetPassword(@RequestParam String email)
            throws EmailTransmissionFailed, UserNotFoundException, UserNotValidatedException {

        IUAUser currentUser = null;
        for (IUAUser user: userRepository.findAll())
            if (user.getEmail().equals(email)) {
                currentUser = user;
                break;
            }

        if (currentUser != null) {
            if (!currentUser.isValidated())
                throw new UserNotValidatedException();
            final String code = generator.nextToken();
            currentUser.setRequestingPassword(true);
            currentUser.setConfirmationCode(code);
            final Long userId = userRepository.save(currentUser).getId();
            try {
                sendPasswordResetEmail(email, userId, code);
            } catch (MessagingException e) {
                currentUser = userRepository.findOne(userId);
                currentUser.setRequestingPassword(false);
                currentUser.setConfirmationCode(null);
                userRepository.save(currentUser);
                throw new EmailTransmissionFailed();
            }
        } else
            throw new UserNotFoundException();
    }

    @GetMapping("reset")
    public String resetPage(@RequestParam Long userId, @RequestParam String code, Model model) {
        model.addAttribute("link", "http://" + hostAddress + "/register/reset?userId=" + userId + CODE_PARAMETER + code);
        return "resetPasswordPage";
    }

    @PostMapping("reset") @ResponseBody
    public void resetPassword(@RequestParam Long userId, @RequestParam String code, @RequestBody IUAUser user)
            throws UserNotFoundException, InvalidTokenException, InvalidDataException {

        final IUAUser currentUser = userRepository.findOne(userId);
        if (currentUser == null)
            throw new UserNotFoundException();
        if (!currentUser.isRequestingPassword())
            throw new UserNotFoundException();
        if (!currentUser.getConfirmationCode().equals(code))
            throw new InvalidTokenException();
        if (user.getPassword() == null || user.getPassword().trim().isEmpty())
            throw new InvalidDataException("Password invalid.");

        currentUser.setRequestingPassword(false);
        currentUser.setConfirmationCode(null);
        currentUser.setPassword(user.getPassword());
        userRepository.save(currentUser);
    }

    private void sendPasswordResetEmail(String email, Long userId, String code)
            throws MessagingException {

        final String emailUserName = System.getenv("EMAIL_USERNAME");
        final String emailPassword = System.getenv("EMAIL_PASSWORD");
        final EmailClient client = new EmailClient(emailUserName, emailPassword, emailServer, emailPort);
        final String link = PROTOCOLL + hostAddress + "/register/reset?userId=" + userId + CODE_PARAMETER + code;
        final InputStream resource = this.getClass().getResourceAsStream(resetPassEmail);
        final String content;

        if (resource != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            final StringBuilder emailContent = new StringBuilder("");
            reader.lines().forEach(emailContent::append);
            content = emailContent.toString().replace("${link}", link);
        } else
            content = "Click <a href=\"" + link + "\">here</a> to reset your IUA user password.";

        client.sendMail(email, "IUA Password Reset", content);
    }

    private void sendAuthorisationCode(String email, Long userId, String code)
            throws MessagingException {

        final String emailUserName = System.getenv("EMAIL_USERNAME");
        final String emailPassword = System.getenv("EMAIL_PASSWORD");
        final EmailClient client = new EmailClient(emailUserName, emailPassword, emailServer, emailPort);
        final String link = PROTOCOLL + hostAddress + "/register/activate?userId=" + userId + CODE_PARAMETER + code;
        final InputStream resource = this.getClass().getResourceAsStream(confirmationEmail);
        final String content;

        if (resource != null) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(resource));
            final StringBuilder emailContent = new StringBuilder("");
            reader.lines().forEach(emailContent::append);
            content = emailContent.toString().replace("${link}", link);
        } else
            content = "Click <a href=\"" + link + "\">here</a> to activate your IUA Account.";

        client.sendMail(email, "Confirm your IUA Account", content);
    }

    private boolean isValidEmail(String email) {
        final int firstAt = email.indexOf('@');
        final int lastAt = email.lastIndexOf('@');
        return firstAt > 0 && lastAt != email.length() - 1
                && firstAt == lastAt
                && validDomains.contains(email.substring(firstAt + 1));
    }

}