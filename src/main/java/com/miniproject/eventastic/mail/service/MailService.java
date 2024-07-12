package com.miniproject.eventastic.mail.service;

import com.miniproject.eventastic.mail.service.entity.dto.RegisterEmailTemp;

public interface MailService {

  void sendEmail(String to, String subject, String body);
  void sendWelcomeEmail(RegisterEmailTemp template);
}
