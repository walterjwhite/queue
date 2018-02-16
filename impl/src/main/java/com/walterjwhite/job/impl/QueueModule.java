package com.walterjwhite.job.impl;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;

import com.google.inject.AbstractModule;
import com.walterjwhite.datastore.criteria.EntityReferenceRepository;
import com.walterjwhite.datastore.criteria.EntityTypeRepository;
import com.walterjwhite.google.guice.executor.GuavaExecutorServiceModule;
import com.walterjwhite.job.impl.annotation.MonitorJob;
import com.walterjwhite.job.impl.helper.JobExecutionEnvironmentManager;
import com.walterjwhite.job.impl.interceptor.JobMonitorInterceptor;
import com.walterjwhite.job.impl.repository.*;
import com.walterjwhite.job.impl.service.persistent.DefaultQueueService;
import com.walterjwhite.queue.api.service.QueueService;

public class QueueModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(QueueService.class).to(DefaultQueueService.class);

    // bind repositories
    bind(EntityJobExecutorRepository.class);
    bind(EntityObjectRepository.class);
    bind(EntityReferenceRepository.class);
    bind(EntityTypeRepository.class);
    bind(JobExecutorRepository.class);
    bind(JobRepository.class);
    bind(JobExecutionRepository.class);
    bind(ScheduleRepository.class);

    bind(JobExecutionEnvironmentManager.class);

    install(new GuavaExecutorServiceModule());

    buildInterceptor();
  }

  protected void buildInterceptor() {
    JobMonitorInterceptor jobMonitorInterceptor =
        new JobMonitorInterceptor(
            getProvider(QueueService.class), getProvider(JobExecutionRepository.class));

    requestInjection(jobMonitorInterceptor);

    // register the interceptor to persist commands
    bindInterceptor(any(), annotatedWith(MonitorJob.class), jobMonitorInterceptor);
  }
}
