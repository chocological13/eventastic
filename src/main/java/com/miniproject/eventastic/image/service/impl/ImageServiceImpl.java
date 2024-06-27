package com.miniproject.eventastic.image.service.impl;

import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadDto;
import com.miniproject.eventastic.image.repository.ImageRepository;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.responses.Response;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final ImageRepository imageRepository;
  private final CloudinaryService cloudinaryService;


  @Override
  public ResponseEntity<Response<Object>> uploadImage(ImageUploadDto imageUploadDto) {
    try {
      if (imageUploadDto.getFileName().isEmpty()) {
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Image not found");
      }
      if (imageUploadDto.getFile().isEmpty()) {
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Image not found");
      }
      Image image = new Image();
      image.setImageName(imageUploadDto.getFileName());
      image.setImageUrl(cloudinaryService.uploadFile(imageUploadDto.getFile(), "eventastic"));
      if (image.getImageUrl() == null) {
        return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Image not found");
      }
      imageRepository.save(image);
      return Response.successfulResponse(HttpStatus.OK.value(), "Image uploaded", image);
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
