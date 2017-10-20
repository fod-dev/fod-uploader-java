package com.fortify.fod.parser.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.fortify.fod.parser.BsiTokenParser;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class BsiTokenValidator implements IParameterValidator {

    private static BsiTokenParser parser = new BsiTokenParser();

    @Override
    public void validate(String name, String value) throws ParameterException {
        try {
            parser.parse(value);
        } catch (URISyntaxException | UnsupportedEncodingException e) {
            throw new ParameterException(e.getMessage());
        }
    }
}
