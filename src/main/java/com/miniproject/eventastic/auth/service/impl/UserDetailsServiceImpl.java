package com.miniproject.eventastic.auth.service.impl;

import com.miniproject.eventastic.auth.entity.UserAuth;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import java.util.Optional;
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
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
//    UserAuth user = usersRepository
//        .findByEmail(usernameOrEmail)
//        .map(UserAuth::new)
//
//        .orElseGet(() -> usersRepository.findByUsername(usernameOrEmail))
//        .map(UserAuth::new)
//        .orElseThrow(() -> new UsernameNotFoundException("User with username or email: " + usernameOrEmail + " not found"));
//    return user;
    Optional<Users> userOpt = usersRepository.findByUsername(usernameOrEmail);
    if (userOpt.isEmpty()) {
      userOpt = usersRepository.findByEmail(usernameOrEmail);
    }
    Users user = userOpt.orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail));

    return new UserAuth(user);
  }
}
