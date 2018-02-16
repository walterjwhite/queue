package com.walterjwhite.job.api.enumeration;

public enum JobExecutionState {
  Pending, // pending pick-up by job execution service
  Scheduled, // picked up by job execution service
  Completed, // completed execution without error
  Exception, // completed execution with error
  Cancelled; // cancelled / aborted by user
}
