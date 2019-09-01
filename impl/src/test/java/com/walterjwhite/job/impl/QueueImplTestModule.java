package com.walterjwhite.job.impl;

import com.walterjwhite.datastore.GoogleGuicePersistModule;
import com.walterjwhite.datastore.api.repository.CriteriaBuilderModule;
import com.walterjwhite.google.guice.property.test.GuiceTestModule;
import com.walterjwhite.google.guice.property.test.PropertyValuePair;
import org.reflections.Reflections;

public class QueueImplTestModule extends GuiceTestModule {
  public QueueImplTestModule(Class testClass, PropertyValuePair... propertyValuePairs) {
    super(testClass, propertyValuePairs);
  }

  public QueueImplTestModule(
      Class testClass, Reflections reflections, PropertyValuePair... propertyValuePairs) {
    super(testClass, reflections, propertyValuePairs);
  }

  @Override
  protected void configure() {
    super.configure();

    bind(LogJobRunnableExecutor.class);

    install(new QueueModule());
    install(new CriteriaBuilderModule());
    install(new GoogleGuicePersistModule(/*propertyManager, reflections*/ ));
  }
}
