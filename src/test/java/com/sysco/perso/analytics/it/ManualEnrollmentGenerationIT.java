package com.sysco.perso.analytics.it;

import com.sysco.perso.analytics.entity.CustomerInfo;
import com.sysco.perso.analytics.event.impl.PerksOfferFulfillmentEventConsumer;
import com.sysco.perso.analytics.event.payload.OfferFulfillmentEvent;
import com.sysco.perso.analytics.repository.CustomerInfoRepository;
import com.sysco.perso.analytics.service.PerksFacade;
import com.sysco.perso.analytics.testutils.AvroUtil;
import com.sysco.perso.analytics.testutils.FileUtil;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@EnableAutoConfiguration()
@SpringBootTest()
public class ManualEnrollmentGenerationIT {

  @Autowired
  PerksFacade perksFacade;
  @Autowired
  CustomerInfoRepository customerInfoRepository;

  @Autowired
  PerksOfferFulfillmentEventConsumer consumer;

  private final FileUtil fileUtil = new FileUtil();

  private final Logger logger = LoggerFactory.getLogger(ManualEnrollmentGenerationIT.class);

  private List<String> getMissingAccountID(List<String> userListFromFile) {

    List<String> missingCustomers = new ArrayList<>();
    userListFromFile.forEach(accountID -> {
      Optional<CustomerInfo> byCustomerIdAndOpcoId =
              customerInfoRepository.findByCustomerIdAndOpcoId(accountID.split("-")[1], accountID.split("-")[0]);
      if (byCustomerIdAndOpcoId.isEmpty()) {
        missingCustomers.add(accountID);
      }
    });

    return missingCustomers;
  }

  private List<String> processMissingCustomerIDList() {
    List<String> customerListFromFile = fileUtil.readCSVFile("src/test/resources/customer_data/EnrolledCustomers.csv");
    List<String> missingCustomerIds = getMissingAccountID(customerListFromFile);

    logger.info("MissingCustomerID Count {}", missingCustomerIds.size());
    logger.info("MissingCustomerID List {}", missingCustomerIds);

    return missingCustomerIds;
  }

  @BeforeAll
  static void beforeAll() {
    TimeZone.setDefault(TimeZone.getTimeZone("America/Chicago"));
  }

  @Test()
  @Disabled
  void generatePromoCodes() {
    List<String> accountIds = processMissingCustomerIDList();
    accountIds.stream().map(String::strip).forEach(
            accountId -> perksFacade.enroll(PerksData.getEnrollmentEvent("Enrolled", accountId)));
  }

  @Test()
  @Disabled
  void generateDtcPromoCodes() throws InterruptedException {
    List<OfferFulfillmentEvent> list =
            fileUtil.readJsonArray("/customer_data/churn.json", OfferFulfillmentEvent[].class);
    list.forEach(x -> consumer.accept(AvroUtil.getMockConsumerRecord(x)));
  }
}
