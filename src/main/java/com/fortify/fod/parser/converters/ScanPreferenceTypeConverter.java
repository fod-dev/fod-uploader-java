package com.fortify.fod.parser.converters;

import com.beust.jcommander.IStringConverter;
import com.fortify.fod.fodapi.FodEnums;

public class ScanPreferenceTypeConverter implements IStringConverter<FodEnums.ScanPreferenceType> {
    @Override
    public FodEnums.ScanPreferenceType convert(String value) {
        try {
            int n = Integer.parseInt(value);
            return FodEnums.ScanPreferenceType.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.ScanPreferenceType.valueOf(value);
        }
    }
}
