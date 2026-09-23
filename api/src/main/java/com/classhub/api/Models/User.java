package com.classhub.api.Models;

import lombok.Getter;
import lombok.Setter;

/**
 * Legacy authentication payload retained only for historical reference.
 * This class is not used by the current authentication flow.
 */
@Deprecated(since = "2026-09-23", forRemoval = true)
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
