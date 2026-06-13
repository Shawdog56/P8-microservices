package com.auth.user.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InitController {
    @GetMapping("/")
    public String init(){
        return "index";
    }
}
