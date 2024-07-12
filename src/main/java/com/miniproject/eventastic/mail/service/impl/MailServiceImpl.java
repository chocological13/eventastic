package com.miniproject.eventastic.mail.service.impl;

import com.miniproject.eventastic.mail.service.MailService;
import com.miniproject.eventastic.mail.service.entity.dto.RegisterEmailTemp;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

  private final JavaMailSender mailSender;

  @Override
  public void sendEmail(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("MS_ZxYYbl@trial-ynrw7gy0m32l2k8e.mlsender.net");
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);

    mailSender.send(message);
  }

  @Override
  public void sendWelcomeEmail(RegisterEmailTemp template) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom("MS_ZxYYbl@trial-ynrw7gy0m32l2k8e.mlsender.net");
    message.setTo(template.getTo());
    message.setSubject(template.getSubject());
    message.setText(template.getBody());

    mailSender.send(message);
  }
}
