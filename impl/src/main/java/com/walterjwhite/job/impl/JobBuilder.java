package com.walterjwhite.job.impl;

import com.google.inject.persist.Transactional;
import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.datastore.api.model.entity.EntityReference;
import com.walterjwhite.datastore.criteria.EntityReferenceRepository;
import com.walterjwhite.datastore.criteria.EntityTypeRepository;
import com.walterjwhite.encryption.api.service.DigestService;
import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.JobExecutor;
import com.walterjwhite.job.api.model.Queue;
import com.walterjwhite.job.api.model.scheduling.NowSchedule;
import com.walterjwhite.job.impl.repository.EntityJobExecutorRepository;
import com.walterjwhite.job.impl.repository.EntityObjectRepository;
import com.walterjwhite.job.impl.repository.JobExecutorRepository;
import com.walterjwhite.job.impl.repository.JobRepository;
import com.walterjwhite.serialization.api.service.SerializationService;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JobBuilder {
  private static final Logger LOGGER = LoggerFactory.getLogger(JobBuilder.class);

  protected final Provider<EntityReferenceRepository> entityReferenceRepositoryProvider;
  protected final Provider<EntityManager> entityManagerProvider;
  protected final Provider<JobRepository> scheduleRepositoryProvider;
  protected final Provider<JobExecutorRepository> jobExecutorRepositoryProvider;
  protected final Provider<EntityTypeRepository> entityTypeRepositoryProvider;
  protected final Provider<EntityJobExecutorRepository> entityJobExecutorRepositoryProvider;
  protected final Provider<EntityReferenceRepository> resourceRepositoryProvider;
  protected final Provider<EntityObjectRepository> entityObjectRepositoryProvider;
  protected final SerializationService serializationService;
  protected final DigestService digestService;

  @Inject
  public JobBuilder(
      Provider<EntityReferenceRepository> entityReferenceRepositoryProvider,
      Provider<EntityManager> entityManagerProvider,
      Provider<JobRepository> scheduleRepositoryProvider,
      Provider<JobExecutorRepository> jobExecutorRepositoryProvider,
      Provider<EntityTypeRepository> entityTypeRepositoryProvider,
      Provider<EntityJobExecutorRepository> entityJobExecutorRepositoryProvider,
      Provider<EntityReferenceRepository> resourceRepositoryProvider,
      Provider<EntityObjectRepository> entityObjectRepositoryProvider,
      SerializationService serializationService,
      DigestService digestService) {
    this.entityReferenceRepositoryProvider = entityReferenceRepositoryProvider;
    this.entityManagerProvider = entityManagerProvider;
    this.scheduleRepositoryProvider = scheduleRepositoryProvider;
    this.jobExecutorRepositoryProvider = jobExecutorRepositoryProvider;
    this.entityTypeRepositoryProvider = entityTypeRepositoryProvider;
    this.entityJobExecutorRepositoryProvider = entityJobExecutorRepositoryProvider;
    this.resourceRepositoryProvider = resourceRepositoryProvider;
    this.entityObjectRepositoryProvider = entityObjectRepositoryProvider;
    this.serializationService = serializationService;
    this.digestService = digestService;
  }

  //    /**
  //     * If the job already exists, re-use it.
  //     * @param callableJobClass the callable class
  //     * @return
  //     */
  //    @Transactional
  //    public Job build(Class<? extends CallableJob> callableJobClass){
  //        JobExecutor jobExecutor =
  // jobExecutorRepositoryProvider.get().findOrCreate(callableJobClass);
  //
  //        try {
  //            Job pendingJob =
  // scheduleRepositoryProvider.get().findPendingJobByExecutor(jobExecutor);
  //            return (pendingJob);
  //        } catch (NoResultException e) {
  //            return (scheduleRepositoryProvider.get().merge(new Job(jobExecutor)));
  //        }
  //    }
  //
  //    @Transactional(ignore = {NoResultException.class})
  //    public Job queue(Class<? extends CallableJob> callableClass, AbstractEntity entityResource)
  // {
  //        LOGGER.info("queuing:" + callableClass + ":" + entityResource);
  //
  //        JobExecutor jobExecutor =
  // jobExecutorRepositoryProvider.get().findOrCreate(callableClass);
  //        EntityReference entityReference =
  // resourceRepositoryProvider.get().findOrCreate(entityResource);
  //
  //        try {
  //            Job pendingJob =
  //                    scheduleRepositoryProvider
  //                            .get()
  //                            .findPendingJobByExecutorAndResource(jobExecutor, entityReference);
  //            return (pendingJob);
  //        } catch (NoResultException nre) {
  //            try {
  //                return (scheduleRepositoryProvider.get().merge(new Job(jobExecutor,
  // entityReference)));
  //            } catch (Exception e) {
  //                LOGGER.error("Error queuing job:", e);
  //                throw (new RuntimeException("Error"));
  //            }
  //        }
  //    }

  //    @Transactional
  //    @Override
  //    public Job queue(
  //            Class<? extends CallableJob> callableClass,
  //            AbstractEntity entityResource,
  //            AbstractSchedule... schedules) {
  //        try {
  //            Job job =
  //                    new Job(
  //                            jobExecutorRepositoryProvider.get().findOrCreate(callableClass),
  //                            resourceRepositoryProvider.get().findOrCreate(entityResource),
  //                            schedules);
  //            return (scheduleRepositoryProvider.get().merge(job));
  //        } catch (Exception e) {
  //            LOGGER.error("error", e);
  //            throw (e);
  //        }
  //    }
  //
  //    @Transactional
  //    @Override
  //    public Job queue(Class<? extends CallableJob> callableClass, Serializable object) {
  //        // serialize an object to an entity object ...
  //        final String objectClassName = object.getClass().getName();
  //        try {
  //            final byte[] data = serializationService.serialize(object);
  //
  //            // TODO: findOrCreateEntityType ...
  //            EntityObject entityObject =
  //                    new EntityObject(
  //                            // this expects the class to extends abstract entity, need to make
  // it generic instead
  //                            entityTypeRepositoryProvider.get().findOrCreate(objectClassName),
  //                            digestService.compute(data),
  //                            data);
  //            entityObjectRepositoryProvider.get().merge(entityObject);
  //
  //            return (queue(callableClass, entityObject));
  //        } catch (Exception e) {
  //            LOGGER.error("Error during serialization", e);
  //            throw (new RuntimeException("Unable to queue job", e));
  //        }
  //    }
  //
  //
  //
  public Job build(AbstractEntity entityResource) {
    try {
      Job job = saveJob(entityReferenceRepositoryProvider.get().findOrCreate(entityResource));
      job.getSchedules().add(new NowSchedule());

      return job;
    } catch (Exception e) {
      LOGGER.error("error", e);
      throw (e);
    }
  }

  @Transactional
  protected Job saveJob(AbstractEntity entityResource) {
    JobExecutor jobExecutor =
        entityJobExecutorRepositoryProvider
            .get()
            .findByResourceType(
                entityTypeRepositoryProvider.get().findOrCreate(entityResource.getClass()));
    EntityReference entityReference = resourceRepositoryProvider.get().findOrCreate(entityResource);

    try {
      return (scheduleRepositoryProvider
          .get()
          .findJobByExecutorAndResource(jobExecutor, entityReference));
    } catch (NoResultException nre) {
      Job job = new Job(jobExecutor, entityReference, new NowSchedule());
      return (scheduleRepositoryProvider.get().persist(job));
    }

    //          NowSchedule schedule = new NowSchedule();
    //          Job job;
    //
    //          try {
    //              job =
    //                      scheduleRepositoryProvider
    //                              .get()
    //                              .findPendingJobByExecutorAndResourceAndScheduleAndQueue(
    //                                      jobExecutor, entityReference, schedule, null);
    //          } catch (NoResultException e) {
    //              job = new Job(jobExecutor, entityReference, schedule);
    //          }
    //          return (scheduleRepositoryProvider.get().merge(job));
  }

  //
  //    @Transactional
  //    @Override
  //    public Job queue(Queue queue, Class<? extends CallableJob> callableClass) {
  //        return (queue(callableClass));
  //        //    throw (new UnsupportedOperationException("Not yet implemented."));
  //    }
  //
  //    @Transactional
  //    @Override
  //    public Job queue(
  //            Queue queue, Class<? extends CallableJob> callableClass, AbstractEntity
  // entityResource) {
  //        return (queue(callableClass, entityResource));
  //        //    throw (new UnsupportedOperationException("Not yet implemented."));
  //    }
  //
  //    @Transactional
  //    @Override
  //    public Job queue(Queue queue, Class<? extends CallableJob> callableClass, Serializable
  // object) {
  //        return (queue(callableClass, object));
  //        //    throw (new UnsupportedOperationException("Not yet implemented."));
  //    }
  //
  //    @Transactional
  //    @Override
  //    public Job queue(
  //            Queue queue,
  //            Class<? extends CallableJob> callableClass,
  //            AbstractEntity entityResource,
  //            AbstractSchedule... schedules) {
  //        return (queue(callableClass, entityResource, schedules));
  //        //    throw (new UnsupportedOperationException("Not yet implemented."));
  //    }
  //
  @Transactional
  public Job build(Queue queue, AbstractEntity entityResource) {
    return (build(entityResource));
    //    throw (new UnsupportedOperationException("Not yet implemented."));
  }
}
