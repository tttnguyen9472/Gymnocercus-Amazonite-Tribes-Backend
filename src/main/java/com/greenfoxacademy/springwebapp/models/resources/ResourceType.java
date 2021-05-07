package com.greenfoxacademy.springwebapp.models.resources;

public enum ResourceType {
  FOOD {
    public String toString() {
      return "Food";
    }
  },
  GOLD {
    public String toString() {
      return "Gold";
    }
  }
}
