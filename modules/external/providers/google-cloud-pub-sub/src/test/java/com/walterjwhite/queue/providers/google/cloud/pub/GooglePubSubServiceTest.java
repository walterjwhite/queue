package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.inject.persist.jpa.JpaPersistModule;
import com.walterjwhite.compression.modules.CompressionModule;
import com.walterjwhite.datastore.GoogleGuicePersistModule;
import com.walterjwhite.datastore.criteria.CriteriaBuilderModule;
import com.walterjwhite.encryption.impl.EncryptionModule;
import com.walterjwhite.google.guice.GuiceHelper;
import com.walterjwhite.google.guice.property.test.GuiceTestModule;
import com.walterjwhite.serialization.modules.java.KryoSerializationServiceModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GooglePubSubServiceTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(GooglePubSubServiceTest.class);

  @Before
  public void onBefore() throws Exception {
    GuiceHelper.addModules(
        new GoogleCloudPubSubQueueModule(),
        new KryoSerializationServiceModule(),
        new GuiceTestModule(),
        new JpaPersistModule("defaultJPAUnit"),
        new CriteriaBuilderModule(),
        new GoogleGuicePersistModule(),
        new EncryptionModule(),
        new CompressionModule());
    GuiceHelper.setup();
  }

  @After
  public void onAfter() throws Exception {
    GuiceHelper.stop();
  }

  @Test
  public void testBasic() throws Exception {
    /*
        GoogleCloudPubSubMessageWriter googleCloudPubSubMessageWriter =
            GuiceHelper.getGuiceInjector().getInstance(GoogleCloudPubSubMessageWriter.class);

        final Queue queue = new Queue("test", QueueType.Self);
        googleCloudPubSubMessageWriter.write(queue, queue);

        LOGGER.info("wrote message");

        AsynchronousGooglePubSubQueueReaderService asynchronousGooglePubSubQueueReaderService =
            GuiceHelper.getGuiceInjector()
                .getInstance(AsynchronousGooglePubSubQueueReaderService.class);
        asynchronousGooglePubSubQueueReaderService.startAsync();

        Thread.sleep(5000);
    */
  }
}
