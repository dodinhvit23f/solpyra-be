package com.shopizer.domain.schedule.services;

import org.springframework.transaction.annotation.Transactional;

public interface CommissionSchedulerService {

  @Transactional
  void createOutboxForEligibleOrders();

  @Transactional
  void processPendingMessages();
}
