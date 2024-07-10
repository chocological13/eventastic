package com.miniproject.eventastic.image.service.impl;

import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.repository.ImageRepository;
import com.miniproject.eventastic.image.service.ImageService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final ImageRepository imageRepository;

  @Override
  public void saveImage(Image image) {
    imageRepository.save(image);
  }

  @Override
  public Image getImageById(Long imageId) {
    return imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException("Image not found"));
  }
}
