package com.zacharywarunek.amazonclone.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailService {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

  private final JavaMailSender mailSender;

  @Async
  public void send(String to, String email) {
    try {
      MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage(), "utf-8");
      helper.setText(email, true);
      helper.setTo(to);
      helper.setSubject("Confirm your email");
      helper.setFrom("noreply@amazon.zacharywarunek.com");
      mailSender.send(helper.getMimeMessage());
    } catch (Exception e) {
      LOGGER.error("failed to send email", e);
    }
  }
}
