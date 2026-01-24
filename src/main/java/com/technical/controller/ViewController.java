package com.technical.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }
    
    @GetMapping("/api/view/password-reset-success")
    public String passwordResetSuccess() {
        return "password-reset-success";
    }
}
