package com.miniproject.eventastic.users.service.impl;

import static com.miniproject.eventastic.users.entity.dto.ProfileUpdateRequestDTO.profileUpdateRequestDTOtoUsers;

import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

  @Override
  public Users getByEmail(String email) {
    Optional<Users> usersOptional = usersRepository.findByEmail(email);
    return usersOptional.orElse(null);
  }

  @Override
  public Users getById(Long id) {
    Optional<Users> usersOptional = usersRepository.findById(id);
    return usersOptional.orElse(null);
  }

  @Override
  public void save(Users users) {
    usersRepository.save(users);
  }

  @Override
  public void update(Long id, ProfileUpdateRequestDTO requestDto) {
    Optional<Users> usersOptional = usersRepository.findById(id);
    if (usersOptional.isPresent()) {
      Users existingUser = usersOptional.get();
      profileUpdateRequestDTOtoUsers(existingUser, requestDto);
    }
  }

}
