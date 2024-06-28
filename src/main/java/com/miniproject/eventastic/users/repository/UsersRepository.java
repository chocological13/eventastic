package com.miniproject.eventastic.users.repository;

import com.miniproject.eventastic.users.entity.Users;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsersRepository extends JpaRepository<Users, Long> {

  Optional<Users> findByUsername(String username);
  Optional<Users> findByEmail(String email);
  Optional<Users> findByOwnedRefCode(String ownedRefCode);
}
