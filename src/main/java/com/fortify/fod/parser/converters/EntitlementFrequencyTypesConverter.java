package com.fortify.fod.parser.converters;

import com.fortify.fod.fodapi.FodEnums;

public class EntitlementFrequencyTypesConverter {
    public FodEnums.EntitlementFrequencyTypes convert(String value) {
        try {
            if (value.equals("Manual")){
                value = FodEnums.EntitlementFrequencyTypes.SingleScan.toString();
            } else if (value == "Automated") {
                value = FodEnums.EntitlementFrequencyTypes.Subscription.toString();
            }
            int n = Integer.parseInt(value);
            return FodEnums.EntitlementFrequencyTypes.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.EntitlementFrequencyTypes.valueOf(value);
        }
    }
}
