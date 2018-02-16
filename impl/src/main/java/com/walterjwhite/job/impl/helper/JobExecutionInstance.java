package com.walterjwhite.job.impl.helper;

import com.google.common.util.concurrent.ListenableFuture;
import com.walterjwhite.job.api.model.JobExecution;

public class JobExecutionInstance {
  protected final JobExecution jobExecution;
  protected final ListenableFuture future;

  public JobExecutionInstance(JobExecution jobExecution, ListenableFuture future) {
    this.jobExecution = jobExecution;
    this.future = future;
  }

  public JobExecution getJobExecution() {
    return jobExecution;
  }

  public ListenableFuture getFuture() {
    return future;
  }
}
