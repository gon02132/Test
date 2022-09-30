package com.study.controller;

import com.study.dto.User;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController{
    @GetMapping("/")
    public User test(User user) {
        user.setPassword(user.getPassword()+"PWD");
        return user;
    }
}