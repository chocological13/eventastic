package com.miniproject.eventastic.auth.entity;

import com.miniproject.eventastic.users.entity.Users;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@EqualsAndHashCode(callSuper = true)
@Data
@RequiredArgsConstructor
public class UserAuth extends Users implements UserDetails {

  private final Users user;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> authorities = new ArrayList<>();
    if (user.getUsername().equals("strwbry")) {
      authorities.add(() -> "ROLE_SUPERCAT");
    } else if (user.getIsOrganizer()) {
      authorities.add(() -> "ROLE_ORGANIZER");
    } else {
      authorities.add(() -> "ROLE_USER");
    }
    return authorities;
  }

  @Override
  public String getPassword() {
    return user.getPassword();
  }

  @Override
  public String getUsername() {
    return user.getUsername();
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
