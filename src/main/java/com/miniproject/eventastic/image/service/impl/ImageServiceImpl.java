package com.miniproject.eventastic.image.service.impl;

import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.Image;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;
import com.miniproject.eventastic.image.repository.ImageRepository;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class ImageServiceImpl implements ImageService {

  private final ImageRepository imageRepository;
//  private final CloudinaryService cloudinaryService;
//  private final UsersService usersService;

  @Override
  public void saveImage(Image image) {
    imageRepository.save(image);
  }

//  @Override
//  public ImageUploadResponseDto uploadImage(ImageUploadRequestDto imageUploadRequestDto) {
//    try {
//      if (imageUploadRequestDto.getFileName().isEmpty()) {
//        return null;
//      }
//      if (imageUploadRequestDto.getFile().isEmpty()) {
//        return null;
//      }
//
//      // Get the currently logged-in user
//      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//      String username = authentication.getName();
//      Users owner = usersService.getByUsername(username);
//
//      // Define the objects
//      String imageName = imageUploadRequestDto.getFileName();
//      String imageUrl = cloudinaryService.uploadFile(imageUploadRequestDto.getFile(), "eventastic");
//
//      Image image = new Image();
//      image.setImageName(imageName);
//      image.setImageUrl(imageUrl);
//      if (image.getImageUrl() == null) {
//        return null;
//      }
//      image.setOwner(owner);
//      imageRepository.save(image);
//
//      return new ImageUploadResponseDto(image);
//    } catch (Exception e) {
//      e.printStackTrace();
//      return null;
//    }
//  }

  @Override
  public Image getImageById(Long imageId) {
    return imageRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException("Image not found"));
  }
}
