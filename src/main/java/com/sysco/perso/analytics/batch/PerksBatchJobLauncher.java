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
public class PerksBatchJobLauncher {

  private static final Logger logger = LoggerFactory.getLogger(PerksBatchJobLauncher.class);

  @Autowired
  @Qualifier("simpleBatchJobLauncher")
  private SimpleJobLauncher jobLauncher;

  @Autowired
  @Qualifier("perksJob")
  Job perksJob;

  @Scheduled(cron = "${perks.fee.update.cron.expression}")
  public void performPerksBatchJob() throws JobInstanceAlreadyCompleteException,
          JobParametersInvalidException, JobRestartException {
    try {
      JobParameters param = new JobParametersBuilder().addString("JobID", "PERKS-" + System.currentTimeMillis() / 60000)
              .toJobParameters();
      jobLauncher.run(perksJob, param);
    } catch (JobExecutionAlreadyRunningException exception) {
      logger.warn("Perks batch job is not running in this instance.");
    }
  }
}
