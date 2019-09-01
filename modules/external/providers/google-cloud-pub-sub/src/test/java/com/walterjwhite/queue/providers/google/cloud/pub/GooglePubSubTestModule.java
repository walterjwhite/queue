package com.walterjwhite.queue.providers.google.cloud.pub;

import com.walterjwhite.compression.modules.CompressionModule;
import com.walterjwhite.datastore.GoogleGuicePersistModule;
import com.walterjwhite.datastore.api.repository.CriteriaBuilderModule;
import com.walterjwhite.encryption.impl.EncryptionModule;
import com.walterjwhite.google.guice.property.test.GuiceTestModule;
import com.walterjwhite.google.guice.property.test.PropertyValuePair;
import com.walterjwhite.job.impl.QueueModule;
import com.walterjwhite.serialization.modules.java.KryoSerializationServiceModule;
import org.reflections.Reflections;

public class GooglePubSubTestModule extends GuiceTestModule {
  public GooglePubSubTestModule(Class testClass, PropertyValuePair... propertyValuePairs) {
    super(testClass, propertyValuePairs);
  }

  public GooglePubSubTestModule(
      Class testClass, Reflections reflections, PropertyValuePair... propertyValuePairs) {
    super(testClass, reflections, propertyValuePairs);
  }

  @Override
  protected void configure() {
    super.configure();

    install(new QueueModule());
    install(new CriteriaBuilderModule());
    install(new GoogleGuicePersistModule(/*propertyManager, reflections*/ ));

    install(new GoogleCloudPubSubQueueModule());
    install(new KryoSerializationServiceModule());
    install(new EncryptionModule());
    install(new CompressionModule());
  }
}
