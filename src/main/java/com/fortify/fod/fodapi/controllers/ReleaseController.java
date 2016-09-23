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
            ReleaseModel messageResponse = gson.fromJson(content, ReleaseModel.class);

            return messageResponse;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public ReleaseModel getRelease(int releaseId) {
        return getRelease(releaseId, "");
    }
/*    private static int getScanStatus(FodApi fodApi) {
        int result = -1;
        try {
            String statusUrl = url + "/api/v2/releases?q=releaseId:" + releaseId + "&fields=status";
            HttpGet get = new HttpGet(statusUrl);
            get.addHeader("Authorization","Bearer " + token);
            HttpResponse response  = httpclient.execute(get);
            HttpEntity entity = response.getEntity();
            String responseString = EntityUtils.toString(entity);
            Gson gson = new Gson();
            ReleaseModel requestQueryResponse = gson.fromJson(responseString, ReleaseModel.class);
            if(requestQueryResponse != null)  // did not get back the expected response
            {
                int responseCode = requestQueryResponse.getResponseCode();
                if(responseCode == 200)
                {
                    ReleaseInfo[] releaseInfo = requestQueryResponse.getData();
                    if(releaseInfo != null && releaseInfo.length == 1 )
                    {
                        result = requestQueryResponse.getData()[0].getStatus();
                        consecutiveGetStatusFailureCount = 0;
                    }
                }
                else if(responseCode == 401)   // got logged out during polling so log back in
                {
                    System.out.println("Token expired re-authorizing");
                    //TODO: this is lame will clean up
                    token = fodApi.authenticate(tenantCode, username, password, fodApi.useClientId());
                    if(token == null || token.isEmpty() )
                    {
                        System.out.println("Failed to reauthorize");
                        consecutiveGetStatusFailureCount++;
                    }
                    else
                    {
                        result = 1;  // set status to in progress
                    }
                }
                else
                {
                    consecutiveGetStatusFailureCount++;
                }

            }
            else
            {
                consecutiveGetStatusFailureCount++;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;
    }*/
}
