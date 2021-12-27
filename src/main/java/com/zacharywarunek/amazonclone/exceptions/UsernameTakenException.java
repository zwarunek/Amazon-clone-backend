package com.zacharywarunek.amazonclone.exceptions;

public class UsernameTakenException extends Exception {
  public UsernameTakenException(String message) {
    super(message);
  }
}
