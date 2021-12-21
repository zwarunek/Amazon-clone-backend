package com.zacharywarunek.amazonclone.exceptions;

import lombok.AllArgsConstructor;

public class UsernameTakenException extends Exception {
  public UsernameTakenException(String message) {
    super(message);
  }
}
