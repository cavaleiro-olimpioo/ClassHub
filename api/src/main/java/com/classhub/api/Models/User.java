package com.classhub.api.Models;

import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter
    @Setter
    private String name;

    @Getter
    @Setter
    private String password;

    @Getter
    @Setter
    private String whoami;

    public User(String name, String password, String whoami){
        this.name = name;
        this.password = password;
        this.whoami = whoami;
    }


}
