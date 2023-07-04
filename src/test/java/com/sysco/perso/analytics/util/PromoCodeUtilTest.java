package com.sysco.perso.analytics.util;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

@EnableAutoConfiguration
@SpringBootTest
public class PromoCodeUtilTest {

    private static final SecureRandom RANDOM = new SecureRandom();

    @Test
    @Tag("getCurrentPerksPromoCode")
    void getCurrentPerksPromoCode_thenSuccess() {
        final LocalDate nextDate = LocalDate.now();
        assertEquals("PERKS" + nextDate.getMonth() + nextDate.getYear(), PromoCodeUtil.getCurrentPerksPromoCode());
    }

    @Test
    @Tag("getNextPerksPromoCode")
    void getNextPerksPromoCode_thenSuccess() {
        final LocalDate nextDate = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
        assertEquals("PERKS" + nextDate.getMonth() + nextDate.getYear(), PromoCodeUtil.getNextPerksPromoCode());
    }

    @Test
    @Tag("getDTCDescription")
    void getDTCDescription_thenSuccess() {
        final double rewardValue = 10;
        assertEquals("Your $" + (int) Math.round(rewardValue) + " reward is here! Use code:", PromoCodeUtil.getDTCDescription(rewardValue));
    }

    @Test
    @Tag("getDTCPromoCode")
    void getDTCPromoCode_thenFailed() {
        final double rewardValue = 10;
        assertNotEquals(ArgumentMatchers.anyString(), PromoCodeUtil.getDTCPromoCode(rewardValue));
    }

    @Test
    @Tag("getPromoCodeUUID")
    void getPromoCodeUUID_thenFailed() {
        assertNotEquals(ArgumentMatchers.anyString(), PromoCodeUtil.getPromoCodeUUID());
    }

    @Test
    @Tag("getPromoCodeUUID")
    void getPromoCodeUUID_thenSuccess() {
        Pattern UUID_REGEX_PATTERN = Pattern.compile("^[{]?[0-9a-fA-F]{8}-([0-9a-fA-F]{4}-){3}[0-9a-fA-F]{12}[}]?$");
        assertTrue(UUID_REGEX_PATTERN.matcher(PromoCodeUtil.getPromoCodeUUID()).matches());
    }
}
