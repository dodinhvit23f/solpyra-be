package com.solpyra.entities;

import com.solpyra.common.constant.CommissionOutboxStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "commission_outbox")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommissionOutbox extends BaseEntity {

  @Column(name = "order_id", nullable = false)
  private BigInteger orderId;

  @Column(name = "user_id")
  private BigInteger userId;

  @Column(name = "commission")
  private BigDecimal commission;

  @Enumerated(EnumType.ORDINAL)
  @Column(name = "status", nullable = false)
  private CommissionOutboxStatus status = CommissionOutboxStatus.PENDING;

  @Column(name = "retry", nullable = false)
  private int retry;

  @Column(name = "error_message", nullable = false)
  private String errorMessage;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @Column(name = "sent_at")
  private ZonedDateTime sentAt;
}