package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.GenericListResponse;
import com.fortify.fod.fodapi.models.LookupItemsModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class LookupItemsController extends ControllerBase {
    public LookupItemsController(FodApi api) {
        super(api);
    }

    public LookupItemsModel[] getLookupItems(String type) {
        try {
            Request request = new Request.Builder()
                    .url(api.getBaseUrl() + "/api/v3/lookup-items?type=" + type)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .get()
                    .build();
            Response response = api.getClient().newCall(request).execute();

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();

            System.out.println(content);

            Gson gson = new Gson();
            Type t = new TypeToken<GenericListResponse<LookupItemsModel>>(){}.getType();
            GenericListResponse<LookupItemsModel> results =  gson.fromJson(content, t);
            return results.getItems();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
