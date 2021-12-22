package com.zacharywarunek.amazonclone.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public class AccountDetails {
    private String first_name;
    private String last_name;
    private String username;
    private String password;
}
