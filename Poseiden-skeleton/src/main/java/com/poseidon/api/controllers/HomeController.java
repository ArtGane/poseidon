package com.poseidon.api.controllers;

import com.poseidon.api.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@Slf4j
public class HomeController {

    @Autowired
    UserRepository userRepository;

    @GetMapping("/")
    public String home(Model model) {
        return "home";
    }
}
