package com.zacharywarunek.amazonclone.exceptions;

public class ExpiredTokenException extends Exception {
  public ExpiredTokenException(String message) {
    super(message);
  }
}
