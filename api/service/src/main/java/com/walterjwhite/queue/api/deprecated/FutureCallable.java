// package com.walterjwhite.queue.api.job;
//
// import java.util.concurrent.*;
// import lombok.Getter;
//
// @Getter
// public class FutureCallable<ResultType> implements Future<ResultType>, QueuedCallable {
//  protected final AbstractRunnable callable;
//  protected final Future<ResultType> future;
//
//  public FutureCallable(AbstractRunnable callable, Future<ResultType> future) {
//    this.callable = callable;
//    this.future = future;
//  }
//
//  @Override
//  public boolean cancel(boolean b) {
//    return future.cancel(b);
//  }
//
//  @Override
//  public boolean isCancelled() {
//    return future.isCancelled();
//  }
//
//  @Override
//  public boolean isDone() {
//    return future.isDone();
//  }
//
//  @Override
//  public ResultType get() throws InterruptedException, ExecutionException {
//    return future.get();
//  }
//
//  @Override
//  public ResultType get(long l, TimeUnit timeUnit)
//      throws InterruptedException, ExecutionException, TimeoutException {
//    return future.get(l, timeUnit);
//  }
//
//  @Override
//  public AbstractRunnable getCallable() {
//    return callable;
//  }
// }
