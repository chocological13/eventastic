package com.miniproject.eventastic.users.controller;

import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UsersController {

  private final UsersService usersService;

  @GetMapping
  public ResponseEntity<Response<List<Users>>> getAllUsers() {
    List<Users> users = usersService.getAllUsers();
    if (!users.isEmpty()) {
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying all users...", users);
    } else return Response.failedResponse("There are no users to display");
  }
}
