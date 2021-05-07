package com.greenfoxacademy.springwebapp.models.resources;

import org.springframework.stereotype.Service;

@Service
public class ResourceFactory {

  public Resource createResource(ResourceType resourceType) {
    if (resourceType.equals(ResourceType.GOLD)) {
      return new Gold(1000, 0);
    } else if (resourceType.equals(ResourceType.FOOD)) {
      return new Food(1000, 0);
    }
    return null;
  }
}

