package edu.hm.cs.iua.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


@Controller // so framework can recognize this as a controller class
@RequestMapping(value = "/", method = RequestMethod.GET)
public class HomeController {

    @GetMapping
    public String index() { return "index.html"; }
}

