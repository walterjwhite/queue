package com.walterjwhite.queue.impl.scheduling;

import lombok.Getter;

@Getter
public class JobCancellationException extends RuntimeException {
  protected final Enum jobStateEnum;

  public JobCancellationException(Enum jobStateEnum) {
    super("Job is unable to be cancelled as it is in: " + jobStateEnum.name());
    this.jobStateEnum = jobStateEnum;
  }
}
