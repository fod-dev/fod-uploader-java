package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.models.GenericListResponse;
import com.fortify.fod.fodapi.models.ReleaseAssessmentTypeDTO;
import com.fortify.fod.fodapi.models.ReleaseDTO;
import com.fortify.fod.fodapi.FodApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ReleaseController extends ControllerBase {
    /**
     * Constructor
     * @param api api object with client info
     */
    public ReleaseController(FodApi api) {
        super(api);
    }

    /**
     * GET specific release with given fields if applicable
     * @param releaseId release to get
     * @param fields specific fields to return
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
            Type t = new TypeToken<GenericListResponse<ReleaseDTO>>(){}.getType();
            GenericListResponse<ReleaseDTO> results =  gson.fromJson(content, t);
            return results.getItems()[0];
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Get a list of available assessment types for a given release
     *
     * @param releaseId release to get assessment types for
     * @return array of possible assessment types
     */
    public ReleaseAssessmentTypeDTO getAssessmentType(final int releaseId, final int assessmentTypeId) {
        try {
            String url = api.getBaseUrl() + "/api/v3/releases/" + releaseId + "/assessment-types?scanType=1";

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

            // Finds the assessment given from the bsiUrl
            ReleaseAssessmentTypeDTO retval = null;
            for (ReleaseAssessmentTypeDTO assessment : results.getItems()) {
                if (assessment.getAssessmentTypeId() == assessmentTypeId && assessment.getEntitlementId() > 0)
                    retval = assessment;
            }

            return retval;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
