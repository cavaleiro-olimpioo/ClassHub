package com.classhub.api.Controller;

import com.classhub.api.Models.User;
import com.classhub.api.Services.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/login")
@RequiredArgsConstructor
public class LoginController {

    private boolean[] verify = new boolean[2];

    @PostMapping
    public boolean[] returnData(@RequestBody User user){
        LoginService loginVerify = new LoginService();
        verify = loginVerify.verifyLogin(user.getName(), user.getPassword(), user.getWhoami());
        return verify;
    }
}
