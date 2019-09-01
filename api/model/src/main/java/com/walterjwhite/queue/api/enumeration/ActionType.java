package com.walterjwhite.queue.api.enumeration;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ActionType {
  Cancel(
      "Job Executions in one of these states may be cancelled.",
      new ExecutionState[] {
        ExecutionState.Queued,
        ExecutionState.Assigned,
        ExecutionState.Scheduled,
        ExecutionState.Running
      }),
  Assign(
      "Job Executions in one of these states may be assigned to a worker",
      new ExecutionState[] {ExecutionState.Queued}),
  RecurringAssign(
      "Job Executions in one of these states may be assigned to a worker (for recurring jobs only)",
      new ExecutionState[] {
        ExecutionState.Completed,
        ExecutionState.Cancelled,
        ExecutionState.Aborted,
        ExecutionState.Exception
      }),
  Aborted(
      "Job Executions in one of these states may be aborted, provided the current time is > the timeoutDateTime for the execution",
      new ExecutionState[] {
        ExecutionState.Queued,
        ExecutionState.Assigned,
        ExecutionState.Scheduled,
        ExecutionState.Running
      });

  private final String description;
  private final ExecutionState[] supportedExecutionStates;

  public boolean isSupported(ExecutionState executionState) {
    return Arrays.stream(supportedExecutionStates)
            .filter(supportedExecutionState -> supportedExecutionState.equals(executionState))
            .count()
        > 0;
  }
}
