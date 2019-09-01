package com.walterjwhite.google.guice;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.walterjwhite.infrastructure.inject.providers.guice.GuiceApplication;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;

public class EventBusDelegate implements StartupAware, ShutdownAware {
  protected Set<WeakReference> syncInstances = new HashSet<>();
  protected Set<WeakReference> asyncInstances = new HashSet<>();

  public void register(Object instance, final boolean isAsync) {
    try {
      GuiceApplication.getGuiceApplicationInjector();
      doRegister(instance, isAsync);
    } catch (Exception e) {
      onRegistrationException(e, instance, isAsync);
    }
  }

  protected void onRegistrationException(
      final Exception e, final Object instance, final boolean isAsync) {
    if (isAsync) asyncInstances.add(new WeakReference(instance));
    else syncInstances.add(new WeakReference(instance));
  }

  protected void doRegister(Object e, final boolean isAsync) {
    if (isAsync)
      GuiceApplication.getGuiceApplicationInjector().getInstance(AsyncEventBus.class).register(e);
    else GuiceApplication.getGuiceApplicationInjector().getInstance(EventBus.class).register(e);
  }

  @Override
  public void onStartup() {
    doRegister(asyncInstances, true);
    doRegister(syncInstances, false);
  }

  protected void doRegister(final Set<WeakReference> instances, final boolean isAsync) {
    final Set<WeakReference> registeredInstances = new HashSet<>();

    for (WeakReference weakReference : instances) {
      doRegister(weakReference.get(), isAsync);

      registeredInstances.add(weakReference);
    }

    instances.removeAll(registeredInstances);
  }

  @Override
  public void onShutdown() {}
}
