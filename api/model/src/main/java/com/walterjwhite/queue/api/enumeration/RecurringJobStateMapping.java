package com.walterjwhite.queue.api.enumeration;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RecurringJobStateMapping {
  Queued(
      ExecutionState.Queued,
      new ExecutionState[] {
        ExecutionState.Aborted,
        ExecutionState.Cancelled,
        ExecutionState.Completed,
        ExecutionState.Exception
      });

  private final ExecutionState assumedJobState;
  private final ExecutionState[] actualJobStates;

  public static ExecutionState translate(ExecutionState sourceJobState) {
    for (RecurringJobStateMapping recurringJobStateMapping : values()) {
      if (Arrays.stream(recurringJobStateMapping.actualJobStates)
          .anyMatch(actualJobState -> sourceJobState.equals(actualJobState)))
        return recurringJobStateMapping.assumedJobState;
    }

    return sourceJobState;
  }
}
