package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import com.walterjwhite.google.guice.property.property.Property;
import com.walterjwhite.google.property.GoogleCloudProjectId;
import com.walterjwhite.google.property.GoogleCloudSubscriptionId;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.impl.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.EntityService;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GoogleCloudPubSubMessageWriter extends AbstractExternalQueueService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(GoogleCloudPubSubMessageWriter.class);

  protected final CredentialsProvider credentialsProvider;
  protected final String googleCloudProjectId;
  protected final String googleCloudSubscriptionId;

  @Inject
  public GoogleCloudPubSubMessageWriter(
      EntityService entityService,
      CredentialsProvider credentialsProvider,
      @Property(GoogleCloudProjectId.class) String googleCloudProjectId,
      @Property(GoogleCloudSubscriptionId.class) String googleCloudSubscriptionId) {
    super(entityService);
    this.credentialsProvider = credentialsProvider;

    this.googleCloudProjectId = googleCloudProjectId;
    this.googleCloudSubscriptionId = googleCloudSubscriptionId;
  }

  @Override
  protected void doWrite(Queue queue, byte[] data) {
    try {
      final Publisher publisher = getPublisher();
      List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

      try {
        ByteString messageData = ByteString.copyFrom(data);
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(messageData).build();

        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        messageIdFutures.add(messageIdFuture);
      } finally {
        confirmDeliveryOfMessages(messageIdFutures);
        publisher.shutdown();
      }
    } catch (Exception e) {
      LOGGER.error("Error publishing message", e);
      throw (new RuntimeException("Error publishing message", e));
    }
  }

  protected Publisher getPublisher() throws IOException {
    final TopicName topicName = TopicName.create(googleCloudProjectId, googleCloudSubscriptionId);
    return (Publisher.defaultBuilder(topicName)
        .setCredentialsProvider(credentialsProvider)
        .build());
  }

  protected void confirmDeliveryOfMessages(List<ApiFuture<String>> messageIdFutures)
      throws ExecutionException, InterruptedException {
    for (final String messageId : ApiFutures.allAsList(messageIdFutures).get()) {
      LOGGER.info("published with message ID: " + messageId);
    }
  }
}
