package com.zacharywarunek.amazonclone.email;

public interface EmailSender {
    void send(String to, String email);
}
