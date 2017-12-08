package edu.hm.cs.iua.controllers;

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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping("/register")
public class RegistrationController {

    private static final TokenGenerator generator = new TokenGenerator();
    private static final List<String> validDomains = new LinkedList<>();
    static {
        validDomains.add("hm.edu");
        validDomains.add("calpoly.edu");
    }

    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;
    @Value("${email.name}")
    private String emailUserName;
    @Value("${email.password}")
    private String emailPassword;
    @Value("${email.server}")
    private String emailServer;
    @Value("${email.port}")
    private String emailPort;
    @Value("${host.url}")
    private String hostAddress;

    @ResponseBody
    @PostMapping
    public void create(@RequestBody IUAUser input)
            throws RegistrationException {

        if (input.getName() == null)
            throw new InvalidDataException("Name invalid.");
        if (input.getEmail() == null || !isValidEmail(input.getEmail()))
            throw new InvalidDataException("Email invalid.");
        if (input.getPassword() == null)
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

    @GetMapping
    public String activate(@RequestParam Long userId, @RequestParam String code) {

        final IUAUser user = userRepository.findOne(userId);
        if (user == null)
            return "activation/activationUserNotFound.html";
        if (user.getConfirmationCode().equals(code)) {
            user.setValidated(true);
            user.setConfirmationCode(null);
            userRepository.save(user);
            return "activation/activationSuccessful.html";
        }
        return "activation/activationInvalidCode.html";
    }

    private void sendAuthorisationCode(String email, Long userId, String code)
            throws MessagingException {

        if (System.getenv("EMAIL_USERNAME") != null)
            emailUserName = System.getenv("EMAIL_USERNAME");
        if (System.getenv("EMAIL_PASSWORD") != null)
            emailPassword = System.getenv("EMAIL_PASSWORD");
        final EmailClient client = new EmailClient(emailUserName, emailPassword, emailServer, emailPort);
        final String link = "http://" + hostAddress + "/register?userId=" + userId + "&code=" + code;
        String content;
        try {
            content = new String(Files.readAllBytes(Paths.get("static/activation/confirmationEmail.html"))).replace("{{link}}", link);
        } catch (IOException e) {
            content = "Click <a href=\"" + link + "\">here</a> to activate your IUA Account.";
        }
        client.sendMail(email, "Confirm your IUA Account", content);
    }

    private boolean isValidEmail(String email) {
        final int firstAt = email.indexOf('@');
        final int lastAt = email.lastIndexOf('@');
        return firstAt >= 0 && lastAt != email.length() - 1
                && firstAt == lastAt
                && validDomains.contains(email.substring(firstAt + 1));
    }

}