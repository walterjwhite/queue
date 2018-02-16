package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractNamedEntity;
import com.walterjwhite.job.api.enumeration.Durability;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Inheritance(strategy = InheritanceType.JOINED)
@Entity
public class JobExecutor extends AbstractNamedEntity {

  @Enumerated(EnumType.STRING)
  @Column
  protected Durability durability;

  @OneToMany(cascade = CascadeType.ALL)
  protected List<Job> jobs = new ArrayList<>();

  public JobExecutor(String callableClassName) {
    super(callableClassName);
  }

  public JobExecutor() {
    super();
  }

  public List<Job> getJobs() {
    return jobs;
  }

  public void setJobs(List<Job> jobs) {
    this.jobs = jobs;
  }

  public Durability getDurability() {
    return durability;
  }

  public void setDurability(Durability durability) {
    this.durability = durability;
  }
}
