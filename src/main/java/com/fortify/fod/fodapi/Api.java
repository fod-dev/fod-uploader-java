package com.fortify.fod.fodapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.commons.io.IOUtils;


public class Api {
    private String baseUrl;
    private final OkHttpClient client;
    private boolean useClientId = false;

    public Api(String url) {
        baseUrl = url;
        this.client = new OkHttpClient();
    }

    public String authenticate(String tenantCode, String username, String password, boolean hasLoginCredentials) {
        String accessToken = "";
        try {
            // Build the form body
            FormBody.Builder formBodyBuilder = new FormBody.Builder().add("scope", "https://hpfod.com/tenant");
            // Has username/password stuff
            if (hasLoginCredentials) {
                formBodyBuilder.add("grant_type", "client_credentials")
                        .add("username", tenantCode + "\\" + username)
                        .add("password", password);
            // Has api key/secret
            } else {
                useClientId = true;
                formBodyBuilder.add("grant_type", "password")
                        .add("client_id", username)
                        .add("client_secret", password);
            }
            RequestBody formBody = formBodyBuilder.build();

            // Create the request
            Request request = new Request.Builder()
                    .url(baseUrl + "/oauth/token")
                    .post(formBody)
                    .build();

            // Get the response
            Response response = client.newCall(request).execute();

            if(response.isSuccessful()) {

                // Read the results and close the response
                String content = IOUtils.toString(response.body().byteStream(), "utf-8");
                response.body().close();

                // Parse the Response
                JsonParser parser = new JsonParser();
                JsonObject obj = parser.parse(content).getAsJsonObject();
                accessToken = obj.get("access_token").getAsString();

                System.out.println(accessToken);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public boolean useClientId() {
        return useClientId;
    }
}
