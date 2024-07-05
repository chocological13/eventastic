package com.miniproject.eventastic.exceptions.image;


public class ImageNotFoundException extends RuntimeException {


  public ImageNotFoundException(String message) {
    super(message);
  }

  public ImageNotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
