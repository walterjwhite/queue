package com.walterjwhite.job.api.enumeration;

public enum QueueType {
  All,
  Self,
  // local queue used to process files outbound
  Outbound;
}
