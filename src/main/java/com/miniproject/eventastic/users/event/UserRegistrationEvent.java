package com.miniproject.eventastic.users.event;

import com.miniproject.eventastic.users.entity.Users;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;

@Getter
@Slf4j
public class UserRegistrationEvent extends ApplicationEvent {

  private final Users user;

  public UserRegistrationEvent(Object source, Users user) {
    super(source);
    this.user = user;
    log.info("UserRegistrationEvent created for user: {}", user.getUsername());
  }

}
