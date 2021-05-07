package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KingdomRepository extends CrudRepository<Kingdom, Long> {
  @Query(value = "SELECT name FROM kingdom", nativeQuery = true)
  List<String> findAllKingdomname();

  Optional<Kingdom> findByLocation(Location location);

  int a;

}