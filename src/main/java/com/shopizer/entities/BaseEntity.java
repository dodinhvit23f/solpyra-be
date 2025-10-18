package com.shopizer.entities;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.SequenceStyleGenerator;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@SuperBuilder(toBuilder = true)
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
public abstract class BaseEntity implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceIdGenerator")
  @GenericGenerator(name = "sequenceIdGenerator", strategy = "sequence", parameters = {
      @Parameter(name = SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, value = SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX),
      @Parameter(name = SequenceStyleGenerator.INITIAL_PARAM, value = "1"),
      @Parameter(name = SequenceStyleGenerator.INCREMENT_PARAM, value = "1")})

  @Column(name = "id", updatable = false)
  private BigInteger id;

  @Version
  @Column(name = "version", columnDefinition = "int8 DEFAULT 0")
  protected Long version = 0L;
}
