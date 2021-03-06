package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import com.walterjwhite.queue.api.model.Queue;
import com.walterjwhite.queue.api.model.QueueMonitor;
import com.walterjwhite.queue.impl.DefaultInputStreamProducer;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import org.apache.commons.codec.binary.Base64;

/** Due to limitations with Amazon SQS, this is NOT truly async ... */
public class AsynchronousAmazonSQSQueueReaderService extends AbstractScheduledService {
  protected final QueueMonitor queueMonitor;
  protected final AmazonSQS amazonSQS;
  protected final ExternalQueueBridgeService externalQueueBridgeService;

  @Inject
  public AsynchronousAmazonSQSQueueReaderService(
      ExternalQueueBridgeService externalQueueBridgeService,
      QueueMonitor queueMonitor,
      AmazonSQS amazonSQS) {
    super();
    this.externalQueueBridgeService = externalQueueBridgeService;
    this.queueMonitor = queueMonitor;
    this.amazonSQS = amazonSQS;

    createQueues();
  }

  @Override
  protected void runOneIteration() throws Exception {
    for (Queue queue : queueMonitor.getQueues()) {
      readQueue(queue);
    }
  }

  protected void readQueue(Queue queue) throws Exception {
    //    try {
    for (final Message message :
        amazonSQS
            .receiveMessage(
                new ReceiveMessageRequest(AmazonSQSUtils.getQueueUrl(amazonSQS, queue))
                    .withMaxNumberOfMessages(10))
            .getMessages()) {

      externalQueueBridgeService.read(
          queue,
          new DefaultInputStreamProducer(
              new ByteArrayInputStream(Base64.decodeBase64(message.getBody()))));
    }
    //    } catch (Exception e) {
    //      LOGGER.error("error processing", e);
    //    }
  }

  @Override
  protected Scheduler scheduler() {
    return AbstractScheduledService.Scheduler.newFixedDelaySchedule(0, 1, TimeUnit.MINUTES);
  }

  protected void createQueues() {
    for (Queue queue : queueMonitor.getQueues()) {
      createQueue(queue);
    }
  }

  protected void createQueue(Queue queue) {
    CreateQueueRequest createQueueRequest =
        new CreateQueueRequest().withQueueName(AmazonSQSUtils.getQueueName(queue));

    handleFifo(queue, createQueueRequest);
    amazonSQS.createQueue(createQueueRequest);
  }

  protected void handleFifo(Queue queue, CreateQueueRequest createQueueRequest) {
    if (AmazonSQSUtils.isFifo(queue)) {
      Map<String, String> attributes = new HashMap<>();
      attributes.put("FifoQueue", "true");
      attributes.put("ContentBasedDeduplication", "true");

      createQueueRequest.withAttributes(attributes);
    }
  }
}
