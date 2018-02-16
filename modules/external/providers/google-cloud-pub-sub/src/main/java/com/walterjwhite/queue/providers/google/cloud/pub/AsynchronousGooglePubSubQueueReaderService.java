package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.common.util.concurrent.AbstractIdleService;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.SubscriptionName;
import com.walterjwhite.google.guice.property.property.Property;
import com.walterjwhite.google.property.GoogleCloudProjectId;
import com.walterjwhite.google.property.GoogleCloudSubscriptionId;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.api.model.QueueMonitor;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Due to limitations with Amazon SQS, this is NOT truly async ... */
public class AsynchronousGooglePubSubQueueReaderService extends AbstractIdleService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(AsynchronousGooglePubSubQueueReaderService.class);

  protected final QueueMonitor queueMonitor;
  protected final MessageReceiver messageReceiver;
  protected final ExternalQueueBridgeService externalQueueBridgeService;

  protected final String googleCloudProjectId;
  protected final String googleCloudSubscriptionId;

  protected List<Subscriber> subscribers = new ArrayList<>();

  @Inject
  public AsynchronousGooglePubSubQueueReaderService(
      ExternalQueueBridgeService externalQueueBridgeService,
      QueueMonitor queueMonitor,
      MessageReceiver messageReceiver,
      @Property(GoogleCloudProjectId.class) String googleCloudProjectId,
      @Property(GoogleCloudSubscriptionId.class) String googleCloudSubscriptionId) {
    super();
    this.externalQueueBridgeService = externalQueueBridgeService;
    this.queueMonitor = queueMonitor;
    this.messageReceiver = messageReceiver;

    this.googleCloudProjectId = googleCloudProjectId;
    this.googleCloudSubscriptionId = googleCloudSubscriptionId;
    createQueues();
  }

  protected void onMessageReceived(Queue queue, PubsubMessage message, AckReplyConsumer consumer) {
    try {
      externalQueueBridgeService.read(queue, Base64.decodeBase64(message.getData().toByteArray()));
    } catch (Exception e) {
      LOGGER.error("Error processing message", e);
      throw (new RuntimeException("Error processing message", e));
    }

    // handle incoming message, then ack/nack the received message
    LOGGER.info("Id : " + message.getMessageId());
    LOGGER.info("Data : " + message.getData().toStringUtf8());
    consumer.ack();
  }

  protected void createQueues() {
    for (Queue queue : queueMonitor.getQueues()) {
      createQueue(queue);
    }
  }

  protected void createQueue(Queue queue) {}

  @Override
  protected void startUp() throws Exception {
    if (!subscribers.isEmpty()) {
      throw (new IllegalStateException("Already initialized."));
    }

    for (Queue queue : queueMonitor.getQueues()) {
      SubscriptionName subscriptionName =
          SubscriptionName.create(googleCloudProjectId, googleCloudSubscriptionId);
      Subscriber subscriber =
          Subscriber.defaultBuilder(
                  subscriptionName,
                  (message, consumer) -> onMessageReceived(queue, message, consumer))
              .build();
      subscriber.startAsync();
      subscribers.add(subscriber);
    }
  }

  @Override
  protected void shutDown() throws Exception {
    // stop receiving messages
    if (subscribers != null && !subscribers.isEmpty()) {
      for (Subscriber subscriber : subscribers) subscriber.stopAsync();
    }
  }
}
