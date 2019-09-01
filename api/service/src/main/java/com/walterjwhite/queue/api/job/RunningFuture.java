package com.walterjwhite.queue.api.job;

import java.util.concurrent.Future;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class RunningFuture {
  protected final Future future;
  protected final int queuedId;
  protected final int executionId;
  protected final int runnableId;
}
