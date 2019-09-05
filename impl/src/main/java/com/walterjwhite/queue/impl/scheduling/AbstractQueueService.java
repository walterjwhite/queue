package com.walterjwhite.queue.impl.scheduling;

import com.walterjwhite.queue.api.enumeration.ActionType;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import com.walterjwhite.queue.api.enumeration.QueueState;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.JobExecution;
import com.walterjwhite.queue.api.model.Worker;
import com.walterjwhite.queue.api.service.JobWorkerService;
import com.walterjwhite.queue.api.service.QueueService;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractQueueService implements QueueService {
  protected final JobWorkerService jobWorkerService;
  protected final Worker worker;

  @Transactional
  @Override
  public <QueuedType extends AbstractQueued> QueuedType cancel(QueuedType queued) {
    queued = refresh(queued);

    if (queued.getQueueState().isCancellable()) {
      queued.setQueueState(QueueState.Cancelled);

      jobWorkerService.cancel(queued);

      return update(queued);
    }

    throw new JobCancellationException(queued.getQueueState());
  }

  @Override
  public JobExecution cancel(JobExecution jobExecution) {
    jobExecution = refresh(jobExecution);

    if (ActionType.Cancel.isSupported(jobExecution.getExecutionState())) {
      jobExecution.setExecutionState(ExecutionState.Cancelled);
      jobWorkerService.cancel(jobExecution);

      return update(jobExecution);
    }

    throw new JobCancellationException(jobExecution.getExecutionState());
  }

  @Override
  public <QueuedType extends AbstractQueued> QueuedType queue(QueuedType queued) {
    if (queued.getId() == null) {
      queued.setOriginatingWorker(worker);
      create(queued);
    }

    // schedule the job with our local worker first
    jobWorkerService.queue(queued);

    return queued;
  }

  // Fetches the latest snapshot for this entity
  protected abstract <QueuedType extends AbstractQueued> QueuedType refresh(QueuedType queued);

  protected abstract JobExecution refresh(JobExecution jobExecution);

  protected abstract <QueuedType extends AbstractQueued> QueuedType create(QueuedType queued);

  // protected abstract <QueuedType extends AbstractQueued> QueuedType update(QueuedType queued);

  protected abstract JobExecution update(JobExecution jobExecution);
}
