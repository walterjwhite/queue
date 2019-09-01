package com.walterjwhite.google.guice.executor.provider;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.RateLimiter;
import com.walterjwhite.job.impl.property.property.NumberOfExecutorServiceThreads;
import com.walterjwhite.property.impl.annotation.Property;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import javax.inject.Inject;

// TODO: use a blocking queue instead
@Deprecated
public class BlockingExecutorService {
  protected final ListeningScheduledExecutorService executorService;
  protected final int numberOfThreads;
  protected final Set<Future> futures = new HashSet<>();
  protected ThreadLocal<Integer> runningJobs = new ThreadLocal<>();

  //  private static final Limiter

  private static final RateLimiter rateLimiter = RateLimiter.create(1.0); // Max 1 call per sec

  @Inject
  public BlockingExecutorService(
      @Property(NumberOfExecutorServiceThreads.class) int numberOfThreads,
      ListeningScheduledExecutorService executorService) {
    super();
    this.executorService = executorService;
    this.numberOfThreads = numberOfThreads;

    this.runningJobs.set(Integer.valueOf(0));
  }

  public void runImmediatelyWhenFree(Callable job, Runnable... listeners) {
    if (rateLimiter.tryAcquire()) rateLimiter.acquire();

    //        ListenableScheduledFuture jobFuture = executorService.submit(queuedJob);
    //
    //        f.cancel();
    //
    //        for(Runnable listener:listeners)
    //            f.addListener(listener, executorService);
    //
    //        int nAvailableProcesses = getAvailableProcesses();
    //        if (nAvailableProcesses > 0) {
    //            nAvailableProcesses--;
    //            if (nAvailableProcesses <= 0) break;
    //        } else {
    //            LOGGER.info("no processes available");
    //        }
  }

  // TODO: put this code in the executor service project
  protected int getAvailableProcesses() {
    int available = numberOfThreads;

    //        executorService.
    final Iterator<Future> futureIterator = futures.iterator();
    while (futureIterator.hasNext()) {
      final Future future = futureIterator.next();
      if (future.isDone() || future.isCancelled()) futures.remove(future);
      else available--;
    }

    return (available);
  }
}
