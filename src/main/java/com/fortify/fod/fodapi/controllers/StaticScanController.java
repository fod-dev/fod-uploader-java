package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import okhttp3.*;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class StaticScanController extends ControllerBase {
    private final int CHUNK_SIZE = 1024 * 1024;

    public StaticScanController(final FodApi api) {
        super(api);
    }

    /**
     * TODO: entitlementId, entitlementFrequencyType, isRemediationScan, excludeThirdPartyLibs
     * Starts a scan based on the V3 API
     * @param bsiUrl releaseId, assessmentTypeId, technologyStack, languageLevel
     * @param cl scanPreferenceType, ScanPreferenceId, AuditPreferenceId, doSonatypeScan,
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

            // Build 'static' portion of url
            String fragUrl = api.getBaseUrl() + "/api/v1/release/" + bsiUrl.getProjectVersionId() + "/scan/?";
            if (bsiUrl.hasAssessmentTypeId())
                fragUrl += "&assessmentTypeId=" + bsiUrl.getAssessmentTypeId();
            if (bsiUrl.hasTechnologyStack())
                fragUrl += "&technologyStack=" + bsiUrl.getTechnologyStack();
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

                // The endpoint call was unsuccessful. Maybe unauthorized who knows.
                if (!response.isSuccessful()) {
                    throw new IOException("Unexpected code: " + response);
                }

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
                        System.out.println("Package upload failed: " + finalResponse);
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
