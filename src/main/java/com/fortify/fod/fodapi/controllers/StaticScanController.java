package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.GenericErrorResponse;
import com.fortify.fod.fodapi.models.PostStartScanResponse;
import com.fortify.fod.fodapi.models.ReleaseAssessmentTypeDTO;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.io.FileInputStream;
import java.util.*;

public class StaticScanController extends ControllerBase {
    private final int CHUNK_SIZE = 1024 * 1024;

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
     * @param bsiUrl releaseId, assessmentTypeId, technologyStack, languageLevel
     * @param cl     scanPreferenceType, ScanPreferenceId, AuditPreferenceId, doSonatypeScan,
     * @return true if successful upload
     */
    public boolean StartStaticScan(final BsiUrl bsiUrl, final FortifyCommandLine cl) {
        PostStartScanResponse scanStartedResponse = null;

        try (FileInputStream fs = new FileInputStream(cl.getZipLocation())) {

            byte[] readByteArray = new byte[CHUNK_SIZE];
            byte[] sendByteArray;
            int fragmentNumber = 0;
            int byteCount;
            long offset = 0;

            if (!bsiUrl.hasAssessmentTypeId() && !bsiUrl.hasTechnologyStack()) {
                return false;
            }

            // Get entitlement info
            ReleaseAssessmentTypeDTO assessment = api.getReleaseController()
                    .getAssessmentType(bsiUrl.getProjectVersionId(), bsiUrl.getAssessmentTypeId());

            // Build 'static' portion of url
            String fragUrl = api.getBaseUrl() + "/api/v3/releases/" + bsiUrl.getProjectVersionId() +
                    "/static-scans/start-scan?";
            fragUrl += "assessmentTypeId=" + bsiUrl.getAssessmentTypeId();
            fragUrl += "&technologyStack=" + bsiUrl.getTechnologyStack();
            fragUrl += "&entitlementId=" + assessment.getEntitlementId();
            fragUrl += "&entitlementFrequencyType=" + assessment.getFrequencyTypeId();

            if (bsiUrl.hasLanguageLevel())
                fragUrl += "&languageLevel=" + bsiUrl.getLanguageLevel();
            if (cl.hasScanPreference())
                fragUrl += "&scanPreferenceType=" + cl.getScanPreferenceType().toString();
            if (cl.hasAuditPreference())
                fragUrl += "&auditPreferenceType=" + cl.getAuditPreferenceType().toString();
            if (cl.hasRunSonatypeScan())
                fragUrl += "&doSonatypeScan=" + cl.hasRunSonatypeScan();
            if (cl.isRemediationScan())
                fragUrl += "&isRemediationScan=" + cl.isRemediationScan();
            if (cl.hasExcludeThirdPartyLibs())
                fragUrl += "&excludeThirdPartyLibs=" + cl.hasExcludeThirdPartyLibs();

            Gson gson = new Gson();

            // Loop through chunks
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

                    // if you had to reauthenticate here, would the loop and request not need to be resubmitted?
                    // possible continue?
                }

                offset += byteCount;

                if (fragmentNumber % 5 == 0) {
                    System.out.println("Upload Status - Bytes sent:" + offset);
                }

                if (response.code() != 202) {
                    String responseJsonStr = IOUtils.toString(response.body().byteStream(), "utf-8");

                    if (response.code() == 200) {
                        scanStartedResponse = gson.fromJson(responseJsonStr, PostStartScanResponse.class);
                        System.out.println("Scan " + scanStartedResponse.getScanId() +
                                " uploaded successfully. Total bytes sent: " + offset);

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
}
