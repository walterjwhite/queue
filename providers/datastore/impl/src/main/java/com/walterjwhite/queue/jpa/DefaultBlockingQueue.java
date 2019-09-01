package com.walterjwhite.queue.jpa;

import com.walterjwhite.datastore.api.repository.Repository;
import com.walterjwhite.queue.api.model.QueuedJob;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;

public class DefaultBlockingQueue implements BlockingQueue {
  protected final String queueName;
  //  protected final JobBuilder jobBuilder;
  protected final Repository repository;

  @Inject
  public DefaultBlockingQueue(String queueName /*, JobBuilder jobBuilder*/, Repository repository) {
    this.queueName = queueName;
    //    this.jobBuilder = jobBuilder;
    this.repository = repository;
  }

  @Override
  public boolean add(Object o) {
    QueuedJob queuedJob =
        null; // jobBuilder.build(new Queue(queueName, QueueType.Self), /*o*/ null);
    repository.create(queuedJob);

    return true;
  }

  @Override
  public boolean offer(Object o) {
    return add(o);
  }

  @Override
  public Object remove() {
    QueuedJob queuedJob = repository.findById(QueuedJob.class, -1);
    return queuedJob;
  }

  @Override
  public Object poll() {
    return null;
  }

  @Override
  public Object element() {
    return null;
  }

  @Override
  public Object peek() {
    return null;
  }

  @Override
  public void put(Object o) throws InterruptedException {}

  @Override
  public boolean offer(Object o, long l, TimeUnit timeUnit) throws InterruptedException {
    return false;
  }

  @Override
  public Object take() throws InterruptedException {
    return null;
  }

  @Override
  public Object poll(long l, TimeUnit timeUnit) throws InterruptedException {
    return null;
  }

  @Override
  public int remainingCapacity() {
    return 0;
  }

  @Override
  public boolean remove(Object o) {
    return false;
  }

  @Override
  public boolean addAll(Collection collection) {
    return false;
  }

  @Override
  public void clear() {}

  @Override
  public boolean retainAll(Collection collection) {
    return false;
  }

  @Override
  public boolean removeAll(Collection collection) {
    return false;
  }

  @Override
  public boolean containsAll(Collection collection) {
    return false;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public boolean isEmpty() {
    return false;
  }

  @Override
  public boolean contains(Object o) {
    return false;
  }

  @Override
  public Iterator iterator() {
    return null;
  }

  @Override
  public Object[] toArray() {
    return new Object[0];
  }

  @Override
  public Object[] toArray(Object[] objects) {
    return new Object[0];
  }

  @Override
  public int drainTo(Collection collection) {
    return 0;
  }

  @Override
  public int drainTo(Collection collection, int i) {
    return 0;
  }

  //  public DefaultBlockingQueue(int i) {
  //    super(i);
  //  }
  //
  //  public DefaultBlockingQueue(int i, boolean b) {
  //    super(i, b);
  //  }
  //
  //  public DefaultBlockingQueue(int i, boolean b, Collection collection) {
  //    super(i, b, collection);
  //  }
}
