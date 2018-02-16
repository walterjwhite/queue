package com.walterjwhite.job.external.queue.impl;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import java.lang.reflect.Field;
import java.util.*;
import javax.inject.Provider;
import javax.persistence.EntityManager;

public enum FieldRefresher {
  SingleType(AbstractEntity.class) {
    @Override
    boolean isSupported(AbstractEntity entity, final Field field) {
      return true;
    }

    @Override
    Object refresh(
        Provider<EntityManager> entityManagerProvider, AbstractEntity entity, final Field field)
        throws IllegalAccessException {
      final AbstractEntity currentEntity = (AbstractEntity) field.get(entity);

      if (isManaged(currentEntity)) {
        final AbstractEntity currentManagedEntity =
            entityManagerProvider.get().find(currentEntity.getClass(), currentEntity.getId());

        return (currentManagedEntity);
      }

      return (null);
    }

    @Override
    Class<? extends AbstractEntity> getType(Field field, AbstractEntity entity)
        throws IllegalAccessException {
      return (Class<? extends AbstractEntity>) field.getType();
    }
  },
  IterableType(Iterable.class) {
    @Override
    boolean isSupported(AbstractEntity entity, final Field field) {
      try {
        return (((Iterable) field.get(entity)).iterator().hasNext()
            && AbstractEntity.class.isAssignableFrom(
                ((Iterable) field.get(entity)).iterator().next().getClass()));
      } catch (IllegalAccessException e) {
        throw (new RuntimeException("Not properly configured", e));
      }
    }

    @Override
    Object refresh(
        Provider<EntityManager> entityManagerProvider, AbstractEntity entity, final Field field)
        throws IllegalAccessException {
      final Collection<AbstractEntity> refreshEntities = get(field);

      for (final AbstractEntity entityItem :
          ((Iterable<? extends AbstractEntity>) field.get(entity))) {

        if (isManaged(entityItem)) {
          //      entityManagerProvider.get().refresh(currentValue);
          final AbstractEntity currentManagedEntity =
              entityManagerProvider.get().find(entityItem.getClass(), entityItem.getId());
          refreshEntities.add(currentManagedEntity);
        } else {
          // check the transient entity for FKs to managed entities
          refresh(entityManagerProvider, entityItem);
          refreshEntities.add(entityItem);
        }
      }

      return (refreshEntities);
    }

    @Override
    Class<? extends AbstractEntity> getType(Field field, AbstractEntity entity)
        throws IllegalAccessException {
      if (field.get(entity) != null && ((Iterable) field.get(entity)).iterator().hasNext())
        return (Class<? extends AbstractEntity>)
            ((Iterable) field.get(entity)).iterator().next().getClass();

      return (null);
    }
  };

  final Class supportedType;

  FieldRefresher(Class supportedType) {
    this.supportedType = supportedType;
  }

  abstract boolean isSupported(AbstractEntity entity, final Field field);

  abstract Object refresh(
      Provider<EntityManager> entityManagerProvider, AbstractEntity entity, final Field field)
      throws IllegalAccessException;

  abstract Class<? extends AbstractEntity> getType(final Field field, AbstractEntity entity)
      throws IllegalAccessException;

  private static Collection get(final Field field) {
    if (Set.class.isAssignableFrom(field.getType())) {
      return (new HashSet());
    }
    if (List.class.isAssignableFrom(field.getType())) {
      return (new ArrayList());
    }

    throw (new UnsupportedOperationException("unsupported field type:" + field.getType()));
  }

  private static boolean isManaged(final AbstractEntity currentEntity) {
    return (currentEntity != null
        && currentEntity.getId() != null /*&& currentEntity.getId() >= 0*/);
  }

  public static AbstractEntity refresh(
      Provider<EntityManager> entityManagerProvider, AbstractEntity entity)
      throws IllegalAccessException {
    return (refresh(entityManagerProvider, entity, null, entity.getClass()));
  }

  public static AbstractEntity refresh(
      Provider<EntityManager> entityManagerProvider,
      AbstractEntity entity,
      final Class fromClass,
      final Class entityClass)
      throws IllegalAccessException {
    refreshFields(entityManagerProvider, entity, fromClass, entityClass);
    doSuperClass(entityManagerProvider, entity, fromClass, entityClass);

    return (entity);
  }

  private static void refreshFields(
      Provider<EntityManager> entityManagerProvider,
      AbstractEntity entity,
      final Class fromClass,
      final Class entityClass)
      throws IllegalAccessException {
    for (final Field field : entityClass.getDeclaredFields()) {
      refreshField(entityManagerProvider, entity, fromClass, field);
    }
  }

  private static boolean refreshField(
      Provider<EntityManager> entityManagerProvider,
      AbstractEntity entity,
      final Class fromClass,
      final Field field)
      throws IllegalAccessException {

    final boolean wasAccessible = field.isAccessible();
    try {
      field.setAccessible(true);

      if (field.get(entity) != null) {

        for (FieldRefresher fieldRefresher : values()) {
          if (fieldRefresher.supportedType.isAssignableFrom(field.getType())) {
            final Class<? extends AbstractEntity> entityType =
                fieldRefresher.getType(field, entity);

            if (entityType == null) break;

            // if (visitedClasses.contains(entityType)) break;
            if (fromClass != null && fromClass.equals(entityType)) break;

            if (fieldRefresher.isSupported(entity, field)) {
              //              visitedClasses.add(entityType);

              final Object result = fieldRefresher.refresh(entityManagerProvider, entity, field);

              if (result != null) {
                field.set(entity, result);

                return (true);
              }

              // field itself may not be managed, but check fields within that entity
              refreshFields(
                  entityManagerProvider,
                  (AbstractEntity) field.get(entity),
                  entity.getClass(),
                  entityType);
              return (false);
            }
          }
        }
      }

      return (false);
    } finally {
      field.setAccessible(wasAccessible);
    }
  }

  private static void doSuperClass(
      Provider<EntityManager> entityManagerProvider,
      AbstractEntity entity,
      final Class fromClass,
      final Class entityClass)
      throws IllegalAccessException {
    if (AbstractEntity.class.isAssignableFrom(entityClass.getSuperclass())) {
      refresh(entityManagerProvider, entity, entityClass, entityClass.getSuperclass());
    }
  }
}
