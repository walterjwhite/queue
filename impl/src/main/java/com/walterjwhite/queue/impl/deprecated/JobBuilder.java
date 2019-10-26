// package com.walterjwhite.queue.impl;
//
// import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
// import com.walterjwhite.datastore.api.model.entity.EntityReference;
// import com.walterjwhite.datastore.api.repository.Repository;
// import
// com.walterjwhite.datastore.query.entityReference.FindEntityReferenceByTypeAndIdQueryConfiguration;
// import
// com.walterjwhite.datastore.query.entityType.FindEntityTypeByEntityClassTypeQueryConfiguration;
// import com.walterjwhite.encryption.api.service.DigestService;
// import com.walterjwhite.queue.api.queuedJob.AbstractJobRunnable;
// import com.walterjwhite.queue.api.model.EntityObject;
// import com.walterjwhite.queue.api.model.QueuedJob;
// import com.walterjwhite.queue.api.model.JobExecutor;
// import com.walterjwhite.queue.api.model.Queue;
// import com.walterjwhite.queue.api.model.scheduling.AbstractSchedule;
// import com.walterjwhite.queue.api.model.scheduling.NowSchedule;
// import com.walterjwhite.queue.impl.query.queuedJob.executor.FindJobExecutorByJobClassName;
// import com.walterjwhite.serialization.api.service.SerializationService;
//
// import javax.inject.Inject;
// import javax.inject.Provider;
// import javax.jdo.annotations.Transactional;
// import java.io.Serializable;
//
// public class JobBuilder {
//
//    protected final Provider<Repository> repositoryProvider;
//    protected final SerializationService serializationService;
//    protected final DigestService digestService;
//
//    @Inject
//    public JobBuilder(
//            Provider<Repository> repositoryProvider,
//            SerializationService serializationService,
//            DigestService digestService) {
//        this.repositoryProvider = repositoryProvider;
//        this.serializationService = serializationService;
//        this.digestService = digestService;
//    }
//
//    @Transactional
//    public QueuedJob build(AbstractEntity abstractEntity){
//        // this entity may have multiple jobs
//        // what if we fail to create the jobs even though the entity was successfully
// created/updated?
//        // this is more like a jpa event listener
//    }
//
//    @Transactional
//    public QueuedJob queue(Class<? extends AbstractRunnable> callableClass, AbstractEntity
// entityResource) {
//        JobExecutor jobExecutor = repositoryProvider.get().query(new
// FindJobExecutorByJobClassName(callableClass.getName()));// or create
//        EntityReference entityReference = repositoryProvider.get().query(new
// FindEntityReferenceByTypeAndIdQueryConfiguration(entityResource.getClass().getName(),
// entityResource.getId())); // or create
//
//        return build(callableClass);
//    }
//
//    /**
//     * If the queuedJob already exists, re-use it.
//     *
//     * @param callableJobClass the callable class
//     * @return
//     */
//    @Transactional
//    public QueuedJob build(Class<? extends AbstractRunnable> callableJobClass) {
//        //JobExecutor jobExecutor = repositoryProvider.get().query(new
// FindJobExecutorByJobClassName(callableJobClass.getName()));// or create
//        return repositoryProvider.get().query(new
// FindPendingJobsByJobExecutor(callableJobClass.getName()));
//    }
//
//    @Transactional
//    public QueuedJob queue(
//            Class<? extends AbstractRunnable> callableClass,
//            AbstractEntity entityResource,
//            AbstractSchedule... schedules) {
//        QueuedJob queuedJob =
//                new QueuedJob(
//                        repositoryProvider.get().query(new
// FindJobExecutorByJobClassName(callableClass.getName())),
//                        repositoryProvider.get().query(new
// FindEntityReferenceByTypeAndIdQueryConfiguration(entityResource.getClass().getName(),
// entityResource.getId())), // or create,
//                        schedules);
//        return (repositoryProvider.get().merge(queuedJob));
//    }
//
//    @Transactional
//    public QueuedJob queue(Class<? extends AbstractRunnable> callableClass, Serializable
// object) {
//        // serialize an object to an entity object ...
//        final String objectClassName = object.getClass().getName();
//
//        final byte[] data = serializationService.serialize(object);
//
//        // TODO: findOrCreateEntityType ...
//        EntityObject entityObject =
//                new EntityObject(
//                        // this expects the class to extends abstract entity, need to make it
// generic instead
//                        repositoryProvider.get().query(new
// FindEntityTypeByEntityClassTypeQueryConfiguration(objectClassName),
//                                digestService.compute(data),
//                                data));
//        repositoryProvider.get().merge(entityObject);
//
//        return (queue(callableClass, entityObject));
//    }
//
//
//    public QueuedJob build(AbstractEntity entityResource) {
//        QueuedJob queuedJob =
//                saveJob(repositoryProvider.get().query(new
// FindEntityReferenceByTypeAndIdQueryConfiguration(entityResource.getClass().getName(),
// entityResource.getId())));
//        queuedJob.getSchedules().add(new NowSchedule());
//
//        return queuedJob;
//    }
//
//    @Transactional
//    protected QueuedJob saveJob(AbstractEntity entityResource) {
//        JobExecutor jobExecutor =
//                entityJobExecutorRepositoryProvider
//                        .get()
//                        .findByResourceType(
//
//
// entityTypeRepositoryProvider.get().findOrCreate(entityResource.getClass()));
//
//        EntityReference entityReference =
//                null; // resourceRepositoryProvider.get().findOrCreate(entityResource);
//
//        try {
//            return (scheduleRepositoryProvider
//                    .get()
//                    .findJobByExecutorAndResource(jobExecutor, entityReference));
//        } catch (NoResultException nre) {
//            QueuedJob queuedJob = new QueuedJob(jobExecutor, entityReference, new NowSchedule());
//            return (scheduleRepositoryProvider.get().create(queuedJob));
//        }
//
//        return null;
//
//        NowSchedule schedule = new NowSchedule();
//        QueuedJob queuedJob;
//
//        try {
//            queuedJob =
//                    scheduleRepositoryProvider
//                            .get()
//                            .findPendingJobByExecutorAndResourceAndScheduleAndQueue(
//                                    jobExecutor, entityReference, schedule, null);
//        } catch (NoResultException e) {
//            queuedJob = new QueuedJob(jobExecutor, entityReference, schedule);
//        }
//        return (scheduleRepositoryProvider.get().merge(queuedJob));
//    }
//
//
//    @Transactional
//    @Override
//    public QueuedJob queue(Queue queue, Class<? extends AbstractRunnable> callableClass) {
//        return (queue(callableClass));
//        //    throw new UnsupportedOperationException("Not yet implemented."));
//    }
//
//    @Transactional
//    @Override
//    public QueuedJob queue(
//            Queue queue, Class<? extends AbstractRunnable> callableClass, AbstractEntity
//            entityResource) {
//        return (queue(callableClass, entityResource));
//        //    throw new UnsupportedOperationException("Not yet implemented."));
//    }
//
//    @Transactional
//    @Override
//    public QueuedJob queue(Queue queue, Class<? extends AbstractRunnable> callableClass,
// Serializable
//            object) {
//        return (queue(callableClass, object));
//        //    throw new UnsupportedOperationException("Not yet implemented."));
//    }
//
//    @Transactional
//    @Override
//    public QueuedJob queue(
//            Queue queue,
//            Class<? extends AbstractRunnable> callableClass,
//            AbstractEntity entityResource,
//            AbstractSchedule... schedules) {
//        return (queue(callableClass, entityResource, schedules));
//        //    throw new UnsupportedOperationException("Not yet implemented."));
//    }
//
//    @Transactional
//    public QueuedJob build(Queue queue, AbstractEntity entityResource) {
//        return (build(entityResource));
//        //    throw new UnsupportedOperationException("Not yet implemented."));
//    }
// }
