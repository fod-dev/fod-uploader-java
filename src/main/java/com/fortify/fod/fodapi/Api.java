package com.fortify.fod.fodapi;

import com.fortify.fod.MessageResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import java.io.IOException;


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

    public void retireToken() {
        try {
            Request request = new Request.Builder()
                    .url(baseUrl + "/oauth/retireToken")
                    .get()
                    .build();
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                // Read the results and close the response
                String content = IOUtils.toString(response.body().byteStream(), "utf-8");
                response.body().close();

                Gson gson = new Gson();
                MessageResponse messageResponse = gson.fromJson(content, MessageResponse.class);

                if(messageResponse != null)  // did not get back the expected response
                    System.out.println("Retiring Token : " + messageResponse.getMessage());
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public boolean useClientId() {
        return useClientId;
    }
}
