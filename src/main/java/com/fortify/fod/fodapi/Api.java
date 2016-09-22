package com.fortify.fod.fodapi;

import com.fortify.fod.MessageResponse;
import com.fortify.fod.parser.Proxy;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import okhttp3.Credentials;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.*;
import org.apache.http.conn.params.ConnRoutePNames;

import java.net.InetSocketAddress;


public class Api {
    private String baseUrl;
    private OkHttpClient client;
    private boolean useClientId = false;

    public Api(String url, Proxy clProxy) {
        baseUrl = url;
        client = Proxy(clProxy);
    }

    public String authenticate(String tenantCode, String username, String password, boolean hasLoginCredentials) {
        String accessToken = "";
        try {
            // Build the form body
            FormBody.Builder formBodyBuilder = new FormBody.Builder().add("scope", "https://hpfod.com/tenant");
            // Has username/password stuff
            if (hasLoginCredentials) {
                formBodyBuilder.add("grant_type", "password")
                        .add("username", tenantCode + "\\" + username)
                        .add("password", password);
            // Has api key/secret
            } else {
                useClientId = true;
                formBodyBuilder.add("grant_type", "client_credentials")
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

            System.out.println("success? "+response.isSuccessful());
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

    private OkHttpClient Proxy(Proxy clProxy) {
        if(clProxy != null) {
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .proxy(new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(clProxy.getProxyUri().getHost(), clProxy.getProxyUri().getPort())));

            if (clProxy.hasUsername() && clProxy.hasPassword()) {
                // Include NTDomain and NTWorkstation in auth
                Authenticator proxyAuthenticator;
                if (clProxy.hasNTDomain() && clProxy.hasNTWorkstation()) {
                    proxyAuthenticator = (Route route, Response response) -> {
                        String credentials = new NTCredentials(clProxy.getUsername(), clProxy.getPassword(),
                                clProxy.getNTWorkstation(), clProxy.getNTDomain()).toString();

                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credentials)
                                .build();
                    };
                // Just use username and password
                } else {
                    proxyAuthenticator = (Route route, Response response) -> {
                        String credentials = Credentials.basic(clProxy.getUsername(), clProxy.getPassword());
                        return response.request().newBuilder()
                                .header("Proxy-Authorization", credentials)
                                .build();
                    };

                }
                builder.proxyAuthenticator(proxyAuthenticator);
            }
            return builder.build();
        } else {
            return new OkHttpClient();
        }
    }

    public boolean useClientId() {
        return useClientId;
    }
}
