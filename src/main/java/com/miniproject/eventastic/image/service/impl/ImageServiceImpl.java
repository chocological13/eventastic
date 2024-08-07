package com.miniproject.eventastic.image.service.impl;

import com.miniproject.eventastic.exceptions.image.ImageNotFoundException;
import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.image.repository.ImageEventRepository;
import com.miniproject.eventastic.image.repository.ImageUserAvatarRepository;
import com.miniproject.eventastic.image.service.CloudinaryService;
import com.miniproject.eventastic.image.service.ImageService;
import com.miniproject.eventastic.users.entity.Users;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

  private final ImageUserAvatarRepository imageUserAvatarRepository;
  private final ImageEventRepository imageEventRepository;
  private final CloudinaryService cloudinaryService;

  @Override
  public void saveAvatar(ImageUserAvatar imageUserAvatar) {
    imageUserAvatarRepository.save(imageUserAvatar);
  }

  @Override
  public void saveEventImage(ImageEvent imageEvent) {
    imageEventRepository.save(imageEvent);
  }

  @Override
  public ImageUserAvatar getAvatarById(Long imageId) {
    return imageUserAvatarRepository.findById(imageId)
        .orElseThrow(() -> new ImageNotFoundException("ImageUserAvatar not found"));
  }

  @Override
  @Transactional
  public ImageUserAvatar uploadAvatar(ImageUploadRequestDto requestDto, Users user) throws IllegalArgumentException {
    validateUploadRequest(requestDto);

    String imageName = requestDto.getFileName();
    String imageUrl = cloudinaryService.uploadFile(requestDto.getFile(),
        "eventastic/users/" + user.getId().toString());

    ImageUserAvatar imageUserAvatar = new ImageUserAvatar();
    imageUserAvatar.setImageName(imageName);
    imageUserAvatar.setImageUrl(imageUrl);
    imageUserAvatar.setUser(user);
    if (imageUserAvatar.getImageUrl() == null) {
      // Handle error appropriately
      return null;
    }
    return imageUserAvatarRepository.save(imageUserAvatar);
  }

  @Override
  @Transactional
  public ImageEvent uploadEventImage(ImageUploadRequestDto requestDto, Users organizer) throws IllegalArgumentException {
      validateUploadRequest(requestDto);

      String imageName = requestDto.getFileName();
      String imageUrl = cloudinaryService.uploadFile(requestDto.getFile(),
          "eventastic/organizer" + organizer.getId().toString());

      ImageEvent imageEvent = new ImageEvent();
      imageEvent.setImageName(imageName);
      imageEvent.setImageUrl(imageUrl);
      imageEvent.setOrganizer(organizer);
      if (imageEvent.getImageUrl() == null) {
        // Handle error appropriately
        return null;
      }
      return imageEventRepository.save(imageEvent);
  }

  @Override
  public ImageEvent getEventImageById(Long imageId) {
    return imageEventRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException("Image not found"));
  }

  private void validateUploadRequest(ImageUploadRequestDto requestDto) {
    if (requestDto.getFileName().isEmpty() || requestDto.getFile().isEmpty()) {
      throw new IllegalArgumentException("Invalid upload request");
    }
  }

}
