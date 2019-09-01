// this is NO LONGER NEEDED because impl is abstract
// impl still requires an underlying implementation such as JPA or MQ

// package com.walterjwhite.queue.guice;
//
// import com.walterjwhite.datastore.modules.GoogleGuicePersistModule1;
// import com.walterjwhite.inject.cli.module.AbstractPropertyModule;
// import com.walterjwhite.property.api.PropertyManager;
// import com.walterjwhite.property.impl.annotation.Property;
// import com.walterjwhite.queue.api.service.QueueService;
// import com.walterjwhite.queue.impl.persistent.AbstractQueueScheduleService;
// import com.walterjwhite.queue.impl.jobWorkerService.JobQueuer;
// import com.walterjwhite.queue.impl.jobWorkerService.property.QueueJPAUnit;
// import org.reflections.Reflections;
//
// public class QueueModule extends AbstractPropertyModule {
//
//  @Property(QueueJPAUnit.class)
//  protected String queueJPAUnitName;
//
//  protected QueueModule(PropertyManager propertyManager, Reflections reflections) {
//    super(propertyManager, reflections);
//  }
//
//  @Override
//  protected void configure() {
//    bind(JobQueuer.class);
//    bind(QueueService.class).to(AbstractQueueScheduleService.class);
//
//    //@QualifierType(QueueJPAUnit.class)
//    install(new GoogleGuicePersistModule1(propertyManager, reflections, queueJPAUnitName));
//
//    //    // bind repositories
//    //    bind(EntityJobExecutorEntityRepository.class);
//    //    bind(EntityObjectEntityRepository.class);
//    //    bind(EntityReferenceEntityRepository.class);
//    //    bind(EntityTypeEntityRepository.class);
//    //    bind(JobExecutorEntityRepository.class);
//    //    bind(JobEntityRepository.class);
//    //    bind(JobExecutionEntityRepository.class);
//    //    bind(ScheduleEntityRepository.class);
//    //
//    //    bind(JobExecutionEnvironmentManager.class);
//  }
// }
