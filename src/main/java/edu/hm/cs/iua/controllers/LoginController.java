package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.login.InvalidPasswordException;
import edu.hm.cs.iua.exceptions.login.LoginException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.login.UserNotValidatedException;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private IUAUserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public Token login(@RequestParam String email, @RequestParam String password)
            throws LoginException {

        for (IUAUser user : userRepository.findAll())
            if (user.getEmail().equals(email)) {
                if (!user.isValidated())
                    throw new UserNotValidatedException("User is not validated.");
                if (!user.getPassword().equals(password))
                    throw new InvalidPasswordException("Password is incorrect.");
                if (tokenRepository.exists(user.getId()))
                    return tokenRepository.findOne(user.getId());
                return tokenRepository.save(new Token(user.getId()));
            }
        throw new UserNotFoundException("No user with the specified email address could be found.");
    }

}