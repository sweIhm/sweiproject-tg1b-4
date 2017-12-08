package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.login.InvalidPasswordException;
import edu.hm.cs.iua.exceptions.login.LoginException;
import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.exceptions.login.UserNotValidatedException;
import edu.hm.cs.iua.models.Token;
import edu.hm.cs.iua.models.Nutzer;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.NutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private NutzerRepository nutzerRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public Token login(@RequestParam String email, @RequestParam String password)
            throws LoginException {

        for (Nutzer nutzer : nutzerRepository.findAll())
            if (nutzer.getEmail().equals(email)) {
                if (!nutzer.isValidated())
                    throw new UserNotValidatedException("User is not validated.");
                if (!nutzer.getPassword().equals(password))
                    throw new InvalidPasswordException("Password is incorrect.");
                if (tokenRepository.exists(nutzer.getId()))
                    return tokenRepository.findOne(nutzer.getId());
                return tokenRepository.save(new Token(nutzer.getId()));
            }
        throw new UserNotFoundException("No user with the specified email address could be found.");
    }

}