package com.walterjwhite.queue.impl.worker;

import com.walterjwhite.datastore.api.repository.Repository;
import com.walterjwhite.infrastructure.inject.core.helper.ApplicationHelper;
import com.walterjwhite.queue.api.model.Worker;
import com.walterjwhite.shell.api.model.Node;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton
public class WorkerProvider implements Provider<Worker> {
  protected final Provider<Repository> repositoryProvider;
  protected final Worker worker;

  @Inject
  public WorkerProvider(Node node, Provider<Repository> repositoryProvider) {
    this.repositoryProvider = repositoryProvider;
    this.worker =
        getWorker(new Worker(ApplicationHelper.getApplicationInstance().getApplicationSession()));
  }

  /**
   * A worker is unique to the application identifier, each time we start the application, that will
   * change the application identifier so we can trace it to a running instance of the application.
   *
   * @param worker the persisted worker
   * @return the worker persisted to the datastore
   */
  protected Worker getWorker(Worker worker) {
    return repositoryProvider.get().create(worker);
  }

  @Override
  public Worker get() {
    return worker;
  }
}
