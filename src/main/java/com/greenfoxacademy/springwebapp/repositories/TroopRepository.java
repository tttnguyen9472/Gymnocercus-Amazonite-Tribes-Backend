package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.troops.Troop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TroopRepository extends CrudRepository<Troop, Long> {
  List<Troop> findTroopsByKingdom(Kingdom kingdom);
}
