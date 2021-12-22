package com.zacharywarunek.amazonclone.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import javax.mail.MessagingException;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

  JavaMailSenderImpl javaMailSender;
  @Mock private JavaMailSender mailSender;
  @InjectMocks private EmailService emailService;

  @Test
  void sendEmail() {
    javaMailSender = new JavaMailSenderImpl();
    given(mailSender.createMimeMessage()).willReturn(javaMailSender.createMimeMessage());
    emailService.send("test@gmail.com", "THIS IS A MOCK EMAIL");
  }

  @Test
  void throwException() {
    javaMailSender = new JavaMailSenderImpl();
    given(mailSender.createMimeMessage()).willReturn(javaMailSender.createMimeMessage());
    emailService.send("NOT A VALID EMAIL", "THIS IS A MOCK EMAIL");
  }
}
