package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.User;
import edu.hm.cs.iua.repositories.ActivityRepository;
import edu.hm.cs.iua.repositories.TokenRepository;
import edu.hm.cs.iua.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private TokenRepository tokenRepository;

    @GetMapping
    public void login() {
        System.out.println(userRepository.count());
        for (User user: userRepository.findAll())
            System.out.println(user.getId() + " " + user.getName() + " " + user.getEmail() + " " + user.getPassword());
    }
}