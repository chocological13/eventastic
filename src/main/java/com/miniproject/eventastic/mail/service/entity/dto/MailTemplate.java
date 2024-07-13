package com.miniproject.eventastic.mail.service.entity.dto;

import com.miniproject.eventastic.trx.entity.Trx;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Data;

@Data
public class MailTemplate {
    private String to;
    private String subject;
    private String body;

    public MailTemplate buildRawTemp(String to, String subject, String body) {
      MailTemplate temp = new MailTemplate();
      temp.setTo(to);
      temp.setSubject(subject);
      temp.setBody(body);
      return temp;
    }

    public MailTemplate buildWelcomeTemp(String to, String fullName) {
      MailTemplate temp = new MailTemplate();
      temp.setTo(to);
      temp.setSubject("Welcome to Eventastic!");
      temp.setBody("Thank you for registering, " + fullName + "! This is your e-mail confirmation for your registration.");
      return temp;
    }

    public MailTemplate buildPurchaseTemp(Trx trx) {
      MailTemplate temp = new MailTemplate();
      String email = trx.getUser().getEmail();
      String eventTitle = trx.getEvent().getTitle();
      LocalDate eventDate = trx.getEvent().getEventDate();
      LocalTime eventStartTime = trx.getEvent().getStartTime();
      temp.setTo(email);
      temp.setSubject("Your purchase has been confirmed!");
      temp.setBody("Thank you for your purchase to event " + eventTitle + "! We will see you on " + eventDate +
                   ", " + eventStartTime + "!");
      return temp;
    }
}
