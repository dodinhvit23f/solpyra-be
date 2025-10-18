package com.shopizer.domain.schedule.services;

import com.shopizer.domain.schedule.dto.CommissionMessage;
import org.springframework.transaction.annotation.Transactional;

public interface CommissionSchedulerService {

  @Transactional
  void createOutboxForEligibleOrders();

  @Transactional
  void processPendingMessages();

  void processCallbackMessages(CommissionMessage message);
}
