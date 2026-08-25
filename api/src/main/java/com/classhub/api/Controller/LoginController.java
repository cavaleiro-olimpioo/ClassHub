package com.classhub.api.Controller;

import com.classhub.api.Models.User;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    @PostMapping
    public User returnData(@RequestBody User user){
        return user;
    }
}
