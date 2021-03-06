package com.walterjwhite.queue.impl.scheduling;

import com.walterjwhite.infrastructure.inject.core.service.StartupAware;
import com.walterjwhite.logging.annotation.LogStackTrace;
import com.walterjwhite.queue.api.annotation.Job;
import com.walterjwhite.queue.api.model.AbstractQueued;
import com.walterjwhite.queue.api.model.QueuedJob;
import com.walterjwhite.queue.api.model.ScheduleInstance;
import com.walterjwhite.queue.api.service.QueueService;
import com.walterjwhite.queue.impl.worker.enumeration.ScheduleType;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.reflections.Reflections;

/** Makes jobs persistent that are registered with @Job. */
@Singleton
public class JobQueuer implements StartupAware {
  protected final QueueService queueService;
  protected final Reflections reflections;

  @Inject
  public JobQueuer(QueueService queueService, Reflections reflections) {
    this.queueService = queueService;
    this.reflections = reflections;
  }

  /**
   * Any jobs that were in-flight at time of shutdown will be marked as cancelled and discovered
   * jobs will be recreated.
   */
  @Override
  public void onStartup() {
    cancelOldJobs();
    scheduleJobs();
  }

  /**
   * Cancel old Jobs (QueuedJob) versus (QueuedEvent), events are generated by the application and
   * cannot be rescheduled However, QueuedJobs are "permanent" and can be rescheduled when the
   * application is restarted. In order to handle job schedule changes, mark all the incomplete ones
   * as aborted, then reschedule anything found.
   */
  protected void cancelOldJobs() {
    queueService.findAbortedJobExecutions().stream().forEach(queuedJob -> cancelJob(queuedJob));
  }

  protected boolean cancelJob(AbstractQueued queuedJob) {
    if (QueuedJob.class.isAssignableFrom(queuedJob.getClass())) {
      queueService.cancel(queuedJob);
      return true;
    }

    return false;
  }

  protected void scheduleJobs() {
    getQueuedJobs().stream().forEach(queuedJob -> scheduleJob(queuedJob));
  }

  protected void scheduleJob(QueuedJob queuedJob) {
    try {
      queueService.queue(queuedJob);
    } catch (Exception e) {
      onQueueError(queuedJob, e);
    }
  }

  @LogStackTrace
  protected void onQueueError(QueuedJob queuedJob, Exception e) {}

  protected Set<QueuedJob> getQueuedJobs() {
    final Set<QueuedJob> queuedJobs = new HashSet<>();

    for (final Class jobClass : reflections.getTypesAnnotatedWith(Job.class)) {
      final Job job = (Job) jobClass.getAnnotation(Job.class);
      addJobs(
          queuedJobs,
          getScheduleInstances(job),
          jobClass,
          job.jobExecutionConfiguration().system());
    }

    return queuedJobs;
  }

  protected Set<ScheduleInstance> getScheduleInstances(final Job jobAnnotation) {
    final Set<ScheduleInstance> scheduleInstances = new HashSet<>();
    Arrays.stream(ScheduleType.values())
        .forEach(
            scheduleType ->
                scheduleType
                    .getScheduleInstanceBuilder()
                    .getDelay(scheduleInstances, jobAnnotation));

    return scheduleInstances;
  }

  protected void addJobs(
      final Set<QueuedJob> queuedJobs,
      final Set<ScheduleInstance> scheduleInstances,
      final Class jobClass,
      final boolean system) {
    scheduleInstances.stream()
        .forEach(
            scheduleInstance ->
                queuedJobs.add(
                    queueService.queue(new QueuedJob(jobClass, scheduleInstance, system))));
  }
}
