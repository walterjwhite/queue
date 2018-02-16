package com.walterjwhite.job.external.queue.impl;

import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import com.walterjwhite.job.impl.JobBuilder;
import com.walterjwhite.queue.api.producer.InputStreamProducer;
import com.walterjwhite.queue.api.service.QueueService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultExternalQueueBridgeService implements ExternalQueueBridgeService {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(DefaultExternalQueueBridgeService.class);

  protected final EntityService entityService;
  protected final Provider<QueueService> queueServiceProvider;
  protected final Provider<EntityManager> entityManagerProvider;
  protected final JobBuilder jobBuilder;

  @Inject
  public DefaultExternalQueueBridgeService(
      EntityService entityService,
      Provider<QueueService> queueServiceProvider,
      Provider<EntityManager> entityManagerProvider,
      JobBuilder jobBuilder) {
    super();
    this.entityService = entityService;
    this.queueServiceProvider = queueServiceProvider;
    this.entityManagerProvider = entityManagerProvider;
    this.jobBuilder = jobBuilder;
  }

  @Override
  public void read(Queue queue, byte[] data) throws Exception {
    queueServiceProvider
        .get()
        .queue(
            jobBuilder.build(
                queue, FieldRefresher.refresh(entityManagerProvider, entityService.read(data))));
  }

  @Override
  public void read(Queue queue, InputStreamProducer inputStreamProducer) throws Exception {
    queueServiceProvider
        .get()
        .queue(
            jobBuilder.build(
                queue,
                FieldRefresher.refresh(
                    entityManagerProvider,
                    entityService.read(IOUtils.toByteArray(inputStreamProducer.get())))));
  }

  @Override
  public void readRaw(Queue queue, byte[] data) throws Exception {
    queueServiceProvider
        .get()
        .queue(
            jobBuilder.build(
                queue, FieldRefresher.refresh(entityManagerProvider, entityService.readRaw(data))));
  }

  @Override
  public void readRaw(Queue queue, InputStreamProducer inputStreamProducer) throws Exception {
    queueServiceProvider
        .get()
        .queue(
            jobBuilder.build(
                queue,
                FieldRefresher.refresh(
                    entityManagerProvider,
                    entityService.readRaw(IOUtils.toByteArray(inputStreamProducer.get())))));
  }
}
