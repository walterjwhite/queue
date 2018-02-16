package com.walterjwhite.job.impl;

import com.google.inject.Injector;
import com.google.inject.persist.jpa.JpaPersistModule;
import com.walterjwhite.datastore.GoogleGuicePersistModule;
import com.walterjwhite.datastore.api.model.entity.EntityContainerType;
import com.walterjwhite.datastore.api.model.entity.EntityReference;
import com.walterjwhite.datastore.api.model.entity.EntityType;
import com.walterjwhite.datastore.criteria.CriteriaBuilderModule;
import com.walterjwhite.datastore.criteria.Repository;
import com.walterjwhite.google.guice.GuiceHelper;
import com.walterjwhite.google.guice.property.test.GuiceTestModule;
import com.walterjwhite.job.api.enumeration.Recurrence;
import com.walterjwhite.job.api.model.EntityJobExecutor;
import com.walterjwhite.job.api.model.Job;
import com.walterjwhite.job.api.model.scheduling.SimpleSchedule;
import com.walterjwhite.queue.api.service.QueueService;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.reflections.Reflections;
import org.reflections.scanners.FieldAnnotationsScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;

public class JobSchedulingTest {
  protected Injector injector;

  @Before
  public void onBefore() {
    GuiceHelper.addModules(
        new GuiceTestModule(
            new Reflections(
                "com.walterjwhite",
                TypeAnnotationsScanner.class,
                SubTypesScanner.class,
                FieldAnnotationsScanner.class)),
        new QueueModule(),
        new QueueTestModule(),
        new CriteriaBuilderModule(),
        new GoogleGuicePersistModule(),
        new JpaPersistModule("defaultJPAUnit"));
    GuiceHelper.setup();
    injector = GuiceHelper.getGuiceInjector();
  }

  @After
  public void onAfter() {
    GuiceHelper.stop();
  }

  @Test
  public void doMinutelyRecurring() {
    QueueService queueService = injector.getInstance(QueueService.class);
    Repository repository = injector.getInstance(Repository.class);
    EntityManager entityManager = injector.getInstance(EntityManager.class);

    EntityTransaction entityTransaction = entityManager.getTransaction();
    entityTransaction.begin();

    Message message = new Message();
    message.setMessage("This is a test");
    repository.persist(message);

    EntityType entityType = new EntityType();
    entityType.setEntityContainerType(EntityContainerType.Database);
    entityType.setName(Message.class.getName());
    repository.persist(entityType);

    EntityJobExecutor jobExecutor =
        new EntityJobExecutor(LogJobExecutor.class.getName(), entityType);
    repository.persist(jobExecutor);

    EntityReference entityReference = new EntityReference();
    entityReference.setEntityId(message.getId());
    entityReference.setEntityType(entityType);
    repository.persist(entityReference);

    final Job minutelyRecurringJob = new Job();
    final SimpleSchedule minuteSchedule = new SimpleSchedule();
    minuteSchedule.setRecurrence(Recurrence.Secondly);
    minuteSchedule.setRecurrenceAmount(5);
    minuteSchedule.setJob(minutelyRecurringJob);

    minutelyRecurringJob.getSchedules().add(minuteSchedule);
    minutelyRecurringJob.setJobExecutor(jobExecutor);
    minutelyRecurringJob.setEntityReference(entityReference);

    repository.persist(minutelyRecurringJob);
    entityTransaction.commit();

    queueService.queue(minutelyRecurringJob);

    try {
      Thread.sleep(1200000);
    } catch (Exception e) {

    }
  }
}
