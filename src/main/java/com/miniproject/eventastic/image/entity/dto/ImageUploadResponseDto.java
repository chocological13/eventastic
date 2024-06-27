package com.miniproject.eventastic.image.entity.dto;

import lombok.Data;

@Data
public class ImageUploadResponseDto {
  private String imageName;
  private String imageUrl;
  private String owner;

  public ImageUploadResponseDto(String imageName, String imageUrl, String owner) {
    this.imageName = imageName;
    this.imageUrl = imageUrl;
    this.owner = owner;
  }
}
