package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.models.ReleaseInfo;
import com.fortify.fod.fodapi.models.ReleaseModel;
import com.fortify.fod.fodapi.FodApi;
import com.google.gson.Gson;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class ReleaseController extends ControllerBase {
    public ReleaseController(FodApi api) {
        super(api);
    }

    public ReleaseModel getRelease(int releaseId, String fields) {
        try {
            String url = api.getBaseUrl() + "/api/v2/releases?q=releaseId:" + releaseId;
            if (fields.length() > 0) {
                url += "&fields=" + fields;
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
            if (response.code() == 401) {  // got logged out during polling so log back in
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            Gson gson = new Gson();
            return gson.fromJson(content, ReleaseModel.class);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
