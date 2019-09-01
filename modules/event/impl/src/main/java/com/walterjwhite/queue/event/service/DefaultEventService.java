package com.walterjwhite.queue.event.service;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.queue.api.service.QueueService;
import com.walterjwhite.queue.event.annotation.SubscribeTo;
import com.walterjwhite.queue.event.enumeration.EventActionType;
import com.walterjwhite.queue.event.model.QueuedEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import org.reflections.Reflections;

// JPA events cannot be stored to the same JPA provider of the originating transaction ...
public class DefaultEventService implements EventService {
  protected final QueueService queueService;
  protected final QueueEventBuilder queueEventBuilder;
  protected final Reflections reflections;

  @Inject
  public DefaultEventService(
      QueueService queueService, QueueEventBuilder queueEventBuilder, Reflections reflections) {
    this.queueService = queueService;
    this.queueEventBuilder = queueEventBuilder;
    this.reflections = reflections;
  }

  // queue event (for immediate processing)
  @Override
  public Set<QueuedEvent> queue(AbstractEntity entity, EventActionType eventActionType) {
    // TODO: should I make an aspect here that prevents execution if the system is shutting down
    // it would be great to be system-wide
    // checkIfShutdown();

    final Set<QueuedEvent> queuedEvents = createJobs(entity, eventActionType);
    queuedEvents.forEach(queuedJob -> queueService.queue(queuedJob));

    // 1. put in future
    // 2. pass entity to create callable job

    return queuedEvents;
  }

  protected Set<QueuedEvent> createJobs(AbstractEntity entity, EventActionType eventActionType) {
    final Set<QueuedEvent> queuedJobs = new HashSet<>();

    // entity should be an EntityReferenceAction
    for (final Class jobSubscriberClass : getSubscribersTo(entity.getClass(), eventActionType)) {
      queuedJobs.add(queueEventBuilder.build(entity, jobSubscriberClass));
    }

    return queuedJobs;
  }

  protected Set<Class> getSubscribersTo(final Class eventClass, EventActionType eventActionType) {
    final Set<Class> subcriberClasses = new HashSet<>();
    for (final Class subscriberClass : reflections.getTypesAnnotatedWith(SubscribeTo.class)) {
      final SubscribeTo subscribeTo =
          (SubscribeTo) subscriberClass.getAnnotation(SubscribeTo.class);
      if (handlesEvent(eventClass, eventActionType, subscribeTo)) {
        subcriberClasses.add(subscriberClass);
      }
    }

    return subcriberClasses;
  }

  protected boolean handlesEvent(
      final Class eventClass,
      final EventActionType eventActionType,
      final SubscribeTo subscribeTo) {
    return subscribeTo.eventClass().isAssignableFrom(eventClass)
        && supportsEventAction(eventActionType, subscribeTo);
  }

  protected boolean supportsEventAction(
      final EventActionType eventActionType, final SubscribeTo subscribeTo) {
    return subscribeTo.eventAction() == null
        || Arrays.stream(subscribeTo.eventAction())
                .filter(eventAction -> eventActionType.equals(eventAction))
                .count()
            > 0;
  }
}
