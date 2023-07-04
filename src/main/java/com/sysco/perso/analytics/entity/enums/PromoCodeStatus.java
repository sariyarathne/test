package com.sysco.perso.analytics.entity.enums;

import java.util.Arrays;

/**
 * @author ashanthiabeyrathna
 * Valid Promo Code status values
 */
public enum PromoCodeStatus {
    INIT("InIt"),
    ACTIVE("Active"),
    CONSUMED("Consumed"),
    EXPIRED("Expired"),
    INACTIVE("Inactive"),
    CLOSED("Closed");

    private final String value;

    private PromoCodeStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}


