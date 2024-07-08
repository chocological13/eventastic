package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.entity.UserAuth;
import com.miniproject.eventastic.users.repository.UsersRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Data
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

  private final UsersRepository usersRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    UserAuth user = usersRepository
        .findByEmail(email)
        .map(UserAuth::new)
        .orElseThrow(() -> new UsernameNotFoundException("Email: " + email + " not found"));
    return user;
  }
}
