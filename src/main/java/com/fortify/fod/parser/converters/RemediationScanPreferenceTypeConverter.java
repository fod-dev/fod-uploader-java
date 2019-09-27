package com.fortify.fod.parser.converters;

import com.beust.jcommander.IStringConverter;
import com.fortify.fod.fodapi.FodEnums;

public class RemediationScanPreferenceTypeConverter  implements IStringConverter<FodEnums.RemediationScanPreferenceType> {
    @Override
    public FodEnums.RemediationScanPreferenceType convert(String value) {
        try {
            int n = Integer.parseInt(value);
            return FodEnums.RemediationScanPreferenceType.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.RemediationScanPreferenceType.valueOf(value);
        }
    }
}
