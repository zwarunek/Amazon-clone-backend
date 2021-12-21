package com.zacharywarunek.amazonclone.exceptions;

public class BadRequestException extends Exception {

  public BadRequestException() {}

  public BadRequestException(String message) {
    super(message);
  }
}
