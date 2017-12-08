package edu.hm.cs.iua.controllers;

import edu.hm.cs.iua.models.Nutzer;
import edu.hm.cs.iua.repositories.NutzerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @Autowired
    private NutzerRepository nutzerRepository;

    @GetMapping
    public void test() {
        final Nutzer nutzer = new Nutzer("Test", "information.iua@gmail.com", "test", "CONFIRMATION_CODE");
        nutzer.setValidated(true);
        nutzerRepository.save(nutzer);
    }

}
