package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.walterjwhite.queue.api.enumeration.QueueType;
import com.walterjwhite.queue.api.model.Queue;

public class AmazonSQSUtils {
  private AmazonSQSUtils() {}

  public static String getQueueName(final String queueName, final boolean isFifo) {
    if (isFifo) return (queueName + ".fifo");

    return (queueName);
  }

  public static String getQueueUrl(AmazonSQS amazonSQS, Queue queue) {
    return (amazonSQS.getQueueUrl(getQueueName(queue)).getQueueUrl());
  }

  public static String getQueueName(Queue queue) {
    if (isFifo(queue)) {
      return (queue.getName() + ".fifo");
    }

    return (queue.getName());
  }

  public static boolean isFifo(Queue queue) {
    return (QueueType.Self.equals(queue.getQueueType()));
  }
}
