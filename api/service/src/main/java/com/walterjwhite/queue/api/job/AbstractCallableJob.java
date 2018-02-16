package com.walterjwhite.queue.api.job;

import com.walterjwhite.job.api.model.JobExecution;
import java.io.Serializable;

public abstract class AbstractCallableJob<EntityType extends Serializable, CallableReturnType>
    implements CallableJob<EntityType, CallableReturnType> {
  protected EntityType entity;
  protected JobExecution jobExecution;

  @Override
  public void onSuccess() {}

  @Override
  public void onError(Throwable thrown) {
    jobExecution.setRetryable(isRetryable(thrown));
  }

  protected abstract boolean isRetryable(Throwable thrown);

  @Override
  public void setJobExecution(JobExecution jobExecution) {
    this.jobExecution = jobExecution;
  }

  @Override
  public void setEntity(EntityType entity) {
    this.entity = entity;
  }

  @Override
  public EntityType getEntity() {
    return entity;
  }
}
