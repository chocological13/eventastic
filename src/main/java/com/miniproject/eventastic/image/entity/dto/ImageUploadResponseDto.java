package com.miniproject.eventastic.image.entity.dto;

import lombok.Data;

@Data
public class ImageUploadResponseDto {
  private Long id;
  private String imageName;
  private String imageUrl;
  private String owner;

  public ImageUploadResponseDto(Long id, String imageName, String imageUrl, String owner) {
    this.id = id;
    this.imageName = imageName;
    this.imageUrl = imageUrl;
    this.owner = owner;
  }
}
