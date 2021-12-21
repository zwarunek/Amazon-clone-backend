package com.zacharywarunek.amazonclone.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UsernameTakenException extends Exception {

  public String accountUsername;
}
