package com.fortify.fod.parser.converters;

import com.beust.jcommander.IStringConverter;
import com.fortify.fod.fodapi.FodEnums;

public class AuditPreferenceConverter implements IStringConverter<FodEnums.AuditPreferenceTypes> {
    @Override
    public FodEnums.AuditPreferenceTypes convert(String value) {
        try {
            if (value.equals("Manual")){
                value = FodEnums.AuditPreferenceTypes.Manual.toString();
            } else if (value == "Automated") {
                value = FodEnums.AuditPreferenceTypes.Automated.toString();
            }
            int n = Integer.parseInt(value);
            return FodEnums.AuditPreferenceTypes.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.AuditPreferenceTypes.valueOf(value);
        }
    }
}
