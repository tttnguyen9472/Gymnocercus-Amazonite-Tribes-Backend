package com.greenfoxacademy.springwebapp.models.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponseDTO {
  private String status;
  private String error;
  private Long id;
  private String username;
  @JsonProperty(value = "kingdom_id")
  private Long kingdomId;

  public RegisterResponseDTO(Long id, String username, Long kingdomId) {
    this.id = id;
    this.username = username;
    this.kingdomId = kingdomId;
  }

  public RegisterResponseDTO(String error) {
    this.error = error;
    status = "error";
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof RegisterResponseDTO)) {
      return false;
    }
    RegisterResponseDTO that = (RegisterResponseDTO) o;
    return getId().equals(that.getId())
        && getUsername().equals(that.getUsername())
        && getKingdomId().equals(that.getKingdomId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getId(), getUsername(), getKingdomId());
  }
}
