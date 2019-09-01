package com.walterjwhite.queue.api.enumeration;

import lombok.Getter;

@Getter
public enum Durability {
  None("Fire and Forget"),
  Persistent("If queuedJob fails, try again until aborted");

  private final String description;

  Durability(String description) {
    this.description = description;
  }
}
