package com.sysco.perso.analytics.config;

import com.sysco.perso.analytics.batch.PromoCodeItemWriterImpl;
import com.sysco.perso.analytics.batch.PromoCodeJobCompletionNotificationListener;
import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.entity.enums.PromoCodeStatus;
import com.sysco.perso.analytics.repository.PromoCodeRepository;
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
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.data.domain.Sort;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class PromoCodeBatchJobConfig {

  @Bean
  @Qualifier("promoCodeReader")
  public RepositoryItemReader<PromoCode> reader(final PromoCodeRepository promoCodeRepository) {
    RepositoryItemReader<PromoCode> promoCodesReader = new RepositoryItemReader<>();
    promoCodesReader.setRepository(promoCodeRepository);
    promoCodesReader.setMethodName("findAllByPromoCodeStatusNotIn");
    promoCodesReader.setPageSize(30);
    promoCodesReader.setArguments(List.of(List.of(PromoCodeStatus.EXPIRED)));
    Map<String, Sort.Direction> sorts = new HashMap<>();
    sorts.put("uuid", Sort.Direction.ASC);
    promoCodesReader.setSort(sorts);
    return promoCodesReader;
  }

  @Bean
  @Qualifier("promoCodeWriter")
  public PromoCodeItemWriterImpl writer(final PromoCodesService promoCodesService) {
    return new PromoCodeItemWriterImpl(promoCodesService);
  }

  @Bean
  @Qualifier("promoCodeStep")
  public Step step(final StepBuilderFactory promoCodeStepBuilderFactory,
                   @Qualifier("promoCodeReader") final RepositoryItemReader<PromoCode> reader,
                   @Qualifier("promoCodeWriter") PromoCodeItemWriterImpl writer) {
    return promoCodeStepBuilderFactory.get("promoCodeStep")
            .<PromoCode, PromoCode>chunk(30)
            .reader(reader)
            .writer(writer)
            .build();
  }

  @Bean
  @Qualifier("promoCodeJobCompletionNotificationListener")
  public PromoCodeJobCompletionNotificationListener jobCompletionNotificationListener() {
    return new PromoCodeJobCompletionNotificationListener();
  }

  @Bean
  @Qualifier("promoCodeJob")
  public Job promoCodeJob(@Qualifier("promoCodeJobCompletionNotificationListener")
                          final PromoCodeJobCompletionNotificationListener listener,
                          @Qualifier("promoCodeStep") final Step step,
                          final JobBuilderFactory promoCodeJobBuilderFactory) {
    return promoCodeJobBuilderFactory.get("promoCodeJob")
            .incrementer(new RunIdIncrementer())
            .listener(listener)
            .start(step)
            .build();
  }
}
