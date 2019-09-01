package com.walterjwhite.queue.api.model;

import com.walterjwhite.datastore.api.model.entity.AbstractEntity;
import com.walterjwhite.datastore.api.model.entity.EntityType;
import javax.persistence.*;
import lombok.*;

/**
 * Wrapper to store any POJO that does not inherit from AbstractEntity. Stores the Type and the hash
 * as the PK.
 */
@Data
@ToString(doNotUseGetters = true)
// @PersistenceCapable
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class EntityObject extends AbstractEntity implements Unqueueable {
  @ManyToOne(optional = false)
  @JoinColumn(nullable = false, updatable = false)
  protected EntityType entityType;

  /** SHA256 sum of the data * */
  @Column(nullable = false, updatable = false)
  protected String hash;

  @EqualsAndHashCode.Exclude
  @Lob
  @Column(nullable = false)
  protected byte[] serializedObject;
}
