package com.fortify.fod.parser.converters;

import com.beust.jcommander.IStringConverter;
import com.fortify.fod.fodapi.FodEnums;

public class EntitlementPreferenceTypeConverter  implements IStringConverter<FodEnums.EntitlementPreferenceType> {
    @Override
    public FodEnums.EntitlementPreferenceType convert(String value) {
        try {
            if (value.equals("SingleScan")){
                value = FodEnums.EntitlementPreferenceType.SingleScanOnly.toString();
            } else if (value == "Subscription") {
                value = FodEnums.EntitlementPreferenceType.SubscriptionOnly.toString();
            }
            int n = Integer.parseInt(value);
            return FodEnums.EntitlementPreferenceType.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.EntitlementPreferenceType.valueOf(value);
        }
    }

}
