package com.walterjwhite.queue.api.service;

import com.walterjwhite.logging.annotation.LogStackTrace;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class WaitTillAllComplete {
  protected final ExecutorService executorService;
  protected final Set<Future> futures = new HashSet<>();

  public Future submit(final Runnable runnable) {
    final Future future = executorService.submit(runnable);
    futures.add(future);

    return future;
  }

  public void waitForAll() {
    for (final Future future : futures) {
      try {
        future.get();
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      } catch (ExecutionException e) {
        onExecutionException(e);
      }
    }
  }

  @LogStackTrace
  protected void onExecutionException(final ExecutionException e) {}
}
