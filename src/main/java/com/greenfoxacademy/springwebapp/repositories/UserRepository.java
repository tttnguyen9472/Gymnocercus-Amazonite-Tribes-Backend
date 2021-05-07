package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.models.users.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
  Optional<User> findUserByUsername(String username);

  @Query(value = "SELECT username FROM user", nativeQuery = true)
  List<String> findAllUsername();
}