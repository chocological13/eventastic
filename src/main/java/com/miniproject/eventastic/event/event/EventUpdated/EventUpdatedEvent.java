package com.miniproject.eventastic.event.event.EventUpdated;

import com.miniproject.eventastic.event.entity.Event;
import com.miniproject.eventastic.event.entity.dto.updateEvent.UpdateEventRequestDto;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class EventUpdatedEvent extends ApplicationEvent {

  private final Event event;
  private final UpdateEventRequestDto requestDto;

  public EventUpdatedEvent(Object source, Event event, UpdateEventRequestDto requestDto) {
    super(source);
    this.event = event;
    this.requestDto = requestDto;
  }
}
