package com.solpyra.domain.schedule.jobs;

import com.solpyra.domain.schedule.services.CommissionSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionCalcJob implements Job {

    private final CommissionSchedulerService schedulerService;

    @Override
    public void execute(JobExecutionContext context) {
        log.info("Starting daily commission calculation job at {}", java.time.ZonedDateTime.now());
        try {
            schedulerService.createOutboxForEligibleOrders();
            log.info("Commission job completed successfully.");
        } catch (Exception e) {
            log.error("Error during commission job: {}", e.getMessage(), e);
        }
    }
}