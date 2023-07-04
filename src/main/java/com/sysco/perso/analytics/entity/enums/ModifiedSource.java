package com.sysco.perso.analytics.entity.enums;

import java.util.Arrays;

/**
 * @author ashanthiabeyrathna
 * Valid Modified Source values
 */
public enum ModifiedSource {
    SUS,
    SHOP_WEB,
    SHOP_MOBILE,
    PCM,
    PE_ANALYTICS;

    public static String[] getNames(Class<? extends Enum<?>> e) {
        return Arrays.stream(e.getEnumConstants()).map(Enum::name).toArray(String[]::new);
    }
}
