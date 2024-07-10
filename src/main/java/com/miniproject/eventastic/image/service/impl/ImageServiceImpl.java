package com.miniproject.eventastic.image.service.impl;

import com.miniproject.eventastic.event.entity.Event;
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
  public ImageUserAvatar getImageById(Long imageId) {
    return imageUserAvatarRepository.findById(imageId).orElseThrow(() -> new ImageNotFoundException("ImageUserAvatar not found"));
  }

  @Override
  @Transactional
  public ImageUserAvatar uploadAvatar(ImageUploadRequestDto requestDto, Users user) {
    try {
      validateUploadRequest(requestDto);

      String imageName = requestDto.getFileName();
      String imageUrl = cloudinaryService.uploadFile(requestDto.getFile(), "eventastic/" + user.getId().toString());

      ImageUserAvatar imageUserAvatar = new ImageUserAvatar();
      imageUserAvatar.setImageName(imageName);
      imageUserAvatar.setImageUrl(imageUrl);
      imageUserAvatar.setUser(user);
      if (imageUserAvatar.getImageUrl() == null) {
        // Handle error appropriately
        return null;
      }
      return imageUserAvatarRepository.save(imageUserAvatar);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  @Override
  @Transactional
  public ImageEvent uploadEventImage(ImageUploadRequestDto requestDto, Event event) {
    try {
      validateUploadRequest(requestDto);

      String imageName = requestDto.getFileName();
      String imageUrl = cloudinaryService.uploadFile(requestDto.getFile(), "eventastic/" + event.getId().toString());

      ImageEvent imageEvent = new ImageEvent();
      imageEvent.setImageName(imageName);
      imageEvent.setImageUrl(imageUrl);
      imageEvent.setEvent(event);
      if (imageEvent.getImageUrl() == null) {
        // Handle error appropriately
        return null;
      }
      return imageEventRepository.save(imageEvent);
    } catch (Exception e) {
      log.error(e.getMessage());
      return null;
    }
  }

  private void validateUploadRequest(ImageUploadRequestDto requestDto) {
    if (requestDto.getFileName().isEmpty() || requestDto.getFile().isEmpty()) {
      throw new IllegalArgumentException("Invalid upload request");
    }
  }

}
