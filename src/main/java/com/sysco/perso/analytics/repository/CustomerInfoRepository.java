package com.sysco.perso.analytics.repository;

import com.sysco.perso.analytics.entity.CustomerInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerInfoRepository extends JpaRepository<CustomerInfo, Long> {

    Optional<CustomerInfo> findByCustomerId(String customerId);

    boolean existsByCustomerId(String customerId);

    boolean existsByOpcoId(String opcoId);

    Optional<CustomerInfo> findByCustomerIdAndOpcoId(String customerId,String opcoId);

}
