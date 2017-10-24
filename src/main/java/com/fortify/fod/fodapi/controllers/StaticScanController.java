package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.GenericErrorResponse;
import com.fortify.fod.fodapi.models.PostStartScanResponse;
import com.fortify.fod.fodapi.models.ReleaseAssessmentTypeDTO;
import com.fortify.fod.parser.FortifyCommands;
import com.google.gson.Gson;
import okhttp3.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;

import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Arrays;

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

            // Get entitlement info
            ReleaseAssessmentTypeDTO assessmentType = api.getReleaseController().getAssessmentType(fc);

            String auditPreferenceType = fc.hasAuditPreferenceType() ? fc.auditPreferenceType.toString() : fc.bsiToken.getAuditPreference();
            String scanPreferenceType = fc.hasScanPreferenceType() ? fc.scanPreferenceType.toString() : fc.bsiToken.getScanPreference();
            boolean excludeThirdPartyLibs = !fc.includeThirdPartyLibs || !fc.bsiToken.getIncludeThirdParty();
            boolean includeOpenSourceScan = fc.runOpenSourceScan || fc.bsiToken.getIncludeOpenSourceAnalysis();

            HttpUrl.Builder builder = HttpUrl.parse(api.getBaseUrl()).newBuilder()
                    .addPathSegments(String.format("/api/v3/releases/%d/static-scans/start-scan", fc.bsiToken.getProjectVersionId()))
                    .addQueryParameter("assessmentTypeId", Integer.toString(fc.bsiToken.getAssessmentTypeId()))
                    .addQueryParameter("technologyStack", fc.bsiToken.getTechnologyType())
                    .addQueryParameter("entitlementId", Integer.toString(assessmentType.getEntitlementId()))
                    .addQueryParameter("entitlementFrequencyType", Integer.toString(assessmentType.getFrequencyTypeId()))
                    .addQueryParameter("isBundledAssessment", Boolean.toString(assessmentType.isBundledAssessment()))
                    .addQueryParameter("doSonatypeScan", Boolean.toString(includeOpenSourceScan))
                    .addQueryParameter("excludeThirdPartyLibs", Boolean.toString(excludeThirdPartyLibs))
                    .addQueryParameter("scanPreferenceType", scanPreferenceType)
                    .addQueryParameter("auditPreferenceType", auditPreferenceType)
                    .addQueryParameter("isRemediationScan", Boolean.toString(fc.isRemediationScan));

            if (assessmentType.isBundledAssessment() && assessmentType.getParentAssessmentTypeId() > 0) {
                builder = builder.addQueryParameter("parentAssessmentTypeId", Integer.toString(assessmentType.getParentAssessmentTypeId()));
            }

            if (fc.bsiToken.getLanguageLevel() != null) {
                builder = builder.addQueryParameter("languageLevel", fc.bsiToken.getTechnologyVersion());
            }

            // TODO: Come back and fix the request to set fragNo and offset query parameters
            String fragUrl = builder.build().toString();

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
