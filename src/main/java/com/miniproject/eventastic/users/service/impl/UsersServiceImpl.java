package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import jakarta.persistence.Entity;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class UsersServiceImpl implements UsersService {

  private final UsersRepository usersRepository;

  @Override
  public List<Users> getAllUsers() {
    return usersRepository.findAll();
  }

  @Override
  public Users getByUsername(String username) {
    Optional<Users> usersOptional = usersRepository.findByUsername(username);
    return usersOptional.orElse(null);
  }
}
