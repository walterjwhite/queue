package com.walterjwhite.queue.api.enumeration;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ExecutionState {
  Queued("Job queued by queue service (scheduler)"),
  Assigned("Job picked up by worker (prevents other workers from picking up the job)"),
  Scheduled("Job picked up by worker and scheduled for execution."),
  Running("Job is actively running"),
  Completed("Job completed execution without error"),
  Cancelled("Job cancelled by user"),
  Aborted("Job aborted by means of worker shutdown"),
  Exception("Job failed to complete normally");
  private final String description;
}
