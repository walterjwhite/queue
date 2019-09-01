package com.walterjwhite.queue.jpa.query;

import com.walterjwhite.queue.api.enumeration.ActionType;
import com.walterjwhite.queue.api.enumeration.QueueState;
import lombok.Getter;

// 1. check for timed out jobs where queue state is queued and execution state is stuck in (queued,
// assigned, scheduled, running > timeout)
@Getter
public class FindAbortedQueuedQuery extends AbstractQueuedQuery {
  public FindAbortedQueuedQuery(int offset, int recordCount) {
    super(offset, recordCount, QueueState.Queued, ActionType.Aborted.getSupportedExecutionStates());
  }

  public FindAbortedQueuedQuery() {
    this(0, -1);
  }
}
