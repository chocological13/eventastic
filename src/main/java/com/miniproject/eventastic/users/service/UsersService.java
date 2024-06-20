package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.ProfileUpdateRequestDTO;
import java.util.List;
import org.springframework.http.ResponseEntity;

public interface UsersService {

  List<Users> getAllUsers();

  Users getByUsername(String username);

  Users getByEmail(String email);

  Users getById(Long id);

  void save(Users users);

  void update(Long id, ProfileUpdateRequestDTO requestDto);

}
