package com.greenfoxacademy.springwebapp.repositories;

import com.greenfoxacademy.springwebapp.models.kingdoms.Kingdom;
import com.greenfoxacademy.springwebapp.models.resources.Resource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends CrudRepository<Resource, Integer> {
  List<Resource> findResourcesByKingdom_id(Long kingdomId);

  List<Resource> findResourcesByKingdom(Kingdom kingdom);
}
