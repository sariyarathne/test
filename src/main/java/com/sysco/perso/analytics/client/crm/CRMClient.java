package com.sysco.perso.analytics.client.crm;

import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import reactor.core.publisher.Mono;

import java.util.List;

public interface CRMClient {
  Mono<List<CRMGenericResponse>> chargePerksMembershipFee(String customerNumber);

  Mono<List<CRMGenericResponse>> waiveOffPerksMembershipFee(String customerNumber);

 }
