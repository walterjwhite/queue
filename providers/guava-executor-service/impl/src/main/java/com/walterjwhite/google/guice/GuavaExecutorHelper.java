// package com.walterjwhite.google.guice;
//
// import java.util.ArrayList;
// import java.util.List;
// import java.util.concurrent.Callable;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.Future;
//
// public class GuavaExecutorHelper {
//  private GuavaExecutorHelper() {
//    super();
//  }
//
//  public static List<Future> submit(
//      Class<? extends Callable> callableClass, final int numberOfInstances) {
//    final ExecutorService executorService =
//        GuiceHelper.getGuiceApplicationInjector().getInstance(ExecutorService.class);
//    List<Future> futures = new ArrayList<>();
//
//    for (int i = 0; i < numberOfInstances; i++) {
//      Callable callableInstance =
// GuiceHelper.getGuiceApplicationInjector().getInstance(callableClass);
//      futures.add(executorService.submit(callableInstance));
//    }
//
//    return (futures);
//  }
//
//  public static Future submit(Class<? extends Callable> callableClass) {
//    final ExecutorService executorService =
//        GuiceHelper.getGuiceApplicationInjector().getInstance(ExecutorService.class);
//
//    Callable callableInstance =
// GuiceHelper.getGuiceApplicationInjector().getInstance(callableClass);
//    return executorService.submit(callableInstance);
//  }
// }
