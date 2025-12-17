package com.solpyra.configuration;

import com.solpyra.domain.schedule.jobs.CommissionCalcJob;
import com.solpyra.domain.schedule.jobs.CommissionOutboxPublisherJob;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Date;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SimpleTriggerFactoryBean;

@Configuration
public class QuartzJobConfiguration {

  @Bean
  public JobDetailFactoryBean commissionJobDetail() {
    JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
    factoryBean.setJobClass(CommissionCalcJob.class);
    factoryBean.setDescription("Calculate commissions for completed orders (15 days old)");
    factoryBean.setDurability(true);
    return factoryBean;
  }

  @Bean
  public SimpleTriggerFactoryBean commissionTrigger(JobDetailFactoryBean commissionJobDetail) {
    SimpleTriggerFactoryBean trigger = new SimpleTriggerFactoryBean();
    trigger.setJobDetail(commissionJobDetail.getObject());
    trigger.setRepeatInterval(24 * 60 * 60 * 1000); // every 24h
    trigger.setStartDelay(calculateDelayToHour(1));
    trigger.setRepeatCount(SimpleTrigger.REPEAT_INDEFINITELY);
    return trigger;
  }

  @Bean
  public JobDetail outboxPublisherJobDetail() {
    return JobBuilder.newJob(CommissionOutboxPublisherJob.class)
        .withIdentity("outboxPublisherJob")
        .storeDurably()
        .build();
  }

  @Bean
  public Trigger outboxPublisherTrigger(JobDetail outboxPublisherJobDetail) {
    return TriggerBuilder.newTrigger()
        .forJob(outboxPublisherJobDetail)
        .withIdentity("outboxPublisherTrigger")
        .startAt(calculateDelayToNextHour())
        .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(12)) // run every 1 minute
        .build();
  }

  private Date calculateDelayToNextHour() {
    ZonedDateTime now = ZonedDateTime.now();

    if(now.getMinute() == 0){
      return Date.from(now.toInstant());
    }

    ZonedDateTime nextRun = now.withHour((now.getHour() + 1) % 24).withMinute(0).withSecond(0);
    return Date.from(nextRun.toInstant());
  }

  private long calculateDelayToHour(int hour) {
    ZonedDateTime now = ZonedDateTime.now();
    ZonedDateTime nextRun = now.withHour(hour).withMinute(0).withSecond(0);
    if (now.isAfter(nextRun)) nextRun = nextRun.plusDays(1);
    return Duration.between(now, nextRun).toMillis();
  }
}
