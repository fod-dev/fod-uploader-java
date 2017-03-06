package com.fortify.fod.parser;


import com.beust.jcommander.IStringConverter;
import com.fortify.fod.fodapi.FodEnums;

public class AuditPreferenceTypeConverter implements IStringConverter<FodEnums.AuditPreferenceType> {
    @Override
    public FodEnums.AuditPreferenceType convert(String value) {
        try {
            int n = Integer.parseInt(value);
            return FodEnums.AuditPreferenceType.fromInt(n);
        } catch(NumberFormatException ex) {
            return FodEnums.AuditPreferenceType.valueOf(value);
        }
    }
}