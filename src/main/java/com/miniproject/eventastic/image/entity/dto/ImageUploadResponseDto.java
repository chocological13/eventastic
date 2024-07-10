package com.miniproject.eventastic.image.entity.dto;

import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import lombok.Data;

@Data
public class ImageUploadResponseDto {
  private Long id;
  private String imageName;
  private String imageUrl;
  private String user;

  public ImageUploadResponseDto(ImageUserAvatar imageUserAvatar) {
    this.id = imageUserAvatar.getId();
    this.imageName = imageUserAvatar.getImageName();
    this.imageUrl = imageUserAvatar.getImageUrl();
    this.user = imageUserAvatar.getUser().getUsername();
  }

  public ImageUploadResponseDto toDto(ImageUserAvatar imageUserAvatar) {
    return new ImageUploadResponseDto(imageUserAvatar);
  }
}
