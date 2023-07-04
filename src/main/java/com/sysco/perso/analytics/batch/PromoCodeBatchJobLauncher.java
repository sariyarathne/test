package com.sysco.perso.analytics.batch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class PromoCodeBatchJobLauncher {

  private static final Logger logger = LoggerFactory.getLogger(PromoCodeBatchJobLauncher.class);

  @Autowired
  @Qualifier("simpleBatchJobLauncher")
  private SimpleJobLauncher jobLauncher;

  @Autowired
  @Qualifier("promoCodeJob")
  Job promoCodeJob;

  @Scheduled(cron = "0 1 0 * * *")
  public void performPromoCodeBatchJob()
          throws JobInstanceAlreadyCompleteException,
          JobParametersInvalidException, JobRestartException {
    try {
      JobParameters param =
              new JobParametersBuilder().addString("JobID", "PROMOCODE-" + System.currentTimeMillis() / 60000)
                      .toJobParameters();
      jobLauncher.run(promoCodeJob, param);
    } catch (JobExecutionAlreadyRunningException exception) {
      logger.warn("Promo Code batch job is not running in this instance.");
    }
  }
}
