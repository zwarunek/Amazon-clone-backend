package com.zacharywarunek.amazonclone.config;

public class Constants {

  public static final long ACCESS_TOKEN_VALIDITY_SECONDS = 5 * 60 * 60;
  public static final String SIGNING_KEY = System.getenv("SIGNING_KEY");
  public static final String TOKEN_PREFIX = "Bearer ";
  public static final String HEADER_STRING = "Authorization";
}
