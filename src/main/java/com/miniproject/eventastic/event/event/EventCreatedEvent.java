package com.miniproject.eventastic.event.event;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.createEvent.CreateEventRequestDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventCreatedEvent extends ApplicationEvent {

  private final Event event;
  private final CreateEventRequestDto requestDto;

  public EventCreatedEvent(Object source, Event event, CreateEventRequestDto requestDto) {
    super(source);
    this.event = event;
    this.requestDto = requestDto;
  }

  public Event getEvent() {
    return event;
  }

  public CreateEventRequestDto getRequestDto() {
    return requestDto;
  }
}
