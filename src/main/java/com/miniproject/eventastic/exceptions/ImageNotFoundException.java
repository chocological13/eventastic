package com.miniproject.eventastic.exceptions;

import com.cloudinary.api.exceptions.NotFound;

public class ImageNotFoundException extends NotFound {


  public ImageNotFoundException(String message) {
    super(message);
  }

}
