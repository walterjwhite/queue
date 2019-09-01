package com.walterjwhite.queue.api.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum QueueState {
  Queued(true),
  Completed(false),
  Cancelled(false);

  private final boolean cancellable;
}
