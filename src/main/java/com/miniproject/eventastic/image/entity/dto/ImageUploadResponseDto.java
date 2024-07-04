package com.miniproject.eventastic.image.entity.dto;

import com.miniproject.eventastic.image.entity.Image;
import lombok.Data;

@Data
public class ImageUploadResponseDto {
  private Long id;
  private String imageName;
  private String imageUrl;
  private String owner;

  public ImageUploadResponseDto(Image image) {
    this.id = image.getId();
    this.imageName = image.getImageName();
    this.imageUrl = image.getImageUrl();
    this.owner = image.getOwner().getUsername();
  }

  public ImageUploadResponseDto toDto(Image image) {
    return new ImageUploadResponseDto(image);
  }
}
