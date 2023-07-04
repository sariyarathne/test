package com.sysco.perso.analytics.actuator;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestParam;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Component
@Endpoint(id = "batch-job")
public class ManualBatchJobLauncher {

  @Autowired
  @Qualifier("asyncJobLauncher")
  private SimpleJobLauncher jobLauncher;

  @Autowired
  @Qualifier("perksJob")
  Job perksJob;

  @Autowired
  @Qualifier("promoCodeJob")
  Job promoCodeJob;

  @Autowired
  JobExplorer jobExplorer;

  @WriteOperation
  @ApiResponses(value = {
          @ApiResponse(responseCode = "200", description = "Batch Job Run Success.",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = RunJobResponseDTO.class))}),
          @ApiResponse(responseCode = "400", description = "Batch Job Run Failed.",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = RunJobErrorResponseDTO.class))}),
          @ApiResponse(responseCode = "500", description = "Internal Server Error.",
                  content = {@Content(mediaType = "application/json",
                          schema = @Schema(implementation = RunJobErrorResponseDTO.class))}),
  })
  public ResponseEntity<Object> batchJobTrigger(
          @RequestParam("command") @NotNull(message = "command cannot be null.") String command,
          @RequestParam("batchJob") @NotNull(message = "batchJob cannot be null.") String batchJob
  ) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException,
          JobParametersInvalidException, JobRestartException {

    final String timeStamp = String.valueOf(System.currentTimeMillis() / 60000);
    String batchJobId = "";

    if ("run".equals(command)) {
      if ("perks".equals(batchJob)) {
        Set<JobExecution> perksJobExecutions = jobExplorer.findRunningJobExecutions(perksJob.getName());
        if(perksJobExecutions.isEmpty()) {
          batchJobId = "PERKS-" + timeStamp;
          jobLauncher.run(perksJob, new JobParametersBuilder().addString("JobID", batchJobId)
                  .toJobParameters());
        }
        else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RunJobErrorResponseDTO("Perks job is already running."));
        }
      } else if ("promoCode".equals(batchJob)) {
        Set<JobExecution> promoCodeJobExecutions = jobExplorer.findRunningJobExecutions(promoCodeJob.getName());
        if(promoCodeJobExecutions.isEmpty()) {
          batchJobId = "PROMOCODE-" + timeStamp;
          jobLauncher.run(promoCodeJob, new JobParametersBuilder().addString("JobID", batchJobId)
                  .toJobParameters());
        }
        else {
          return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new RunJobErrorResponseDTO("Promo Code job is already running."));
        }
      }
      else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RunJobErrorResponseDTO("Invalid Job Name."));
      }
    }
    else {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new RunJobErrorResponseDTO("Invalid Command."));
    }

    return ResponseEntity.status(HttpStatus.OK).body(new RunJobResponseDTO(batchJobId));
  }
}
