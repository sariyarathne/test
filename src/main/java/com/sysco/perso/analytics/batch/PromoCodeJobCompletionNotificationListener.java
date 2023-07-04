package com.sysco.perso.analytics.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;

public class PromoCodeJobCompletionNotificationListener extends JobExecutionListenerSupport {
  private static final Logger log = LoggerFactory.getLogger(PromoCodeJobCompletionNotificationListener.class);

  @Override
  public void afterJob(final JobExecution promoCodeJobExecution) {
    if (promoCodeJobExecution.getStatus() == BatchStatus.COMPLETED) {
      log.info("PromoCode batch job finished. start Time {} - end Time {}", promoCodeJobExecution.getStartTime(),
              promoCodeJobExecution.getEndTime());
    }
  }
}
