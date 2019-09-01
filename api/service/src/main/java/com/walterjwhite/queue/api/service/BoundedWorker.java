package com.walterjwhite.queue.api.service;

import java.util.concurrent.Executor;
import java.util.concurrent.Semaphore;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BoundedWorker {
  protected final Executor executor;
  protected final Semaphore semaphore;

  public BoundedWorker(Executor executor, final int queueSize) {
    this(executor, new Semaphore(queueSize));
  }

  public void submit(final Runnable runnable) throws InterruptedException {
    semaphore.acquire();
    executor.execute(
        () -> {
          try {
            runnable.run();
          } finally {
            semaphore.release();
          }
        });
  }
}
