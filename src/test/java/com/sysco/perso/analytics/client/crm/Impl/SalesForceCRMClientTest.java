package com.sysco.perso.analytics.client.crm.Impl;

import com.sysco.perso.analytics.client.crm.CRMClient;
import com.sysco.perso.analytics.client.crm.schema.CRMGenericResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@EnableAutoConfiguration
@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SalesForceCRMClientTest {

  @MockBean
  WebClient webClient;

  @Autowired
  CRMClient crmClient;

  @Mock
  private WebClient.RequestBodyUriSpec requestBodyUriSpec;

  @Mock
  private WebClient.RequestBodySpec requestBodySpec;

  @Mock
  private WebClient.RequestHeadersSpec requestHeadersSpec;

  @Mock
  private WebClient.ResponseSpec responseSpec;

  @BeforeEach
  public void beforeEach() {
    ArrayList<CRMGenericResponse> arr = new ArrayList<>();
    String[] errors = new String[0];
    CRMGenericResponse crmGenericResponse = new CRMGenericResponse();
    crmGenericResponse.setId("a1w53000000CstNAAS");
    crmGenericResponse.setSuccess(true);
    crmGenericResponse.setErrors(errors);
    arr.add(crmGenericResponse);

    Mockito.when(this.webClient.method(ArgumentMatchers.any(HttpMethod.class)))
            .thenReturn(requestBodyUriSpec);
    Mockito.when(this.requestBodyUriSpec.uri(ArgumentMatchers.anyString()))
            .thenReturn(requestBodySpec);
    Mockito.when(this.requestBodySpec.contentType(MediaType.APPLICATION_JSON))
            .thenReturn(requestBodySpec);
    Mockito.when(this.requestBodySpec.bodyValue(ArgumentMatchers.anyString()))
            .thenReturn(requestHeadersSpec);
    Mockito.when(this.requestHeadersSpec.retrieve())
            .thenReturn(responseSpec);
    Mockito.when(this.responseSpec.bodyToMono(CRMGenericResponse.class))
            .thenReturn(Mono.just(crmGenericResponse));
    Mockito.when(this.responseSpec.toBodilessEntity())
            .thenReturn(Mono.just(ResponseEntity.ok(null)));
  }

  @Test
  @Tag("waiveOffPerksMembershipFee")
  void waiveOffPerksMembershipFee_thenSuccess() {
    Mono<List<CRMGenericResponse>> listMono = crmClient.waiveOffPerksMembershipFee("056-407833");
    StepVerifier.create(listMono).expectNextCount(1).verifyComplete();
  }

  @Test
  @Tag("chargePerksMembershipFee")
  void chargePerksMembershipFee_thenSuccess() {
    Mono<List<CRMGenericResponse>> listMono = crmClient.chargePerksMembershipFee("056-407833");
    StepVerifier.create(listMono).expectNextCount(1).verifyComplete();
  }
}
