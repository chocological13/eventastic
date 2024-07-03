package com.miniproject.eventastic.image.entity.dto.eventImage;

import com.miniproject.eventastic.image.entity.Image;
import lombok.Data;

@Data
public class EventImageResponseDto {

  private Long id;
  private String imageName;
  private String imageUrl;

  public EventImageResponseDto(Image image) {
    this.id = image.getId();
    this.imageName = image.getImageName();
    this.imageUrl = image.getImageUrl();
  }

  public EventImageResponseDto toDto(Image image) {
    return new EventImageResponseDto(image);
  }

}
