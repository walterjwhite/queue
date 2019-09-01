package com.walterjwhite.queue.jpa.query;

import com.walterjwhite.datastore.query.AbstractQuery;
import com.walterjwhite.queue.api.enumeration.ExecutionState;
import com.walterjwhite.queue.api.enumeration.QueueState;
import com.walterjwhite.queue.api.model.AbstractQueued;
import java.util.List;
import lombok.Getter;

@Getter
public class AbstractQueuedQuery extends AbstractQuery<AbstractQueued, List> {
  protected final QueueState queueState;
  protected final ExecutionState[] executionStates;

  public AbstractQueuedQuery(
      int offset, int recordCount, QueueState queueState, ExecutionState[] executionStates) {
    super(offset, recordCount, AbstractQueued.class, List.class, false);
    this.queueState = queueState;
    this.executionStates = executionStates;
  }

  public AbstractQueuedQuery(QueueState queueState, ExecutionState[] executionStates) {
    this(0, -1, queueState, executionStates);
  }
}
