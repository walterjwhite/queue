package com.walterjwhite.queue.providers.amazon.sqs.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.google.inject.AbstractModule;
import com.walterjwhite.amazon.AmazonModule;
import com.walterjwhite.job.external.queue.api.ExternalQueueService;
import com.walterjwhite.job.external.queue.impl.ExternalQueueModule;
import com.walterjwhite.queue.providers.amazon.sqs.service.provider.AmazonSQSProvider;

public class AmazonSQSQueueModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ExternalQueueService.class).to(AmazonSQSQueueService.class);
    bind(AmazonSQS.class).toProvider(AmazonSQSProvider.class);

    bind(AsynchronousAmazonSQSQueueReaderService.class);

    install(new AmazonModule());
    install(new ExternalQueueModule());
  }
}
