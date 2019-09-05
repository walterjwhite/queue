package com.walterjwhite.queue.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractUUIDEntity;
import com.walterjwhite.infrastructure.inject.core.model.ApplicationSession;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.ToString;

// TODO:
// 1. support any number of workers
// 2. automatically distribute jobs across workers using buckets to minimize contention of selecting
// jobs
//    a. if the hash of the job fits into worker 1's bucket, then worker 1 picks it up
//    b. if the designated worker fails to pick the job up in a timely manner, then another worker
// will pick it up
// 3. scheduling / assignment algorithm
//    a. round-robin
//       1. as workers join the cluster, they announce theirselves
//       2. as jobs are queued, workers are selected sequentially
//       3. jobs that fail to be picked up as "accepted" will be re-assigned and the failing worker
// will be removed from the cluster
// @PersistenceCapable
@Data
@ToString(doNotUseGetters = true)
@Entity
public class Worker extends AbstractUUIDEntity {
  //    @ManyToOne
  //    @JoinColumn
  //    protected Node node;

  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected ApplicationSession applicationSession;

  public Worker(ApplicationSession applicationSession) {
    super();
    this.applicationSession = applicationSession;
  }

  public Worker() {
    super();
  }
}
