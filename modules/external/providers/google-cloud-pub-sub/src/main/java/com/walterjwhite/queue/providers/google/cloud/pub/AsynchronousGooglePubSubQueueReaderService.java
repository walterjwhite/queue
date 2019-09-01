package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.walterjwhite.google.property.GoogleCloudProjectId;
import com.walterjwhite.google.property.GoogleCloudSubscriptionId;
import com.walterjwhite.infrastructure.inject.core.helper.ApplicationHelper;
import com.walterjwhite.infrastructure.inject.core.service.StartupAware;
import com.walterjwhite.interruptable.Interruptable;
import com.walterjwhite.interruptable.annotation.InterruptableService;
import com.walterjwhite.property.impl.annotation.Property;
import com.walterjwhite.queue.api.model.Queue;
import com.walterjwhite.queue.api.model.QueueMonitor;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Due to limitations with Amazon SQS, this is NOT truly async ... */
@InterruptableService
public class AsynchronousGooglePubSubQueueReaderService implements StartupAware, Interruptable {
  protected final MessageReceiver messageReceiver;

  protected final String googleCloudProjectId;
  protected final String googleCloudSubscriptionId;
  protected final QueueMonitor queueMonitor;

  protected List<Subscriber> subscribers = new ArrayList<>();

  @Inject
  public AsynchronousGooglePubSubQueueReaderService(
      MessageReceiver messageReceiver,
      @Property(GoogleCloudProjectId.class) String googleCloudProjectId,
      @Property(GoogleCloudSubscriptionId.class) String googleCloudSubscriptionId,
      QueueMonitor queueMonitor) {
    super();
    this.messageReceiver = messageReceiver;

    this.googleCloudProjectId = googleCloudProjectId;
    this.googleCloudSubscriptionId = googleCloudSubscriptionId;

    this.queueMonitor = queueMonitor;
  }

  protected void createQueues() {
    for (Queue queue : queueMonitor.getQueues()) {
      createQueue(queue);
    }
  }

  protected void createQueue(Queue queue) {}

  protected void setupMessageReceivers() {
    for (Queue queue : queueMonitor.getQueues()) {

      DefaultMessageReceiver messageReceiver =
          ApplicationHelper.getApplicationInstance()
              .getInjector()
              .getInstance(DefaultMessageReceiver.class);
      messageReceiver.setQueue(queue);

      Subscriber subscriber =
          Subscriber.newBuilder(
                  ProjectSubscriptionName.of(googleCloudProjectId, googleCloudSubscriptionId),
                  messageReceiver)
              .build();
      subscriber.startAsync();
      subscribers.add(subscriber);
    }
  }

  @Override
  public void interrupt() {
    // stop receiving messages
    if (subscribers != null && !subscribers.isEmpty()) {
      for (Subscriber subscriber : subscribers) subscriber.stopAsync();
    }
  }

  @Override
  public void onStartup() throws Exception {
    if (!subscribers.isEmpty()) {
      throw (new IllegalStateException("Already initialized."));
    }

    createQueues();
    setupMessageReceivers();
  }
}
