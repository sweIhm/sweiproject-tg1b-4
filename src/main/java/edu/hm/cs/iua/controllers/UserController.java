package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.models.Nutzer;
import edu.hm.cs.iua.models.VisibleUserData;
import edu.hm.cs.iua.repositories.NutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private NutzerRepository nutzerRepository;

    @GetMapping("{id}")
    public VisibleUserData find(@PathVariable Long id) throws UserNotFoundException {
        final Nutzer nutzer = nutzerRepository.findOne(id);
        if (nutzer == null || !nutzer.isValidated())
            throw new UserNotFoundException("User not found.");
        return VisibleUserData.getUserData(nutzer);
    }

}
