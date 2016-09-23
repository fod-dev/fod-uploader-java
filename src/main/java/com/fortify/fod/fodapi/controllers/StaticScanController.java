package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import okhttp3.*;
import org.apache.commons.io.IOUtils;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class StaticScanController {
    private final int CHUNK_SIZE = 1024 * 1024;
    private FodApi api;

    public StaticScanController(final FodApi a) {
        api = a;
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
            while ((byteCount = fs.read(readByteArray)) != -1) {
                System.out.println(byteCount);
                if (byteCount < CHUNK_SIZE) {
                    fragmentNumber = -1;
                    lastFragment = true;
                    sendByteArray = Arrays.copyOf(readByteArray, byteCount);
                } else {
                    sendByteArray = readByteArray;
                }

                // Build url
                String fragUrl = bsiUrl.getEndpoint() + "/api/v1/release/" + bsiUrl.getProjectVersionId() + "/scan/?"
                        + "&fragNo=" + fragmentNumber + "&offset=" + offset;
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

                MediaType byteArray = MediaType.parse("application/octet-stream");
                Request request = new Request.Builder()
                        .addHeader("Authorization","Bearer " + api.getToken())
                        .addHeader("Content-Type", "application/octet-stream")
                        .url(fragUrl)
                        .post(RequestBody.create(byteArray, sendByteArray))
                        .build();
                // Get the response
                Response response = api.getClient().newCall(request).execute();

                System.out.println(response);

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
                        System.out.println(finalResponse);
                    }
                }
                offset += byteCount;
            }
            fs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return successfulUpload;
    }
}
