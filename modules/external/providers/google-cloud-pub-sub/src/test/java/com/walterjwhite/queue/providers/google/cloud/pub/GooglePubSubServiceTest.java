package com.walterjwhite.queue.providers.google.cloud.pub;

import com.walterjwhite.google.guice.GuiceHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class GooglePubSubServiceTest {

  @Before
  public void onBefore() {
    GuiceHelper.addModules(new GooglePubSubTestModule(getClass()));
    GuiceHelper.setup();
  }

  @After
  public void onAfter() {
    GuiceHelper.stop();
  }

  @Test
  public void testBasic() {
    /*
        GoogleCloudPubSubMessageWriter googleCloudPubSubMessageWriter =
            GuiceHelper.getGuiceApplicationInjector().getInstance(GoogleCloudPubSubMessageWriter.class);

        final Queue queue = new Queue("test", QueueType.Self);
        googleCloudPubSubMessageWriter.write(queue, queue);

        LOGGER.info("wrote message");

        AsynchronousGooglePubSubQueueReaderService asynchronousGooglePubSubQueueReaderService =
            GuiceHelper.getGuiceApplicationInjector()
                .getInstance(AsynchronousGooglePubSubQueueReaderService.class);
        asynchronousGooglePubSubQueueReaderService.startAsync();

        Thread.sleep(5000);
    */
  }
}
