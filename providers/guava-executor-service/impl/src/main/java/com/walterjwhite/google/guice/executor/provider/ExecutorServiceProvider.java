package com.walterjwhite.google.guice.executor.provider;

import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.walterjwhite.job.impl.property.property.NumberOfExecutorServiceThreads;
import com.walterjwhite.property.impl.annotation.Property;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import javax.inject.Provider;

public class ExecutorServiceProvider implements Provider<ListeningScheduledExecutorService> {
  protected final ListeningScheduledExecutorService executorService;

  @Inject
  public ExecutorServiceProvider(
      @Property(NumberOfExecutorServiceThreads.class) int numberOfThreads) {
    super();
    executorService =
        MoreExecutors.listeningDecorator(Executors.newScheduledThreadPool(numberOfThreads));
    // this is needed unless we handle it differently (ie. wrap executor service as an idle service,
    // then let our handler call shutdown on the service)

    // TODO: remove this shutdown hook, instead implement shutdown-aware
    // Runtime.getRuntime().addShutdownHook(new ShutdownHook());
  }

  @Override
  public ListeningScheduledExecutorService get() {
    return executorService;
  }

  class ShutdownHook extends Thread {
    @Override
    public void run() {
      executorService.shutdownNow();
    }
  }
}
