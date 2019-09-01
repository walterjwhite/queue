package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class GoogleCloudPubSubMessageOutputStream extends ByteArrayOutputStream {
  protected final Publisher publisher;

  public GoogleCloudPubSubMessageOutputStream(Publisher publisher) {
    this.publisher = publisher;
  }

  public void flush() throws IOException {
    super.flush();

    doWrite();
  }

  protected void confirmDeliveryOfMessages(List<ApiFuture<String>> messageIdFutures)
      throws ExecutionException, InterruptedException {
    for (final String messageId : ApiFutures.allAsList(messageIdFutures).get()) {
      confirmDeliveryOfMessage(messageId);
    }
  }

  protected void confirmDeliveryOfMessage(final String messageId) {}

  protected void doWrite() {
    try {
      List<ApiFuture<String>> messageIdFutures = new ArrayList<>();

      try {
        // work with streams directly
        ByteString messageData = ByteString.copyFrom(toByteArray());
        PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(messageData).build();

        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        messageIdFutures.add(messageIdFuture);
      } finally {
        confirmDeliveryOfMessages(messageIdFutures);
        publisher.shutdown();
      }
    } catch (Exception e) {
      handleErrorPublishing(e);
    }
  }

  protected void handleErrorPublishing(Exception e) {
    throw (new RuntimeException("Error publishing message", e));
  }
}
