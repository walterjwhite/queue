package com.walterjwhite.queue.jpa.query;

import com.walterjwhite.queue.api.enumeration.ActionType;
import com.walterjwhite.queue.api.enumeration.QueueState;
import lombok.Getter;

// 1. find all jobs with a queue state of queued and execution state of (queued, aborted, exception)
// 2. same as above, but for recurring jobs, also include (completed, cancelled)
@Getter
public class FindAssignableQueuedJobsQuery extends AbstractQueuedQuery {
  public FindAssignableQueuedJobsQuery(int offset, int recordCount) {
    super(offset, recordCount, QueueState.Queued, ActionType.Assign.getSupportedExecutionStates());
  }

  public FindAssignableQueuedJobsQuery() {
    this(0, -1);
  }
}
