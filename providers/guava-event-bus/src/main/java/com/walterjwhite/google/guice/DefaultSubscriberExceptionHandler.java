package com.walterjwhite.google.guice;

import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

public class DefaultSubscriberExceptionHandler implements SubscriberExceptionHandler {
  @Override
  public void handleException(Throwable exception, SubscriberExceptionContext context) {}
}
