package com.sysco.perso.analytics.client.crm.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.sysco.perso.analytics.client.crm.CRMClient;
import com.sysco.perso.analytics.client.crm.schema.Account;
import com.sysco.perso.analytics.client.crm.schema.ApproveCaseRequest;
import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import com.sysco.perso.analytics.client.crm.schema.Case;
import com.sysco.perso.analytics.client.crm.schema.Product;
import com.sysco.perso.analytics.client.crm.schema.SupportRequestLineItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SalesForceCRMClient implements CRMClient {

  private static final Logger logger = LoggerFactory.getLogger(SalesForceCRMClient.class);

  private static final String CREATE_CASE = "/sobjects/Case";

  private static final String CREATE_LINE_ITEM = "/sobjects/Support_Request_Line_Item__c";

  private static final double PERKS_MEMBERSHIP_FEE = 149;

  private static final int MAX_ATTEMPTS = 3;

  private static final SecureRandom RANDOM = new SecureRandom();

  @Value("${salesforce.case.recordTypeId}")
  private String caseRecordTypeId;

  @Value("${salesforce.case.ownerId}")
  private String caseOwnerId;

  @Value("${salesforce.lineItem.perksFee.supc}")
  private String perksFeeSupc;

  private final WebClient webClient;

  @Autowired
  public SalesForceCRMClient(final WebClient webClient) {
    this.webClient = webClient;
  }

  @Override
  public Mono<List<CRMGenericResponse>> chargePerksMembershipFee(final String customerNumber) {
    return createCase(customerNumber)
            .flatMap(caseResponse -> createSupportRequestLineItem(caseResponse.getId(), PERKS_MEMBERSHIP_FEE)
                    .flatMap(lineItemResponse -> approveCase(caseResponse.getId())
                            .map(approveResponse -> Collections.singletonList(lineItemResponse))))
            .onErrorResume(this::logError);
  }

  @Override
  public Mono<List<CRMGenericResponse>> waiveOffPerksMembershipFee(final String customerNumber) {
    return createCase(customerNumber)
            .flatMap(caseResponse -> createSupportRequestLineItem(caseResponse.getId(), PERKS_MEMBERSHIP_FEE)
                    .flatMap(lineItemResponse -> createSupportRequestLineItem(caseResponse.getId(),
                            -PERKS_MEMBERSHIP_FEE)
                            .flatMap(lineItemResponse2 -> approveCase(caseResponse.getId())
                                    .map(approveResponse -> Arrays.asList(lineItemResponse, lineItemResponse2)))))
            .onErrorResume(this::logError);
  }

  private Mono<? extends List<CRMGenericResponse>> logError(final Throwable t) {
    logger.error("Support Request creation failed with error {}", t.getMessage());
    return Mono.just(Collections.emptyList());
  }

  private Mono<CRMGenericResponse> createCase(final String accountId) {
    final String perksMembershipFee = "Perks Membership Fee";
    final Case caseBody = Case.builder()
            .account(Account.builder().accountId(accountId).build())
            .recordTypeId(caseRecordTypeId)
            .ownerID(caseOwnerId)
            .subject(perksMembershipFee)
            .description(perksMembershipFee)
            .reasonCode(perksMembershipFee).requestType("Credit Memo")
            .build();

    return this.webClient
            .method(HttpMethod.POST)
            .uri(CREATE_CASE)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertToJsonRequestObject(caseBody))
            .retrieve()
            .bodyToMono(CRMGenericResponse.class)
            .retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(getRandomBackoff())).jitter(0.4)
                    .doAfterRetry(
                            retrySignal -> logger.warn("Create case failed with {} and total retries in a row is {}",
                                    retrySignal.failure(), retrySignal.totalRetriesInARow())));
  }

  private long getRandomBackoff() {
    return RANDOM.nextLong(2, 10);
  }

  private Mono<CRMGenericResponse> createSupportRequestLineItem(String supportRequestNumber, double amount) {

    final SupportRequestLineItem supportRequestLineItemBody =
            SupportRequestLineItem.builder().supportRequestNumber(supportRequestNumber)
                    .product(Product.builder().supcNumber(perksFeeSupc).build())
                    .itemAmount(amount)
                    .quantity(1)
                    .build();

    return this.webClient
            .method(HttpMethod.POST)
            .uri(CREATE_LINE_ITEM)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertToJsonRequestObject(supportRequestLineItemBody))
            .retrieve()
            .bodyToMono(CRMGenericResponse.class)
            .retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(getRandomBackoff())).jitter(0.7)
                    .doAfterRetry(retrySignal -> logger.warn(
                            "Create line item failed with {} and total retries in a row is {}",
                            retrySignal.failure(), retrySignal.totalRetriesInARow())));
  }

  private Mono<ResponseEntity<Void>> approveCase(final String caseId) {
    ApproveCaseRequest caseApproveRequest = ApproveCaseRequest.builder().type("Case").status("Approved").build();

    return this.webClient
            .method(HttpMethod.PATCH)
            .uri(CREATE_CASE + "/" + caseId)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(convertToJsonRequestObject(caseApproveRequest))
            .retrieve()
            .toBodilessEntity()
            .retryWhen(Retry.backoff(MAX_ATTEMPTS, Duration.ofSeconds(getRandomBackoff())).jitter(0.3)
                    .doAfterRetry(
                            retrySignal -> logger.warn("Approve case failed with {} and total retries in a row is {}",
                                    retrySignal.failure(), retrySignal.totalRetriesInARow())));
  }

  private String convertToJsonRequestObject(final Object object) {
    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();
    String requestBody = "";

    try {
      requestBody = objectWriter.writeValueAsString(object);
    } catch (JsonProcessingException e) {
      logger.error("Json parse error {}", e.getMessage());
    }

    return requestBody;
  }
}
