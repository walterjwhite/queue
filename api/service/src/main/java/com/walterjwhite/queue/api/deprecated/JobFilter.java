package com.walterjwhite.queue.api.deprecated;

/** Determines whether the given queuedJob executor should process the given argument. */
/** Not sure what to do with this * */
@Deprecated
public interface JobFilter {

  boolean accepts(final Object argument);
}
