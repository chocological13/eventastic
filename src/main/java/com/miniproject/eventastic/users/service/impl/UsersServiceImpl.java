package com.miniproject.eventastic.users.service.impl;

import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.userManagement.ProfileUpdateRequestDTO;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.repository.UsersRepository;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import java.util.Optional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Data
public class UsersServiceImpl implements UsersService {

  private final UsersRepository usersRepository;
  private final PasswordEncoder passwordEncoder;

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
  public void register(Users newUser, RegisterRequestDto requestDto) {
    RegisterRequestDto reqToUser = new RegisterRequestDto();
    reqToUser.toEntity(newUser, requestDto);
    newUser.setPassword(passwordEncoder.encode(requestDto.getPassword()));
    usersRepository.save(newUser);
  }

  @Override
  public void update(Long id, ProfileUpdateRequestDTO requestDto) {
    Optional<Users> usersOptional = usersRepository.findById(id);
    if (usersOptional.isPresent()) {
      Users existingUser = usersOptional.get();
      ProfileUpdateRequestDTO update = new ProfileUpdateRequestDTO();
      update.profileUpdateRequestDTOtoUsers(existingUser, requestDto);
      usersRepository.save(existingUser);
    }
  }

}
