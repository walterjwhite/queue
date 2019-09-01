package com.walterjwhite.datastore.persistence.events;

import com.walterjwhite.infrastructure.datastore.PersistenceEventHandlerInstance;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Aspect;

@Aspect
public class OnCreateAspect {
  @Before(
      "within(com.walterjwhite.datastore.api.repository.Repository+) && execution(* create(..))")
  public void onBeforeCreate(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
    new PersistenceEventHandlerInstance(proceedingJoinPoint, EventActionType.Create).call();
  }
}
