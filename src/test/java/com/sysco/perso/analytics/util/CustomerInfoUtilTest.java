package com.sysco.perso.analytics.util;

import com.sysco.perso.analytics.entity.CustomerInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest
public class CustomerInfoUtilTest {

    @Test
    @Tag("splitCustomerDetailsToCustomerIdAndOpcoId")
    void splitCustomerDetailsToCustomerIdAndOpcoId_thenSuccess() {
        String customerNumber = "022-941684";
        CustomerInfo newCustomer = CustomerInfo.builder().build();
        String[] customer = CustomerInfoUtil.splitToOpcoAndCustomerId(customerNumber);
        newCustomer.setCustomerId(customer[1]);
        newCustomer.setOpcoId(customer[0]);

        assertEquals(newCustomer, CustomerInfoUtil.splitCustomerDetailsToCustomerIdAndOpcoId(customerNumber));
    }

    @Test
    @Tag("splitCustomerDetailsToCustomerIdAndOpcoId")
    void splitCustomerDetailsToCustomerIdAndOpcoId_throwArrayIndexOutOfBoundsException() {
        String customerNumber = "022941684";
        assertThrows(ArrayIndexOutOfBoundsException.class, () ->CustomerInfoUtil.splitCustomerDetailsToCustomerIdAndOpcoId(customerNumber));
    }

    @Test
    @Tag("splitToOpcoAndCustomerId")
    void splitToOpcoAndCustomerId_thenSuccess() {
        String[] splitArr = new String[2];
        splitArr[0] = "022";
        splitArr[1] = "941684";

        assertArrayEquals(splitArr, CustomerInfoUtil.splitToOpcoAndCustomerId("022-941684"));
    }
}
