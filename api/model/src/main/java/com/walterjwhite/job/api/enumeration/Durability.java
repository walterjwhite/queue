package com.walterjwhite.job.api.enumeration;

public enum Durability {
  None("Fire and Forget"),
  Persistent("If job fails, try again until aborted");

  private final String description;

  Durability(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
