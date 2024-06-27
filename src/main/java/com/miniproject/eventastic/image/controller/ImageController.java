package com.miniproject.eventastic.image.controller;

import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.responses.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/image")
public class ImageController {

   private final ImageService imageService;

  @PostMapping("/upload")
  public ResponseEntity<Response<ImageUploadResponseDto>> uploadImage(ImageUploadRequestDto requestDto) {
    ImageUploadResponseDto response = imageService.uploadImage(requestDto);
    if (response == null) {
      return ResponseEntity.noContent().build();
    } else {
      return Response.successfulResponse(HttpStatus.OK.value(), "Image uploaded! :D", response);
    }
  }
}
