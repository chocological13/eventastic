package com.miniproject.eventastic.mail.service.entity.dto;

import lombok.Data;

@Data
public class RegisterEmailTemp {
    private String to;
    private String subject;
    private String body;

    public RegisterEmailTemp buildTemplate(String to, String fullName) {
      RegisterEmailTemp temp = new RegisterEmailTemp();
      temp.setTo(to);
      temp.setSubject("Welcome to Eventastic!");
      temp.setBody("Thank you for registering, " + fullName + "! This is your e-mail confirmation for your registration.");
      return temp;
    }
}
