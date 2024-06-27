package com.miniproject.eventastic.image.service;

import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadDto;
import com.miniproject.eventastic.responses.Response;
import org.springframework.http.ResponseEntity;

public interface ImageService {

  ResponseEntity<Response<Object>> uploadImage(ImageUploadDto imageUploadDto);
}
