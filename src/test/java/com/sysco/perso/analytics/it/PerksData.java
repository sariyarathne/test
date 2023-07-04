package com.sysco.perso.analytics.it;

import com.sysco.perso.analytics.entity.RewardSourceInfo;
import com.sysco.perso.analytics.event.payload.AccountAttributeChangeEvent;
import com.sysco.perso.analytics.event.payload.ChangeEventHeader;
import com.sysco.perso.analytics.event.payload.NewFields;

import java.util.List;

public class PerksData {
  public static List<RewardSourceInfo> getRewardSources() {
    return List.of(RewardSourceInfo.builder().rewardSourceId("1").rewardSource("DTC").build(),
            RewardSourceInfo.builder().rewardSourceId("2").rewardSource("SYSCOPERKS").build());
  }

  public static AccountAttributeChangeEvent getEnrollmentEvent(final String enrollmentType, final String accountId) {
    final AccountAttributeChangeEvent accountAttributeChangeEvent = new AccountAttributeChangeEvent();

    final ChangeEventHeader changeEventHeader = new ChangeEventHeader();
    changeEventHeader.setChangeType("scheduled:Perks_Enrollment_Date__c");

    final NewFields newFields = new NewFields();
    newFields.setSyscoPerks(enrollmentType);
    newFields.setAccountId(accountId);

    accountAttributeChangeEvent.setNewFields(newFields);
    accountAttributeChangeEvent.setChangeEventHeader(changeEventHeader);
    return accountAttributeChangeEvent;
  }
}
