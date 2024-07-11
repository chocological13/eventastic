package com.miniproject.eventastic.image.service;

import com.miniproject.eventastic.image.entity.ImageEvent;
import com.miniproject.eventastic.image.entity.ImageUserAvatar;
import com.miniproject.eventastic.image.entity.dto.ImageUploadRequestDto;
import com.miniproject.eventastic.users.entity.Users;

public interface ImageService {

  ImageUserAvatar uploadAvatar(ImageUploadRequestDto imageUploadRequestDto, Users user);

  ImageUserAvatar getAvatarById(Long imageId);

  void saveAvatar(ImageUserAvatar imageUserAvatar);

  // event
  void saveEventImage(ImageEvent imageEvent);
//  ImageEvent getEventImageById(Long imageId);

  ImageEvent uploadEventImage(ImageUploadRequestDto imageUploadRequestDto, Users organizer);

  ImageEvent getEventImageById(Long imageId);
}
