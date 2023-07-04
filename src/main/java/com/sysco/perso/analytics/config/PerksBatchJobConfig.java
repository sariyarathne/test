package com.sysco.perso.analytics.config;

import com.sysco.perso.analytics.batch.EnrollmentItemWriterImpl;
import com.sysco.perso.analytics.batch.PerksJobCompletionNotificationListener;
import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.entity.enums.EnrollmentType;
import com.sysco.perso.analytics.repository.EnrollmentRepository;
import com.sysco.perso.analytics.service.PerksFacade;
import com.sysco.perso.analytics.service.PromoCodesService;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class PerksBatchJobConfig {

  @Bean
  @Qualifier("perksReader")
  public RepositoryItemReader<Enrollment> perksReader(final EnrollmentRepository enrollmentRepository) {
    RepositoryItemReader<Enrollment> reader = new RepositoryItemReader<>();
    reader.setRepository(enrollmentRepository);
    reader.setMethodName("findAllByEnrollmentTypeIn");
    reader.setPageSize(30);
    reader.setArguments(List.of(List.of(EnrollmentType.FREE_TRIAL, EnrollmentType.ENROLLED)));
    Map<String, Sort.Direction> sorts = new HashMap<>();
    sorts.put("id", Sort.Direction.ASC);
    reader.setSort(sorts);
    return reader;
  }

  @Bean
  @Qualifier("perksWriter")
  public EnrollmentItemWriterImpl perksWriter(final PerksFacade perksService,
                                              final PromoCodesService promoCodesService) {
    return new EnrollmentItemWriterImpl(perksService, promoCodesService);
  }

  @Bean
  @Qualifier("perksStep")
  public Step perkStep(final StepBuilderFactory perksStepBuilderFactory,
                       @Qualifier("perksReader") final RepositoryItemReader<Enrollment> perksReader,
                       @Qualifier("perksWriter") EnrollmentItemWriterImpl perksWriter) {
    return perksStepBuilderFactory.get("perkStep")
            .<Enrollment, Enrollment>chunk(30)
            .reader(perksReader)
            .writer(perksWriter)
            .build();
  }

  @Bean
  @Qualifier("perksListener")
  public PerksJobCompletionNotificationListener perksListener() {
    return new PerksJobCompletionNotificationListener();
  }

  @Bean
  @Qualifier("perksJob")
  public Job perksJob(
          @Qualifier("perksListener") final PerksJobCompletionNotificationListener perksListener,
          @Qualifier("perksStep") final Step perkStep,
          final JobBuilderFactory perksJobBuilderFactory) {
    return perksJobBuilderFactory.get("perksJob")
            .incrementer(new RunIdIncrementer())
            .listener(perksListener)
            .start(perkStep)
            .build();
  }

  @Bean
  @Qualifier("simpleBatchJobLauncher")
  public SimpleJobLauncher getPerksJobLauncher(final JobRepository perksJobRepository) {
    SimpleJobLauncher perksLauncher = new SimpleJobLauncher();
    perksLauncher.setJobRepository(perksJobRepository);
    return perksLauncher;
  }
}
