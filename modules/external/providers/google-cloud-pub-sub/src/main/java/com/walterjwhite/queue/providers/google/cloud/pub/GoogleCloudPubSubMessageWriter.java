package com.walterjwhite.queue.providers.google.cloud.pub;

import com.google.api.gax.core.CredentialsProvider;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.pubsub.v1.ProjectTopicName;
import com.walterjwhite.google.property.GoogleCloudProjectId;
import com.walterjwhite.google.property.GoogleCloudSubscriptionId;
import com.walterjwhite.job.external.queue.impl.service.AbstractExternalQueueService;
import com.walterjwhite.job.external.queue.impl.service.EntityService;
import com.walterjwhite.property.impl.annotation.Property;
import com.walterjwhite.queue.api.model.Queue;
import java.io.IOException;
import java.io.OutputStream;
import javax.inject.Inject;

public class GoogleCloudPubSubMessageWriter extends AbstractExternalQueueService {

  protected final CredentialsProvider credentialsProvider;
  protected final String googleCloudProjectId;
  protected final String googleCloudSubscriptionId;

  @Inject
  public GoogleCloudPubSubMessageWriter(
      EntityService entityService,
      CredentialsProvider credentialsProvider,
      @Property(GoogleCloudProjectId.class) String googleCloudProjectId,
      @Property(GoogleCloudSubscriptionId.class) String googleCloudSubscriptionId) {
    super(entityService);
    this.credentialsProvider = credentialsProvider;

    this.googleCloudProjectId = googleCloudProjectId;
    this.googleCloudSubscriptionId = googleCloudSubscriptionId;
  }

  protected Publisher getPublisher() throws IOException {
    return (Publisher.newBuilder(
            ProjectTopicName.of(googleCloudProjectId, googleCloudSubscriptionId))
        .setCredentialsProvider(credentialsProvider)
        .build());
  }

  @Override
  protected OutputStream getOutputStream(Queue queue, OutputStream outputStream) throws Exception {
    return new GoogleCloudPubSubMessageOutputStream(getPublisher());
  }
}
