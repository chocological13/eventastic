package com.miniproject.eventastic.mail.service;

import com.miniproject.eventastic.mail.service.entity.dto.MailTemplate;

public interface MailService {

  void sendEmail(MailTemplate mailTemplate);
}
