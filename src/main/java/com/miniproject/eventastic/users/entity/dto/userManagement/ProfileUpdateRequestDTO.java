package com.miniproject.eventastic.users.entity.dto.userManagement;

import com.miniproject.eventastic.users.entity.Users;
import java.time.LocalDate;
import java.util.Optional;
import lombok.Data;

@Data
public class ProfileUpdateRequestDTO {

  private String firstName;
  private String lastName;
  private String avatar;
  private String bio;
  private LocalDate birthday;

  public static void profileUpdateRequestDTOtoUsers(Users user, ProfileUpdateRequestDTO requestDto) {
    Optional.ofNullable(requestDto.getFirstName()).ifPresent(user::setFirstName);
    Optional.ofNullable(requestDto.getLastName()).ifPresent(user::setLastName);
    Optional.ofNullable(requestDto.getAvatar()).ifPresent(user::setAvatar);
    Optional.ofNullable(requestDto.getBio()).ifPresent(user::setBio);
    Optional.ofNullable(requestDto.getBirthday()).ifPresent(user::setBirthday);
  }

}
