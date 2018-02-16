package com.walterjwhite.queue.api.job;

import com.walterjwhite.job.api.model.JobExecution;
import java.io.Serializable;
import java.util.concurrent.Callable;

/**
 * Extends callable functionality by providing callbacks for onSuccess and onError.
 *
 * @param <CallableReturnType>
 */
public interface CallableJob<EntityType extends Serializable, CallableReturnType>
    extends Callable<CallableReturnType> {
  /** Invoked if the run method returns successfully. */
  void onSuccess();

  /** Invoked if the run method throws an exception. */
  void onError(Throwable thrown);

  /** Sets job context. */
  void setJobExecution(JobExecution jobExecution);

  void setEntity(EntityType entity);

  EntityType getEntity();
}
