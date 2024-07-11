package com.miniproject.eventastic.users.entity.dto.profile;

import com.miniproject.eventastic.image.entity.dto.ImageUploadResponseDto;
import com.miniproject.eventastic.users.entity.Users;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileDto {

  private Long id;
  private String username;
  private String email;
  private boolean isOrganizer;
  private String fullName;
  private ImageUploadResponseDto avatar;
  private String bio;
  private Date birthday;
  private String ownedRefCode;

  public UserProfileDto(Users user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.isOrganizer = user.getIsOrganizer();
    this.fullName = user.getFullName();
    this.avatar = user.getAvatar() == null ? null : new ImageUploadResponseDto(user.getAvatar());
    this.bio = user.getBio() == null ? null : user.getBio();
    this.birthday = user.getBirthday() == null ? null : new Date(user.getBirthday().getTime());
    this.ownedRefCode = user.getOwnedRefCode();
  }

  public UserProfileDto toDto(Users user) {
    return new UserProfileDto(user);
  }

}
