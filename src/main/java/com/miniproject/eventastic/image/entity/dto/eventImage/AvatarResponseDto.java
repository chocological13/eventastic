package com.miniproject.eventastic.image.entity.dto.eventImage;

import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import lombok.Data;

@Data
public class AvatarResponseDto {

  private Long id;
  private String imageName;
  private String imageUrl;

  public AvatarResponseDto(ImageUserAvatar imageUserAvatar) {
    this.id = imageUserAvatar.getId();
    this.imageName = imageUserAvatar.getImageName();
    this.imageUrl = imageUserAvatar.getImageUrl();
  }

  public AvatarResponseDto toDto(ImageUserAvatar imageUserAvatar) {
    return new AvatarResponseDto(imageUserAvatar);
  }

}
