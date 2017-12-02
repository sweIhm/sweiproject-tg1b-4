package edu.hm.cs.iua.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeController {

    @GetMapping
    public String index() {
        return "index.html";
    }

    @PostMapping
    public void testPost() {

    }

    @PutMapping
    public void testPut() {

    }

    @DeleteMapping
    public void testDelete() {

    }

}