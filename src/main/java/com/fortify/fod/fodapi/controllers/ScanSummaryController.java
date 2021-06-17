package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.ScanSummaryDTO;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import java.io.IOException;
import java.lang.reflect.Type;


public class ScanSummaryController extends ControllerBase {
    /**
     * Constructor
     *
     * @param api api object with client info
     */
    public ScanSummaryController(FodApi api) {
        super(api);
    }

    /**
     * GET specific release with given fields if applicable
     *
     * @param releaseId release to get
     * @param scanId    specific fields to return
     * @return returns ScanSummaryDTO object containing specified fields or null
     */
    public ScanSummaryDTO getScanSummary(final int releaseId,final int scanId) {
        try {

            String url = api.getBaseUrl() + "/api/v3/scan/" + scanId + "/summary";

            String statusUrl = api.getBaseUrl() + "/api/v3/releases/" + releaseId + "/scans/"+ scanId;
            Request request = new Request.Builder()
                    .url(statusUrl)
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
            // Create a type of GenericList<ScanSummaryDTO> to play nice with gson.
            Type t = new TypeToken<ScanSummaryDTO>() {
            }.getType();

            ScanSummaryDTO results = gson.fromJson(content, t);

            if (results != null) {
                return results;
            } else {
                System.out.println("Error retrieving scan summary data from API. Please log into online website to view summary information.");
                System.out.println(String.format("API response code: %s", response.code()));
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}