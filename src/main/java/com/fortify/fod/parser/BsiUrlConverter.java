package com.fortify.fod.parser;

import com.beust.jcommander.IStringConverter;

public class BsiUrlConverter implements IStringConverter<BsiUrl> {
    @Override
    public BsiUrl convert(String value) {
        return new BsiUrl(value);
    }
}
