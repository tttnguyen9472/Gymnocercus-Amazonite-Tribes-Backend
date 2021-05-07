package com.greenfoxacademy.springwebapp.models.kingdoms;

import com.greenfoxacademy.springwebapp.models.buildings.BuildingDTO;
import com.greenfoxacademy.springwebapp.models.resources.ResourceDTO;
import com.greenfoxacademy.springwebapp.models.troops.TroopDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KingdomResponseDTO {

  private Long id;
  private String name;
  private List<TroopDTO> troops;
  private List<BuildingDTO> buildings;
  private List<ResourceDTO> resources;
  private String user;


}
