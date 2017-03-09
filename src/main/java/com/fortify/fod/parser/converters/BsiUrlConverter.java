package com.fortify.fod.parser.converters;

import com.beust.jcommander.IStringConverter;
import com.fortify.fod.parser.BsiUrl;

public class BsiUrlConverter implements IStringConverter<BsiUrl> {
    @Override
    public BsiUrl convert(String value) {
        return new BsiUrl(value);
    }
}
