package com.miniproject.eventastic.exceptions;

import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;

public class CategoryNotFoundException extends RuntimeException {

  public CategoryNotFoundException(String message) {
    super(message);
  }

  public CategoryNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }

}
