package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.User;
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

    @GetMapping
    public void test() {
        final User user = new User("Test", "information.iua@gmail.com", "test", "CONFIRMATION_CODE");
        user.setValidated(true);
        userRepository.save(user);
    }

}
