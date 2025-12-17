package com.solpyra.domain.schedule.services;

import com.solpyra.domain.schedule.dto.CommissionMessage;
import org.springframework.transaction.annotation.Transactional;

public interface CommissionSchedulerService {

  @Transactional
  void createOutboxForEligibleOrders();

  @Transactional
  void processPendingMessages();

  void processCallbackMessages(CommissionMessage message);
}
