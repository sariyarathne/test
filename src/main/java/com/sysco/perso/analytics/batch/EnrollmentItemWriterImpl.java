package com.sysco.perso.analytics.batch;

import com.sysco.perso.analytics.entity.Enrollment;
import com.sysco.perso.analytics.service.PerksFacade;
import com.sysco.perso.analytics.service.PromoCodesService;
import lombok.NonNull;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EnrollmentItemWriterImpl implements ItemWriter<Enrollment> {

  private final PerksFacade perksFacade;

  private final PromoCodesService promoCodesService;

  @Autowired
  public EnrollmentItemWriterImpl(final PerksFacade perksService, final PromoCodesService promoCodesService) {
    this.perksFacade = perksService;
    this.promoCodesService = promoCodesService;
  }

  @Override
  public void write(@NonNull List<? extends Enrollment> items) {
    this.perksFacade.processBatch((List<Enrollment>) items);
    this.promoCodesService.createBatchPerksPromoCodes((List<Enrollment>) items);
  }
}
