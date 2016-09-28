package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.GenericErrorResponse;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URIBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class StaticScanController extends ControllerBase {
    private final int CHUNK_SIZE = 1024 * 1024;
    /**
     * Constructor
     * @param api api object with client info
     */
    public StaticScanController(final FodApi api) {
        super(api);
    }

    /**
     * TODO: isRemediationScan, excludeThirdPartyLibs
     * Starts a scan based on the V3 API
     * @param bsiUrl releaseId, assessmentTypeId, technologyStack, languageLevel
     * @param cl scanPreferenceType, ScanPreferenceId, AuditPreferenceId, doSonatypeScan,
     * @return true if successful upload
     */
    public boolean StartStaticScan(final BsiUrl bsiUrl, final FortifyCommandLine cl) {
        boolean successfulUpload = false;
        boolean lastFragment = false;
        try {
            FileInputStream fs = new FileInputStream(cl.getZipLocation());

            byte[] readByteArray = new byte[CHUNK_SIZE];
            byte[] sendByteArray;
            int fragmentNumber = 0;
            int byteCount;
            long offset = 0;

            if(!bsiUrl.hasAssessmentTypeId() && !bsiUrl.hasTechnologyStack() && !cl.hasEntitlementId() &&
                    !cl.hasEntitlementFrequencyType()) {
                return false;
            }

            // Build 'static' portion of url
            String fragUrl = api.getBaseUrl() + "/api/v3/releases/" + bsiUrl.getProjectVersionId() +
                    "/static-scans/start-scan?";
            fragUrl += "assessmentTypeId=" + bsiUrl.getAssessmentTypeId();
            fragUrl += "&technologyStack=" + bsiUrl.getTechnologyStack();
            fragUrl += "&entitlementId=" + cl.getEntitlementId();
            fragUrl += "&entitlementFrequencyType=" + cl.getEntitlementFrequencyType();

            if (bsiUrl.hasLanguageLevel())
                fragUrl += "&languageLevel=" + bsiUrl.getLanguageLevel();
            if (cl.hasScanPreferenceId())
                fragUrl += "&scanPreferenceId=" + cl.getScanPreferenceId();
            if (cl.hasAuditPreferencesId())
                fragUrl += "&auditPreferenceId=" + cl.getAuditPreferenceId();
            if (cl.hasRunSonatypeScan())
                fragUrl += "&doSonatypeScan=" + cl.hasRunSonatypeScan();

            // Loop through chunks
            while ((byteCount = fs.read(readByteArray)) != -1) {
                if (byteCount < CHUNK_SIZE) {
                    fragmentNumber = -1;
                    lastFragment = true;
                    sendByteArray = Arrays.copyOf(readByteArray, byteCount);
                } else {
                    sendByteArray = readByteArray;
                }

                MediaType byteArray = MediaType.parse("application/octet-stream");
                Request request = new Request.Builder()
                        .addHeader("Authorization","Bearer " + api.getToken())
                        .addHeader("Content-Type", "application/octet-stream")
                        // Add offsets
                        .url(fragUrl + "&fragNo=" + fragmentNumber + "&offset=" + offset)
                        .post(RequestBody.create(byteArray, sendByteArray))
                        .build();
                // Get the response
                Response response = api.getClient().newCall(request).execute();

                if (fragmentNumber != 0 && fragmentNumber % 5 == 0) {
                    System.out.println("Upload Status - Bytes sent:" + offset);
                }
                if (lastFragment) {
                    // Read the results and close the response
                    String finalResponse = IOUtils.toString(response.body().byteStream(), "utf-8");
                    response.body().close();

                    // Scan successfully uploaded
                    if (finalResponse.toUpperCase().equals("ACK")) {
                        successfulUpload = true;
                    // There was an error along the lines of 'another scan in progress' or something
                    } else {
                        Gson gson = new Gson();
                        GenericErrorResponse errors = gson.fromJson(finalResponse, GenericErrorResponse.class);
                        System.out.println("Package upload failed for the following reasons: " +
                                errors.toString());
                    }
                }
                offset += byteCount;
            }
            fs.close();
            if (successfulUpload)
                System.out.println("Upload completed successfully. Total bytes sent: " + offset);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successfulUpload;
    }
}
