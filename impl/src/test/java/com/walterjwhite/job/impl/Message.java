package com.walterjwhite.job.impl;

import com.walterjwhite.datastore.api.model.entity.AbstractUUIDEntity;
import javax.jdo.annotations.PersistenceCapable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(doNotUseGetters = true, callSuper = true)
@PersistenceCapable
public class Message extends AbstractUUIDEntity {
  protected String message;
}
