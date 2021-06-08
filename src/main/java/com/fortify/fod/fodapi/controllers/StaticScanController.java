package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.FodEnums;
import com.fortify.fod.fodapi.models.*;
import com.fortify.fod.parser.FortifyCommands;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import com.fortify.fod.parser.converters.BsiTokenConverter;
import com.fortify.fod.parser.BsiToken;

import java.io.FileInputStream;
import java.util.Arrays;

public class StaticScanController extends ControllerBase {
    private final int CHUNK_SIZE = 1024 * 1024;
    private final int MAX_NOTES_LENGTH = 250;
    private int triggeredScanId = -1;
    private BsiToken parsedbsiToken = null;
    private static BsiTokenConverter parser = new BsiTokenConverter();

    /**
     * Constructor
     *
     * @param api api object with client info
     */
    public StaticScanController(final FodApi api) {
        super(api);
    }

    /**
     * Starts a scan based on the V3 API
     *
     * @param fc fortify command object
     * @return true if scan successfully started
     */
    public boolean StartStaticScan(final FortifyCommands fc) {
        PostStartScanResponse scanStartedResponse;

        try (FileInputStream fs = new FileInputStream(fc.payload)) {

            byte[] readByteArray = new byte[CHUNK_SIZE];
            byte[] sendByteArray;
            int fragmentNumber = 0;
            int byteCount;
            long offset = 0;

            parsedbsiToken = (fc.bsiToken == null) ? null :parser.convert(fc.bsiToken);

            fc.remediationScanPreference = (fc.isRemediationScan) ? FodEnums.RemediationScanPreferenceType.RemediationScanOnly
                    : fc.remediationScanPreference != null ? fc.remediationScanPreference : FodEnums.RemediationScanPreferenceType.NonRemediationScanOnly ;
            HttpUrl.Builder builder = HttpUrl.parse(api.getBaseUrl()).newBuilder()
                    .addPathSegments(String.format("/api/v3/releases/%d/static-scans/start-scan-advanced",(fc.releaseId != 0)? fc.releaseId :  parsedbsiToken.getProjectVersionId()))
                    .addQueryParameter("releaseId", Integer.toString((fc.releaseId != 0)? fc.releaseId : parsedbsiToken.getProjectVersionId()))
                    .addQueryParameter("entitlementPreferenceType", (fc.entitlementPreference != null) ? fc.entitlementPreference.toString() : "3")
                    .addQueryParameter("purchaseEntitlement", Boolean.toString(fc.purchaseEntitlement))
                    .addQueryParameter("remdiationScanPreferenceType", (fc.remediationScanPreference != null) ? fc.remediationScanPreference.toString() : "2")
                    .addQueryParameter("inProgressScanActionType", (fc.inProgressScanPreferenceType != null) ? fc.inProgressScanPreferenceType.toString() : "0")
                    .addQueryParameter("scanTool", fc.scanTool)
                    .addQueryParameter("scanToolVersion", fc.getImplementedVersion())
                    .addQueryParameter("scanMethodType", fc.scanMethodType);
            if(fc.entitlementId > 0){
                builder = builder.addQueryParameter("entitlementId", Integer.toString(fc.entitlementId));
            }

            if(fc.releaseId == 0){
                builder = builder.addQueryParameter("bsiToken", fc.bsiToken.toString());
            }
            if (fc.notes != null && !fc.notes.isEmpty()) {
                String truncatedNotes = abbreviateString(fc.notes.trim(), MAX_NOTES_LENGTH);
                builder = builder.addQueryParameter("notes", truncatedNotes);
            }
            // TODO: Come back and fix the request to set fragNo and offset query parameters
            String fragUrl = builder.build().toString();
            // Loop through chunk
            while ((byteCount = fs.read(readByteArray)) != -1) {

                if (byteCount < CHUNK_SIZE) {
                    sendByteArray = Arrays.copyOf(readByteArray, byteCount);
                    fragmentNumber = -1;
                } else {
                    sendByteArray = readByteArray;
                }
                MediaType byteArray = MediaType.parse("application/octet-stream");
                Request request = new Request.Builder()
                        .addHeader("Authorization", "Bearer " + api.getToken())
                        .addHeader("Content-Type", "application/octet-stream")
                        // Add offsets
                        .url(fragUrl + "&fragNo=" + fragmentNumber++ + "&offset=" + offset)
                        .post(RequestBody.create(byteArray, sendByteArray))
                        .build();
                // Get the response
                Response response = api.getClient().newCall(request).execute();
                if (response.code() == HttpStatus.SC_FORBIDDEN) {  // got logged out during polling so log back in
                    // Re-authenticate
                    api.authenticate();

                    // if you had to re-authenticate here, would the loop and request not need to be resubmitted?
                    // possible continue?
                }

                offset += byteCount;

                if (fragmentNumber % 5 == 0) {
                    System.out.println("Upload Status - Bytes sent:" + offset);
                }

                if (response.code() != 202) {
                    String responseJsonStr = IOUtils.toString(response.body().byteStream(), "utf-8");

                    Gson gson = new Gson();
                    if (response.code() == 200) {
                        scanStartedResponse = gson.fromJson(responseJsonStr, PostStartScanResponse.class);
                        System.out.println("Scan " + scanStartedResponse.getScanId() +
                                " uploaded successfully. Total bytes sent: " + offset);
                        triggeredScanId = scanStartedResponse.getScanId();
                        return true;
                    } else if (!response.isSuccessful()) {
                        GenericErrorResponse errors = gson.fromJson(responseJsonStr, GenericErrorResponse.class);
                        System.out.println("Package upload failed for the following reasons: " +
                                errors.toString());
                        return false;
                    }
                }
                response.body().close();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static String abbreviateString(String input, int maxLength) {
        if (input.length() <= maxLength)
            return input;
        else
            return input.substring(0, maxLength);
    }

    public int getTriggeredScanId() {
        return triggeredScanId;
    }
}
