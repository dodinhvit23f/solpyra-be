package com.solpyra.entities;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;

@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@MappedSuperclass
public abstract class LogEntity extends BaseEntity {

  @Column(name = "created_by")
  String createdBy;

  @Column(name = "created_date")
  ZonedDateTime createdDate;

  @Column(name = "updated_by")
  String updatedBy;

  @Column(name = "updated_date")
  ZonedDateTime updatedDate;

  @PrePersist
  public void prePersist() {
    createdBy = SecurityContextHolder.getContext().getAuthentication().getName();
    createdDate = ZonedDateTime.now();
  }

  @PreUpdate
  public void preUpdate() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();

    updatedBy = "SYSTEM";
    if(!ObjectUtils.isEmpty(auth)) {
      updatedBy = auth.getName();
    }

    updatedDate = ZonedDateTime.now();
  }

}
