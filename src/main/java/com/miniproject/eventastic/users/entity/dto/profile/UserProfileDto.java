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
  private String firstName;
  private String lastName;
  private ImageUploadResponseDto avatar;
  private String bio;
  private Date birthday;
  private String ownedRefCode;

  public UserProfileDto(Users user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.email = user.getEmail();
    this.isOrganizer = user.getIsOrganizer();
    this.firstName = user.getFirstName();
    this.lastName = user.getLastName();
    this.avatar = new ImageUploadResponseDto(user.getAvatar());
    this.bio = user.getBio();
    this.birthday = user.getBirthday();
    this.ownedRefCode = user.getOwnedRefCode();
  }

  public UserProfileDto toDto(Users user) {
    return new UserProfileDto(user);
  }

}
