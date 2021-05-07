package com.greenfoxacademy.springwebapp.services;

import com.greenfoxacademy.springwebapp.models.kingdoms.Location;
import com.greenfoxacademy.springwebapp.repositories.LocationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class LocationService {

  public static Integer mapSize = 1000;
  private final LocationRepository locationRepository;

  @Autowired
  public LocationService(LocationRepository locationRepository) {
    this.locationRepository = locationRepository;
  }

  public Location getNewLocation() {
    Random random = new Random();
    Location location = new Location(random.nextInt(mapSize), random.nextInt(mapSize));
    while (isLocationOccupied(location)) {
      location = new Location((location.getX() + 1) % mapSize, (location.getY() + 3) % mapSize);
    }
    return location;
  }

  private boolean isLocationOccupied(Location newLocation) {
    boolean isOccupied;
    List<Location> locationList = (List<Location>) locationRepository.findAll();
    isOccupied = locationList.contains(newLocation);
    return isOccupied;
  }

  public List<Location> getAllLocations() {
    return (List<Location>) locationRepository.findAll();
  }

}
