package com.shopizer.domain.schedule.jobs;

import com.shopizer.domain.schedule.services.CommissionSchedulerService;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionOutboxPublisherJob implements Job {

  final CommissionSchedulerService commissionSchedulerService;

  @Override
  public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
    log.info("Running CommissionOutboxPublisherJob ");
    commissionSchedulerService.processPendingMessages();
    log.info("Completed CommissionOutboxPublisherJob ");
  }
}
