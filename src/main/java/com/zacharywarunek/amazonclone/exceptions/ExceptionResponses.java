package com.zacharywarunek.amazonclone.exceptions;

public enum ExceptionResponses {
  BAD_REQUEST("An error occurred: %s"),
  USERNAME_NOT_FOUND("Account with username %s not found"),
  ACCOUNT_ID_NOT_FOUND("Account with id %s not found"),
  INCORRECT_AUTH("Username or password was incorrect"),
  AUTH_FIELD_NOT_FOUND("username or password field not found"),
  USERNAME_TAKEN("Username is already in use"),
  NULL_VALUES("Null values present"),
  TOKEN_NOT_FOUND("token not found"),
  INVALID_TOKEN("Invalid token"),
  EXPIRED_TOKEN("Invalid token"),
  EMAIL_ALREADY_CONFIRMED("Email already confirmed"),
  NO_FAVORITE_ADDRESS("Account with id %s has no favorite address"),
  NO_FAVORITE_PAYMENT_METHOD("Account with id %s has no favorite payment method"),
  ADDRESS_NOT_FOUND("Address with id %s not found"),
  ADDRESS_UNAUTHORIZED("Address with id %s does not belong to account with id %s"),
  PAYMENT_METHOD_UNAUTHORIZED("Payment method with id %s does not belong to account with id %s"),
  PAYMENT_TYPE_ID_NOT_FOUND("Payment type with id %s not found"),
  PAYMENT_METHOD_ID_NOT_FOUND("Payment method with id %s not found");

  public final String label;

  ExceptionResponses(String label) {
    this.label = label;
  }
}
