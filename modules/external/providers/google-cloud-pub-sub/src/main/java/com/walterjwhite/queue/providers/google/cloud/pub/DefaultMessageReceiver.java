package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.PubsubMessage;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import com.walterjwhite.queue.api.model.Queue;
import com.walterjwhite.queue.api.model.QueueMonitor;
import com.walterjwhite.queue.impl.DefaultInputStreamProducer;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.codec.binary.Base64;

@Data
@ToString(doNotUseGetters = true)
public class DefaultMessageReceiver implements MessageReceiver {
  protected final ExternalQueueBridgeService externalQueueBridgeService;

  protected Queue queue;

  @Inject
  public DefaultMessageReceiver(
      ExternalQueueBridgeService externalQueueBridgeService, QueueMonitor queueMonitor) {
    this.externalQueueBridgeService = externalQueueBridgeService;
  }

  @Override
  public void receiveMessage(PubsubMessage message, AckReplyConsumer consumer) {
    try {
      externalQueueBridgeService.read(
          queue,
          new DefaultInputStreamProducer(
              new ByteArrayInputStream(Base64.decodeBase64(message.getData().toByteArray()))));
    } catch (Exception e) {
      throw new RuntimeException("Error processing message", e);
    }

    consumer.ack();
  }
}
