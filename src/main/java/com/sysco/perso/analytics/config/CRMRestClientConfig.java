package com.sysco.perso.analytics.config;

import com.sysco.perso.analytics.client.crm.impl.SalesForceCRMClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.InMemoryReactiveOAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.InMemoryReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CRMRestClientConfig {
  @Value("${crm.api.v3.base.url}")
  private String baseUrl;

  @Value("${oauth.token.url}")
  private String tokenUrl;

  @Value("${application.client.id}")
  private String clientId;

  @Value("${application.client.secret}")
  private String clientSecret;

  @Bean
  public WebClient reactiveCRMWebClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
    ServerOAuth2AuthorizedClientExchangeFilterFunction oAuth =
            new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);
    oAuth.setDefaultClientRegistrationId("Perso");
    return WebClient.builder().filter(oAuth).baseUrl(baseUrl).build();
  }

  @Bean
  public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(
          ReactiveClientRegistrationRepository clientRegistrationRepository) {

    ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider =
            ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
                    .authorizationCode()
                    .refreshToken()
                    .clientCredentials()
                    .password()
                    .build();

    AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager authorizedClientManager =
            new AuthorizedClientServiceReactiveOAuth2AuthorizedClientManager(
                    clientRegistrationRepository,
                    new InMemoryReactiveOAuth2AuthorizedClientService(clientRegistrationRepository));
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

    return authorizedClientManager;
  }

  @Bean
  public ReactiveClientRegistrationRepository clientRegistrationRepository() {
    ClientRegistration registration = ClientRegistration.withRegistrationId("Perso")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .tokenUri(tokenUrl)
            .scope("crm-v3.Case.create", "crm-v3.Case.read", "crm-v3.Case.update",
                    "crm-v3.Support_Request_Line_Item__c.create")
            .build();

    return new InMemoryReactiveClientRegistrationRepository(registration);
  }

  @Bean
  public SalesForceCRMClient salesForceCRMClient(WebClient reactiveCRMWebClient) {
    return new SalesForceCRMClient(reactiveCRMWebClient);
  }
}
