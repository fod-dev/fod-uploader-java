package com.fortify.fod.parser.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;

import java.io.File;

public class FileValidator implements IParameterValidator {
    public void validate(String name, String value) throws ParameterException {
        final long maxFileSize = 5000 * 1024 * 1024L;

        File f = new File(value);

        if (f.length() > maxFileSize) {
            throw new ParameterException("Parameter " + name +
                    " should point to a file smaller than " + maxFileSize + " bytes.");
        }
    }
}
