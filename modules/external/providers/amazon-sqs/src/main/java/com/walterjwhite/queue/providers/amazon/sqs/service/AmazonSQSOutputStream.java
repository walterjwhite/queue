package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.util.Base64;
import com.walterjwhite.queue.api.model.Queue;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class AmazonSQSOutputStream extends ByteArrayOutputStream {
  protected final AmazonSQS amazonSQS;
  protected final Queue queue;

  public AmazonSQSOutputStream(AmazonSQS amazonSQS, Queue queue) {
    this.amazonSQS = amazonSQS;
    this.queue = queue;
  }

  public void flush() throws IOException {
    super.flush();

    doWrite();
  }

  protected void doWrite() {
    final SendMessageRequest sendMessageRequest = new SendMessageRequest();
    sendMessageRequest.setMessageBody(Base64.encodeAsString(toByteArray()));
    sendMessageRequest.setQueueUrl(AmazonSQSUtils.getQueueUrl(amazonSQS, queue));

    handleFifo(sendMessageRequest);

    amazonSQS.sendMessage(sendMessageRequest);
  }

  // Set the message group ID (required for fifo queues)
  protected void handleFifo(SendMessageRequest sendMessageRequest) {

    if (AmazonSQSUtils.isFifo(queue)) {
      sendMessageRequest
          .getMessageAttributes()
          .put("JMSXGroupID", new MessageAttributeValue().withStringValue("Default"));
    }
  }
}
