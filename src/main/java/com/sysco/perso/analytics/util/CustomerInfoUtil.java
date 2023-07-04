package com.sysco.perso.analytics.util;

import com.sysco.perso.analytics.entity.CustomerInfo;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CustomerInfoUtil {
  public static CustomerInfo splitCustomerDetailsToCustomerIdAndOpcoId(String customerNumber) {
    CustomerInfo newCustomer = CustomerInfo.builder().build();
    String[] customer = splitToOpcoAndCustomerId(customerNumber);
    newCustomer.setCustomerId(customer[1]);
    newCustomer.setOpcoId(customer[0]);
    return newCustomer;
  }

  public static String[] splitToOpcoAndCustomerId(String customerDetails) {
    return customerDetails.split("-");
  }
}
