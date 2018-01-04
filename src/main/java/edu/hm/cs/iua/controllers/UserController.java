package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.exceptions.login.UserNotFoundException;
import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.models.UserProfile;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private IUAUserRepository userRepository;

    @GetMapping()
    public List<UserProfile> listAll() {
        final List<UserProfile> users = new ArrayList<>((int)userRepository.count());
        userRepository.findAll().forEach(user -> users.add(user.getProfile()));
        return users;
    }

    @GetMapping("{id}")
    public UserProfile find(@PathVariable Long id) throws UserNotFoundException {
        final IUAUser user = userRepository.findOne(id);
        if (user == null || !user.isValidated())
            throw new UserNotFoundException();
        return user.getProfile();
    }

}
