package com.walterjwhite.queue.api.model;

import java.util.concurrent.TimeUnit;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.*;

@Embeddable
@ToString(doNotUseGetters = true)
// @Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ScheduleInstance implements Unqueueable {
  @Column(updatable = false)
  protected long initialDelay;

  @Column(updatable = false)
  protected long fixedDelay;

  @Column(updatable = false)
  protected TimeUnit units;

  //  @Enumerated
  //  @Column
  //  protected ScheduleType scheduleType;

  public boolean isRecurring() {
    return fixedDelay > 0;
  }
}
