package com.walterjwhite.job.impl;

import com.walterjwhite.datastore.api.model.entity.AbstractUUIDEntity;
import javax.persistence.Entity;

@Entity
public class Message extends AbstractUUIDEntity {
  protected String message;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }
}
