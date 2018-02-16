package com.walterjwhite.job.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.datastore.api.model.entity.EntityType;
import java.util.Arrays;
import java.util.Objects;
import javax.persistence.*;

/**
 * Wrapper to store any POJO that does not inherit from AbstractEntity. Stores the Type and the hash
 * as the PK.
 */
@Entity
public class EntityObject extends AbstractEntity {
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected EntityType entityType;

  /** SHA256 sum of the data * */
  @Column(nullable = false, updatable = false)
  protected String hash;

  @Lob
  @Column(nullable = false)
  protected byte[] serializedObject;

  public EntityObject(EntityType entityType, String hash, byte[] serializedObject) {
    super();
    this.entityType = entityType;
    this.hash = hash;
    this.serializedObject = serializedObject;
  }

  public EntityObject() {
    super();
  }

  public byte[] getSerializedObject() {
    return serializedObject;
  }

  public void setSerializedObject(byte[] serializedObject) {
    this.serializedObject = serializedObject;
  }

  public EntityType getEntityType() {
    return entityType;
  }

  public void setEntityType(EntityType entityType) {
    this.entityType = entityType;
  }

  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EntityObject that = (EntityObject) o;
    return Objects.equals(entityType, that.entityType)
        && Objects.equals(hash, that.hash)
        && Arrays.equals(serializedObject, that.serializedObject);
  }

  @Override
  public int hashCode() {

    int result = Objects.hash(entityType, hash);
    result = 31 * result + Arrays.hashCode(serializedObject);
    return result;
  }
}
