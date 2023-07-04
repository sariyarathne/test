package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.TransactionSystemException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@EnableAutoConfiguration
@SpringBootTest
class CustomerInfoRepositoryTestIT {

  @Autowired
  CustomerInfoRepository customerInfoRepository;

  @Test
  @Tag("createCustomer")
  @DisplayName("Verify save customerInfo when length of the CustomerNumber is Six")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerInfo_thenSuccess() {
    final String customerId = "110110";
    final String opCo = "330";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    customerInfoRepository.save(expectedCustomer);
    Optional<CustomerInfo> actualCustomer = customerInfoRepository.findByCustomerIdAndOpcoId(customerId, opCo);

    assertEquals(expectedCustomer.getOpcoId(), actualCustomer.map(CustomerInfo::getOpcoId).orElse(null));
    assertEquals(expectedCustomer.getCustomerId(), actualCustomer.map(CustomerInfo::getCustomerId).orElse(null));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo when length of the CustomerNumber is greater than Six")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithCustomerNumberGreaterThanSix_thenSuccess() {

    final String customerId = "11111111";
    final String opCo = "043";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    customerInfoRepository.save(expectedCustomer);
    Optional<CustomerInfo> actualCustomer = customerInfoRepository.findByCustomerIdAndOpcoId(customerId, opCo);

    assertEquals(expectedCustomer.getOpcoId(), actualCustomer.map(CustomerInfo::getOpcoId).orElse(null));
    assertEquals(expectedCustomer.getCustomerId(), actualCustomer.map(CustomerInfo::getCustomerId).orElse(null));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo fails when length of the CustomerNumber is less than Six")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithCustomerNumberLessThanSix_thenFail() throws TransactionSystemException {

    final String customerId = "11111";
    final String opCo = "003";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    assertThrows(TransactionSystemException.class, () -> customerInfoRepository.save(expectedCustomer));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo fails when CustomerNumber contains any special character")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithCustomerNumberContainsSpecialCharacter_thenFail() throws TransactionSystemException {

    final String customerId = "1@1111";
    final String opCo = "033";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    assertThrows(TransactionSystemException.class, () -> customerInfoRepository.save(expectedCustomer));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo fails when length of the OpCoNumber is less than Three")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithOpCoNumberLessThanThree_thenFail() throws TransactionSystemException {

    final String customerId = "11111";
    final String opCo = "32";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    assertThrows(TransactionSystemException.class, () -> customerInfoRepository.save(expectedCustomer));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo fails when length of the OpCoNumber is greater than Three")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithOpCoNumberGreaterThanThree_thenFail() throws TransactionSystemException {

    final String customerId = "11111";
    final String opCo = "0031";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    assertThrows(TransactionSystemException.class, () -> customerInfoRepository.save(expectedCustomer));
  }

  @Test
  @Tag("CreateCustomer")
  @DisplayName("Verify save customerInfo fails when OpCoNumber contains any special character")
  @WithMockUser(username = "admin", roles = {"ADMIN"})
  void whenCreateCustomerWithOpCoNumberContainsSpecialCharacter_thenFail() throws TransactionSystemException {

    final String customerId = "111111";
    final String opCo = "0@3";
    CustomerInfo expectedCustomer = CustomerInfo.builder().customerId(customerId).opcoId(opCo).build();
    assertThrows(TransactionSystemException.class, () -> customerInfoRepository.save(expectedCustomer));
  }

}
