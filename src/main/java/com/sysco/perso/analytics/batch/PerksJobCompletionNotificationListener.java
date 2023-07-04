package com.sysco.perso.analytics.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class PerksJobCompletionNotificationListener extends JobExecutionListenerSupport {
  private static final Logger log = LoggerFactory.getLogger(PerksJobCompletionNotificationListener.class);

  @Override
  public void afterJob(final JobExecution perksJobExecution) {
    if (perksJobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("Perks batch job finished. start Time {} - end Time {}", perksJobExecution.getStartTime(),
              perksJobExecution.getEndTime());
    }
  }
}
