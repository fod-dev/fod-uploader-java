package com.fortify.fod.fodapi;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.*;
import org.apache.commons.io.IOUtils;


public class Api {
    private String baseUrl = "http://api.local/";
    private final OkHttpClient client;

    public Api() {
        this.client = new OkHttpClient();
    }

/*    private String authenticate(String tenantCode, String username, String password)
    {
        String accessToken = "";
        try {
            String endpoint = baseUrl + "/oauth/token";
            HttpPost httppost = new HttpPost(endpoint);
            List<NameValuePair> formParams = new ArrayList<NameValuePair>();
            if(username.toLowerCase().startsWith("key"))
            {
                formParams.add(new BasicNameValuePair("scope", "https://hpfod.com/tenant"));
                formParams.add(new BasicNameValuePair("grant_type", "client_credentials"));
                formParams.add(new BasicNameValuePair("client_id", username.substring(4)));
                formParams.add(new BasicNameValuePair("client_secret", password));
            }
            else
            {
                formParams.add(new BasicNameValuePair("scope", "https://hpfod.com/tenant"));
                formParams.add(new BasicNameValuePair("grant_type", "password"));
                formParams.add(new BasicNameValuePair("username",  tenantCode + "\\" + username));
                formParams.add(new BasicNameValuePair("password", password));
            }

            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
            httppost.setEntity(entity);
            HttpResponse response = client.execute(httppost);
            StatusLine sl = response.getStatusLine();
            Integer statusCode = sl.getStatusCode();
            if(statusCode.toString().startsWith("2") )
            {
                HttpEntity respopnseEntity = response.getEntity();
                InputStream is = respopnseEntity.getContent();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder content = new StringBuilder();
                while ((line = rd.readLine()) != null) {
                    content.append(line);
                    content.append('\r');
                }
                rd.close();
                String x=content.toString();
                JsonParser parser=new JsonParser();
                JsonElement jsonElement = parser.parse(x);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                JsonPrimitive tokenPrimitive=jsonObject.getAsJsonPrimitive("access_token");
                if( tokenPrimitive!=null )
                {
                    accessToken=tokenPrimitive.getAsString();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }*/

    public String authenticateHttpOk(String tenantCode, String username, String password) {
        String accessToken = "";
        try {
            // Build the form body
            RequestBody formBody = new FormBody.Builder()
                    .add("scope", "https://hpfod.com/tenant")
                    .add("grant_type", "client_credentials")
                    .add("client_id", "a77d67fd-f868-4316-900c-d8e1b8bdbbf8")
                    .add("client_secret", "XC7WaeazY>9FA|3zEhi1xWz2lg|wxD")
                    .build();

            // Create the request
            Request request = new Request.Builder()
                    .url(baseUrl + "/oauth/token")
                    .post(formBody)
                    .build();

            // Get the response
            Response response = client.newCall(request).execute();

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            // Parse the Response
            JsonParser parser = new JsonParser();
            JsonObject obj = parser.parse(content).getAsJsonObject();
            accessToken  = obj.get("access_token").getAsString();

            System.out.println(accessToken);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }
}
