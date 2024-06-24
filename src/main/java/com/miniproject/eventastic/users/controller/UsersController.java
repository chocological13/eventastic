package com.miniproject.eventastic.users.controller;

import com.miniproject.eventastic.responses.Response;
import com.miniproject.eventastic.users.entity.Users;
import com.miniproject.eventastic.users.entity.dto.register.RegisterRequestDto;
import com.miniproject.eventastic.users.service.UsersService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UsersController {

  private final UsersService usersService;

  // ! Get all
  @GetMapping
  public ResponseEntity<Response<List<Users>>> getAllUsers() {
    List<Users> users = usersService.getAllUsers();
    if (!users.isEmpty()) {
      return Response.successfulResponse(HttpStatus.FOUND.value(), "Displaying all users...", users);
    } else {
      return Response.failedResponse("There are no users to display");
    }
  }

  // helper to get users response
  public ResponseEntity<Response<Users>> getUserCheckResponse(Users user, String identifier, String value) {
    if (user == null) {
      return Response.failedResponse("There is no user with " + identifier + " " + value);
    } else {
      return Response.successfulResponse(HttpStatus.FOUND.value(), HttpStatus.FOUND.getReasonPhrase(), user);
    }
  }

  // ! Get by ID
  @GetMapping("?id=")
  public ResponseEntity<Response<Users>> getUserById(@RequestParam("id") Long id) {
    Users user = usersService.getById(id);
    return getUserCheckResponse(user, "id", " " + id);
  }

  // ! Get by Email
  @GetMapping("?email=")
  public ResponseEntity<Response<Users>> getUserByEmail(@RequestParam("email") String email) {
    Users user = usersService.getByEmail(email);
    return getUserCheckResponse(user, "email", " " + email);
  }

  // Get by Username
  @GetMapping("?username=")
  public ResponseEntity<Response<Users>> getUserByUsername(@RequestParam("username") String username) {
    Users user = usersService.getByUsername(username);
    return getUserCheckResponse(user, "username", " " + username);
  }

  /* !TODO:
   *  - simulate registration
   * - simulate edit profile*/

  // ! DELETE AFTER MOVED TO AUTHCONTROLLER
  @PostMapping("/register")
  public ResponseEntity<Response<Users>> registerUser(@RequestBody RegisterRequestDto requestDto) {
    Users newUser = new Users();
    usersService.register(newUser, requestDto);
    return Response.successfulResponse(HttpStatus.CREATED.value(), "Register successful!! :D", newUser);
  }

  // Region - USER PROFILE MANAGEMENT

}
