package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.GenericListResponse;
import com.fortify.fod.fodapi.models.ReleaseAssessmentTypeDTO;
import com.fortify.fod.fodapi.models.ReleaseDTO;
import com.fortify.fod.parser.FortifyCommands;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import com.fortify.fod.parser.BsiToken;
import com.fortify.fod.parser.converters.BsiTokenConverter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URLEncoder;

public class ReleaseController extends ControllerBase {
    /**
     * Constructor
     *
     * @param api api object with client info
     */
    public ReleaseController(FodApi api) {
        super(api);
    }

    /**
     * GET specific release with given fields if applicable
     *
     * @param releaseId release to get
     * @param fields    specific fields to return
     * @return returns ReleaseDTO object containing specified fields or null
     */
    public ReleaseDTO getRelease(int releaseId, String fields) {
        try {
            String url = api.getBaseUrl() + "/api/v3/releases?filters=releaseId:" + releaseId;
            if (fields.length() > 0) {
                url += "&fields=" + fields + "&limit=1";
            }
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .get()
                    .build();
            Response response = api.getClient().newCall(request).execute();

            // The endpoint call was unsuccessful. Maybe unauthorized who knows.
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            if (response.code() == HttpStatus.SC_UNAUTHORIZED) {  // got logged out during polling so log back in
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            Gson gson = new Gson();
            // Create a type of GenericList<ReleaseDTO> to play nice with gson.
            Type t = new TypeToken<GenericListResponse<ReleaseDTO>>() {
            }.getType();
            GenericListResponse<ReleaseDTO> results = gson.fromJson(content, t);
            return results.getItems()[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get Assessment Type from bsi url
     *
     * @param fc FortifyCommands object
     * @return returns assessment type obj
     */
    public ReleaseAssessmentTypeDTO getAssessmentType(final FortifyCommands fc) {
        try {

            try {
                BsiToken parsedbsiToken = new BsiTokenConverter().convert(fc.bsiToken);
                if (parsedbsiToken == null) {
                    throw new Exception("Bsi token given is invalid and cannot be parsed");
                }

                String filters = "frequencyTypeId:" + fc.entitlementPreference.getValue();

                filters = URLEncoder.encode(filters, "UTF-8");

                String url = String.format("%s/api/v3/releases/%s/assessment-types?scanType=1&filters=%s",
                        api.getBaseUrl(),
                        parsedbsiToken.getProjectVersionId(),
                        filters);

                if (api.getToken() == null)
                    api.authenticate();

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Authorization", "Bearer " + api.getToken())
                        .get()
                        .build();
                Response response = api.getClient().newCall(request).execute();

                if (response.code() == HttpStatus.SC_FORBIDDEN) {  // got logged out during polling so log back in
                    // Re-authenticate
                    api.authenticate();
                }

                // Read the results and close the response
                String content = IOUtils.toString(response.body().byteStream(), "utf-8");
                response.body().close();

                Gson gson = new Gson();
                // Create a type of GenericList<ApplicationDTO> to play nice with gson.
                Type t = new TypeToken<GenericListResponse<ReleaseAssessmentTypeDTO>>() {
                }.getType();
                GenericListResponse<ReleaseAssessmentTypeDTO> results = gson.fromJson(content, t);

                // Get entitlement based on available options
                for (ReleaseAssessmentTypeDTO assessment : results.getItems()) {
                    if (assessment.getAssessmentTypeId() == parsedbsiToken.getAssessmentTypeId()) {
                        if (fc.purchaseEntitlement || assessment.getEntitlementId() > 0)
                            return assessment;
                        return null;
                    }
                }
            } catch (Exception e) {
                System.out.println("Bsi Token cannot be parsed , please check and upload valid bsi Token");
                return null;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
