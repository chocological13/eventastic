package com.miniproject.eventastic.image.service;

import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;

public interface ImageService {

  ImageUploadResponseDto uploadImage(ImageUploadRequestDto imageUploadRequestDto);

  Image getImageById(Long imageId);
}
