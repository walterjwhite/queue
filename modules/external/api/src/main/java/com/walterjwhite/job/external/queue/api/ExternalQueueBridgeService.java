package com.walterjwhite.job.external.queue.api;

import com.walterjwhite.queue.api.model.Queue;
import com.walterjwhite.queue.api.producer.InputStreamProducer;

public interface ExternalQueueBridgeService {
  /**
   * Takes the given external queuedJob and queues it locally.
   *
   * @throws Exception
   */
  void read(Queue queue, byte[] data) throws Exception;

  void read(Queue queue, InputStreamProducer inputStreamProducer) throws Exception;

  void readRaw(Queue queue, byte[] data) throws Exception;

  void readRaw(Queue queue, InputStreamProducer inputStreamProducer) throws Exception;
}
