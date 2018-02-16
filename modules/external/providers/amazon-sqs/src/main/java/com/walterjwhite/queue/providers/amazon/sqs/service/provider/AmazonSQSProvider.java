package com.walterjwhite.queue.providers.amazon.sqs.service.provider;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.walterjwhite.amazon.property.AmazonRegion;
import com.walterjwhite.google.guice.property.property.Property;
import javax.inject.Inject;
import javax.inject.Provider;

public class AmazonSQSProvider implements Provider<AmazonSQS> {
  protected final AWSCredentialsProvider awsCredentialsProvider;

  protected final AmazonSQS amazonSQS;

  // http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-java-message-service-jms-client.html#code-examples
  // https://github.com/aws/aws-sdk-java/blob/master/src/samples/AmazonSimpleQueueService/SimpleQueueServiceSample.java
  // code below is copy-paste for synchronous
  // async code is also on site, move to async
  @Inject
  public AmazonSQSProvider(
      AWSCredentialsProvider awsCredentialsProvider, @Property(AmazonRegion.class) Regions region)
      throws Exception {
    this.awsCredentialsProvider = awsCredentialsProvider;

    amazonSQS =
        AmazonSQSClientBuilder.standard()
            .withRegion(region)
            .withCredentials(awsCredentialsProvider)
            .build();
  }

  @Override
  public AmazonSQS get() {
    return amazonSQS;
  }
}
