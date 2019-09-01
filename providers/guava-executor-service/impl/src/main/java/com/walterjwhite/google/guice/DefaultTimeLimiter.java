package com.walterjwhite.google.guice;

import com.google.common.util.concurrent.TimeLimiter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DefaultTimeLimiter implements TimeLimiter {
  @Override
  public <T> T newProxy(
      T target, Class<T> interfaceType, long timeoutDuration, TimeUnit timeoutUnit) {
    return null;
  }

  @Override
  public <T> T callWithTimeout(Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit)
      throws TimeoutException, InterruptedException, ExecutionException {
    return null;
  }

  @Override
  public <T> T callUninterruptiblyWithTimeout(
      Callable<T> callable, long timeoutDuration, TimeUnit timeoutUnit)
      throws TimeoutException, ExecutionException {
    return null;
  }

  @Override
  public void runWithTimeout(Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit)
      throws TimeoutException, InterruptedException {}

  @Override
  public void runUninterruptiblyWithTimeout(
      Runnable runnable, long timeoutDuration, TimeUnit timeoutUnit) throws TimeoutException {}
}
