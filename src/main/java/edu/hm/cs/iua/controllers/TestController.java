package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.IUAUser;
import edu.hm.cs.iua.repositories.IUAUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/test")
public class TestController {

    @Autowired
    private IUAUserRepository userRepository;

    @GetMapping @ResponseBody
    public void test() {
        final IUAUser user = new IUAUser("Test", "information.iua@gmail.com", "test", "CONFIRMATION_CODE");
        user.setValidated(true);
        userRepository.save(user);
    }

}
