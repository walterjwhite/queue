package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.Base64;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.impl.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.EntityService;
import javax.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// @RegisteredProvider(ExternalQueueService.class)
public class AmazonSQSQueueService extends AbstractExternalQueueService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AmazonSQSQueueService.class);
  protected final AmazonSQS amazonSQS;

  @Inject
  public AmazonSQSQueueService(EntityService entityService, AmazonSQS amazonSQS) {
    super(entityService);
    this.amazonSQS = amazonSQS;
  }

  // TODO: make a generic superclass to automatically handle base64 conversion
  @Override
  protected void doWrite(Queue queue, byte[] data) {
    final SendMessageRequest sendMessageRequest = new SendMessageRequest();
    final String messageBody = Base64.encodeAsString(data);
    LOGGER.info("message:" + messageBody);
    sendMessageRequest.setMessageBody(messageBody);

    sendMessageRequest.setQueueUrl(AmazonSQSUtils.getQueueUrl(amazonSQS, queue));

    // Set the message group ID (required for fifo queues)
    if (AmazonSQSUtils.isFifo(queue)) {
      sendMessageRequest
          .getMessageAttributes()
          .put("JMSXGroupID", new MessageAttributeValue().withStringValue("Default"));
    }

    amazonSQS.sendMessage(sendMessageRequest);
  }
}
