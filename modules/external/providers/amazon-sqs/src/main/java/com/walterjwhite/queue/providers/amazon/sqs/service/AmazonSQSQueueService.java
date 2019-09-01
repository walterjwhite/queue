package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.walterjwhite.job.external.queue.impl.service.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.service.EntityService;
import com.walterjwhite.queue.api.model.Queue;
import java.io.OutputStream;
import javax.inject.Inject;

// @RegisteredProvider(ExternalQueueService.class)
public class AmazonSQSQueueService extends AbstractExternalQueueService {
  protected final AmazonSQS amazonSQS;

  @Inject
  public AmazonSQSQueueService(EntityService entityService, AmazonSQS amazonSQS) {
    super(entityService);
    this.amazonSQS = amazonSQS;
  }

  @Override
  protected OutputStream getOutputStream(Queue queue, OutputStream outputStream) throws Exception {
    return new AmazonSQSOutputStream(amazonSQS, queue);
  }
}
