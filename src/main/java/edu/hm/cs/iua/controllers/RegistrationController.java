package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.EmailAlreadyTakenException;
import edu.hm.cs.iua.exceptions.InvalidDataException;
import edu.hm.cs.iua.exceptions.RegistrationException;
import edu.hm.cs.iua.exceptions.UsernameAlreadyTakenException;
import edu.hm.cs.iua.models.User;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/register")
public class RegistrationController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public void testGet() {

    }

    @PutMapping
    public void testPut() {

    }

    @DeleteMapping
    public void testDelete() {

    }

    @PostMapping
    public void create(@RequestBody User input) throws RegistrationException {
        for (User user: userRepository.findAll()) {
            if (user.getEmail().equals(input.getEmail()))
                throw new EmailAlreadyTakenException();
            if (user.getName().equals(input.getName()))
                throw new UsernameAlreadyTakenException();
        }
        if (input.getName() == null)
            throw new InvalidDataException("Name invalid.");
        if (input.getEmail() == null || !input.getEmail().endsWith("@hm.edu"))
            throw new InvalidDataException("Email invalid.");
        if (input.getPassword() == null)
            throw new InvalidDataException("Password invalid.");
        final User user = new User(input.getName(), input.getEmail(), input.getPassword());
        userRepository.save(user);
    }

}