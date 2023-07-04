package com.sysco.perso.analytics.batch;

import com.sysco.perso.analytics.entity.PromoCode;
import com.sysco.perso.analytics.service.PromoCodesService;
import lombok.NonNull;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PromoCodeItemWriterImpl implements ItemWriter<PromoCode> {
  private final PromoCodesService promoCodesService;

  @Autowired
  public PromoCodeItemWriterImpl(final PromoCodesService promoCodesService) {
    this.promoCodesService = promoCodesService;
  }

  @Override
  public void write(@NonNull List<? extends PromoCode> items) {
    this.promoCodesService.processPromoCodesChanges((List<PromoCode>) items);
  }
}
