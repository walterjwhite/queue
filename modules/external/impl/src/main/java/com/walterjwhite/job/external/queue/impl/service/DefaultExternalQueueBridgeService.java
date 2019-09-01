package com.walterjwhite.job.external.queue.impl.service;

import com.walterjwhite.datastore.api.repository.Repository;
import com.walterjwhite.datastore.util.FieldRefresherUtil;
import com.walterjwhite.job.external.queue.api.ExternalQueueBridgeService;
import com.walterjwhite.queue.api.model.Queue;
import com.walterjwhite.queue.api.producer.InputStreamProducer;
import java.io.ByteArrayInputStream;
import javax.inject.Inject;
import javax.inject.Provider;

// import com.walterjwhite.queue.impl.JobBuilder;

public class DefaultExternalQueueBridgeService implements ExternalQueueBridgeService {
  protected final EntityService entityService;
  protected final Provider<Repository> entityManagerProvider;
  // protected final JPAEventQueueService jpaEventQueueService;
  //  protected final JobBuilder jobBuilder;
  protected final Provider<Repository> repositoryProvider;

  @Inject
  public DefaultExternalQueueBridgeService(
      EntityService entityService,
      Provider<Repository> entityManagerProvider,
      Provider<Repository> repositoryProvider) {
    super();
    this.entityService = entityService;
    this.repositoryProvider = repositoryProvider;
    this.entityManagerProvider = entityManagerProvider;
  }

  @Override
  public void read(Queue queue, byte[] data) throws Exception {
    // upon persisting this entity, the jpa event listener will automatically queue all related jobs
    repositoryProvider
        .get()
        .create(
            FieldRefresherUtil.refresh(
                entityManagerProvider.get(), entityService.read(new ByteArrayInputStream(data))));
  }

  @Override
  public void read(Queue queue, InputStreamProducer inputStreamProducer) throws Exception {
    // upon persisting this entity, the jpa event listener will automatically queue all related jobs
    repositoryProvider
        .get()
        .create(
            FieldRefresherUtil.refresh(
                entityManagerProvider.get(), entityService.read(inputStreamProducer.get())));
  }

  @Override
  public void readRaw(Queue queue, byte[] data) throws Exception {
    // upon persisting this entity, the jpa event listener will automatically queue all related jobs
    repositoryProvider
        .get()
        .create(
            FieldRefresherUtil.refresh(
                entityManagerProvider.get(), entityService.read(new ByteArrayInputStream(data))));
  }

  @Override
  public void readRaw(Queue queue, InputStreamProducer inputStreamProducer) throws Exception {
    // upon persisting this entity, the jpa event listener will automatically queue all related jobs
    repositoryProvider
        .get()
        .create(
            FieldRefresherUtil.refresh(
                entityManagerProvider.get(), entityService.read(inputStreamProducer.get())));
  }
}
