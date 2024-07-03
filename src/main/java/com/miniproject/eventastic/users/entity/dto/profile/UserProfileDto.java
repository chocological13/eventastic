package com.miniproject.eventastic.users.entity.dto.profile;

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
  private String avatar;
  private String bio;
  private Date birthday;

  public UserProfileDto toDto(Users user) {
    UserProfileDto userProfileDto = new UserProfileDto();
    userProfileDto.setId(user.getId());
    userProfileDto.setUsername(user.getUsername());
    userProfileDto.setEmail(user.getEmail());
    userProfileDto.setFirstName(user.getFirstName());
    userProfileDto.setLastName(user.getLastName());
    userProfileDto.setAvatar(user.getAvatar());
    userProfileDto.setBio(user.getBio());
    userProfileDto.setBirthday(user.getBirthday());
    return userProfileDto;
  }

}
