package com.sysco.perso.analytics.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

import static java.util.UUID.randomUUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PromoCodeUtil {
  private static final SecureRandom RANDOM = new SecureRandom();

  public static String getCurrentPerksPromoCode() {
    final LocalDate nextDate = LocalDate.now();
    return getPromoCodeString(nextDate);
  }

  private static String getPromoCodeString(final LocalDate date) {
    return "PERKS" + date.getMonth() + date.getYear();
  }

  public static String getNextPerksPromoCode() {
    final LocalDate nextDate = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
    return getPromoCodeString(nextDate);
  }

  public static String getPromoCodeUUID() {
    return randomUUID().toString();
  }


  public static String getDTCDescription(final double rewardValue) {
    return "Your $" + (int) Math.round(rewardValue) + " reward is here! Use code:";
  }

  public static String getDTCPromoCode(final double rewardValue) {
    final String alpha = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    StringBuilder stringBuilder = new StringBuilder();

    for (int i = 0; i < 3; i++) {
      stringBuilder.append(alpha.charAt(RANDOM.nextInt(alpha.length())));
    }

    return "SAVE" + (int) Math.round(rewardValue) + stringBuilder.toString().toUpperCase();
  }

}
