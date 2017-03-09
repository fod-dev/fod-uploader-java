package com.fortify.fod.parser.validators;

import com.beust.jcommander.IParameterValidator;
import com.beust.jcommander.ParameterException;
import com.fortify.fod.parser.BsiUrl;

import java.util.ArrayList;
import java.util.List;

public class BsiUrlValidator implements IParameterValidator {
    @Override
    public void validate(String name, String value) throws ParameterException {
        BsiUrl url = new BsiUrl(value);
        List<String> missingParams = new ArrayList<>();

        if (!url.hasAssessmentTypeId())
            missingParams.add("astid");
        if (!url.hasTechnologyStack())
            missingParams.add("ts");
        if (!url.hasProjectVersionId())
            missingParams.add("pv");
        if (!url.hasTenantCode())
            missingParams.add("tc");

        if (missingParams.size() > 0) {
            String errorMessage = "Parameter " + name + " is invalid. Missing the following: ";
            for (String param : missingParams) {
                errorMessage = errorMessage.concat(param + " ");
            }

            throw new ParameterException(errorMessage);
        }
    }
}

