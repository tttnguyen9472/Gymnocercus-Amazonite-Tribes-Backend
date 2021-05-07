package com.greenfoxacademy.springwebapp.models.resources;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResourceDTO {
  private String type;
  private Integer amount;
  private Integer generation;
  private Long kingdomId;

  public ResourceDTO(Resource resource) {
    this.type = resource.getType();
    this.amount = resource.getAmount();
    this.generation = resource.getGeneration();
  }
}

