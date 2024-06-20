package com.miniproject.eventastic.users.service;

import com.miniproject.eventastic.users.entity.Users;
import java.util.List;

public interface UsersService {

  List<Users> getAllUsers();

  Users getByUsername(String username);

}
