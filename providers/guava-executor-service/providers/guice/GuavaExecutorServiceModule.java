package com.walterjwhite.google.guice.executor;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.ListeningScheduledExecutorService;
import com.google.inject.AbstractModule;
import com.walterjwhite.google.guice.executor.annotation.ExecutorManagementServiceOnly;
import com.walterjwhite.google.guice.executor.annotation.ExecutorServiceOnly;
import com.walterjwhite.google.guice.executor.provider.ExecutorServiceProvider;
import java.util.concurrent.ExecutorService;
import javax.inject.Singleton;

/** TODO: 1. support configuring # of threads 2. support configuring executor service type */
public class GuavaExecutorServiceModule extends AbstractModule {

  @Override
  protected void configure() {
    // https://github.com/google/guava/wiki/ListenableFutureExplained
    bind(ListeningScheduledExecutorService.class)
        .annotatedWith(ExecutorServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);
    bind(ListeningExecutorService.class)
        .annotatedWith(ExecutorServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);
    bind(ExecutorService.class)
        .annotatedWith(ExecutorServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);

    bind(ListeningScheduledExecutorService.class)
        .annotatedWith(ExecutorManagementServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);
    bind(ListeningExecutorService.class)
        .annotatedWith(ExecutorManagementServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);
    bind(ExecutorService.class)
        .annotatedWith(ExecutorManagementServiceOnly.class)
        .toProvider(ExecutorServiceProvider.class)
        .in(Singleton.class);
  }
}
