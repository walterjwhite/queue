package com.walterjwhite.queue.jpa.query;

import com.walterjwhite.datastore.query.AbstractQuery;
import com.walterjwhite.queue.api.enumeration.ActionType;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import com.walterjwhite.queue.api.enumeration.QueueState;
import com.walterjwhite.queue.api.model.QueuedJob;
import java.util.List;
import lombok.Getter;

// 1. find all jobs with a queue state of queued and execution state of (queued, aborted, exception)
// 2. same as above, but for recurring jobs, also include (completed, cancelled)
@Getter
public class FindRecurringAssignableQueuedJobsQuery extends AbstractQuery<QueuedJob, List> {
  protected final QueueState queueState = QueueState.Queued;
  protected final ExecutionState[] executionStates =
      ActionType.RecurringAssign.getSupportedExecutionStates();

  public FindRecurringAssignableQueuedJobsQuery(int offset, int recordCount) {
    super(offset, recordCount, QueuedJob.class, List.class, false);
  }

  public FindRecurringAssignableQueuedJobsQuery() {
    this(0, -1);
  }
}
