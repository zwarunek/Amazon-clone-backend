package com.zacharywarunek.amazonclone.registration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistrationRequest {
  private final String firstName;
  private final String lastName;
  private final String username;
  private final String password;
}
