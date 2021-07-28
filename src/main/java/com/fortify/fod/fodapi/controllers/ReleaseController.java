package com.fortify.fod.fodapi.controllers;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.FodEnums;
import com.fortify.fod.fodapi.models.*;
import com.fortify.fod.parser.BsiToken;
import com.fortify.fod.parser.FortifyCommands;
import com.fortify.fod.parser.converters.BsiTokenConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import java.io.IOException;
import java.lang.reflect.Type;

public class ReleaseController extends ControllerBase {
    private static BsiTokenConverter parser = new BsiTokenConverter();
    /**
     * Constructor
     *
     * @param api api object with client info
     */
    private BsiToken parsedbsiToken = null;

    public ReleaseController(FodApi api) {
        super(api);
    }

    /**
     * GET specific release with given fields if applicable
     *
     * @param releaseId release to get
     * @param fields    specific fields to return
     * @return returns ReleaseDTO object containing specified fields or null
     */
    public ReleaseDTO getRelease(int releaseId, String fields) {
        try {
            String url = api.getBaseUrl() + "/api/v3/releases?filters=releaseId:" + releaseId;
            if (fields.length() > 0) {
                url += "&fields=" + fields + "&limit=1";
            }
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .get()
                    .build();
            Response response = api.getClient().newCall(request).execute();

            // The endpoint call was unsuccessful. Maybe unauthorized who knows.
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            if (response.code() == HttpStatus.SC_UNAUTHORIZED) {  // got logged out during polling so log back in
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();
            System.out.println(content);
            Gson gson = new Gson();
            // Create a type of GenericList<ReleaseDTO> to play nice with gson.
            Type t = new TypeToken<GenericListResponse<ReleaseDTO>>() {
            }.getType();
            GenericListResponse<ReleaseDTO> results = gson.fromJson(content, t);
            return results.getItems()[0];
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PollingScanSummaryDTO getReleaseScanForPolling(int releaseId, int scanId) {
        try {
            String url = api.getBaseUrl() + "/api/v3/releases/" + releaseId + "/scans/" + scanId + "/polling-summary";
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .get()
                    .build();
            Response response = api.getClient().newCall(request).execute();

            // The endpoint call was unsuccessful. Maybe unauthorized who knows.
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            if (response.code() == HttpStatus.SC_UNAUTHORIZED) {  // got logged out during polling so log back in
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }

            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();
            Gson gson = new Gson();
            PollingScanSummaryDTO summaryResults = gson.fromJson(content, PollingScanSummaryDTO.class);
            return summaryResults;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public StaticScanSetupDTO getReleaseScanSettings(int releaseId) {

        try {
            String url = api.getBaseUrl() + String.format("/api/v3/releases/%d/static-scans/scan-setup", releaseId);
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .get()
                    .build();
            Response response = api.getClient().newCall(request).execute();

            if (response.code() == HttpStatus.SC_UNAUTHORIZED) {
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code: " + response);
            }
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();
            Type t = new TypeToken<StaticScanSetupDTO>() {
            }.getType();
            Gson gson = new Gson();
            StaticScanSetupDTO scanSettings = gson.fromJson(content, t);
            return scanSettings;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public boolean SaveScanSettings(PutStaticScanSetupRequest saveScanSettingsRequest, FortifyCommands fc) {
        PutStaticScanSetupResponse saveScanSettingsResponse;
        parsedbsiToken = (fc.bsiToken == null) ? null : parser.convert(fc.bsiToken);
        try {
            Gson gson = new Gson();
            String url = api.getBaseUrl() + String.format("/api/v3/releases/%d/static-scans/scan-setup", (fc.releaseId != 0) ? fc.releaseId : parsedbsiToken.getProjectVersionId());
            byte[] json = gson.toJson(saveScanSettingsRequest).getBytes();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Authorization", "Bearer " + api.getToken())
                    .put(RequestBody.create(MediaType.parse("application/json"), json))
                    .build();

            Response response = api.getClient().newCall(request).execute();
            // The endpoint call was unsuccessful. Maybe unauthorized who knows.

            if (response.code() == HttpStatus.SC_UNAUTHORIZED) {
                System.out.println("Token expired re-authorizing");
                // Re-authenticate
                api.authenticate();
            }
            // Read the results and close the response
            String content = IOUtils.toString(response.body().byteStream(), "utf-8");
            response.body().close();
            Type t = new TypeToken<PutStaticScanSetupResponse>() {
            }.getType();
            saveScanSettingsResponse = gson.fromJson(content, t);
            if(saveScanSettingsResponse.getSuccess()) {
                System.out.println("Saving Scan Settings Complete.");
                return true;
            }
            else if(saveScanSettingsResponse.getErrors().size() > 0){
                System.out.println("Error saving Scan Settings");
                String errorMessage= null;
                for(int e = 0 ;e < saveScanSettingsResponse.getErrors().size();e++ ){
                    String error = saveScanSettingsResponse.getErrors().get(e);
                    errorMessage = errorMessage == null ? String.format("%d - %s ",e+1,error) : errorMessage + String.format("%d - %s ",e+1,error);
                }
                System.out.println(errorMessage);
                return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean UpdateScanSettings(StaticScanSetupDTO scanSettingsDTO, final FortifyCommands fc) {
        PutStaticScanSetupRequest pss = new PutStaticScanSetupRequest();
        boolean proccedWithScan = true;
        // Means there are no existing settings
        if (scanSettingsDTO.getTechnologyStackId() == 0 && scanSettingsDTO.getEntitlementId() <= 0 && scanSettingsDTO.getReleaseId() > 0) {
            if (fc.assessmentType <= 0 || fc.entitlementPreference == null || fc.auditPreferenceType == null || fc.technologyStack <= 0) {
                System.err.println("The following options are required: Assessment Type, Entitlement Preference, Audit Preference Type and Technology Stack. Without saving these settings a scan cannot be started");
                return false;
            }
            if (fc.technologyStack != 0) {
                if (needsLanguageLevel(fc.technologyStack) && fc.languageLevel == 0) {
                    System.err.println("Language Id is required for following TechnologyTypes 1(.NET) , 23 (.NET Core) , 7 (Java/J2EE) , 10 (Python)");
                    return false;
                }
            }
            pss.assessmentTypeId = fc.assessmentType;
            pss.technologyStackId = fc.technologyStack;
            pss.languageLevelId = fc.languageLevel;
            pss.auditPreferenceType = fc.auditPreferenceType;
            pss.entitlementFrequencyType = null;
            pss.performOpenSourceAnalysis = fc.runOpenSourceScan;
            pss.scanBinary = fc.isBinaryScan;
            pss.includeThirdPartyLibraries = fc.includeThirdPartyLibs;
            pss.useSourceControl = false;
            proccedWithScan = SaveScanSettings(pss, fc);
        } else if (fc.assessmentType > 0 || fc.technologyStack > 0 || fc.auditPreferenceType != null || fc.languageLevel > 0 || fc.isBinaryScan || fc.includeThirdPartyLibs || fc.runOpenSourceScan) {
            if (scanSettingsDTO.getTechnologyStackId() <= 0) {
                System.err.println("There is no TechStack available as part of Scan settings. Please enter Technology stack to proceed further for updating scan settings.");
                return false;
            }
            pss.assessmentTypeId = fc.assessmentType > 0 ? fc.assessmentType : scanSettingsDTO.getAssessmentTypeId();
            pss.technologyStackId = fc.technologyStack > 0 ? fc.technologyStack : scanSettingsDTO.getTechnologyStackId();
            pss.languageLevelId = fc.languageLevel > 0 ? fc.languageLevel : scanSettingsDTO.getLanguageLevelId();
            pss.auditPreferenceType = fc.auditPreferenceType != null ? fc.auditPreferenceType : FodEnums.AuditPreferenceTypes.fromInt(scanSettingsDTO.getauditPreferenceType());
            pss.entitlementFrequencyType = null;
            pss.performOpenSourceAnalysis = fc.runOpenSourceScan ? fc.runOpenSourceScan : scanSettingsDTO.getPerformOpenSourceAnalysis();
            pss.scanBinary = fc.isBinaryScan ? fc.isBinaryScan : scanSettingsDTO.getScanBinary();
            pss.includeThirdPartyLibraries = fc.includeThirdPartyLibs ? fc.includeThirdPartyLibs : scanSettingsDTO.getincludeThirdPartyLibraries();
            pss.useSourceControl = false;
            if(fc.technologyStack > 0){
               pss.languageLevelId = needsLanguageLevel(fc.technologyStack) ? fc.languageLevel : 0;
            } else {
               pss.languageLevelId = scanSettingsDTO.getLanguageLevelId();
            }
            proccedWithScan = SaveScanSettings(pss, fc);
        }
        return proccedWithScan;
    }

    private boolean needsLanguageLevel(int techStackId){
        if (techStackId == 1 || techStackId == 23 || techStackId == 7 || techStackId == 10) {
            return true;
        } else{
            return false;
        }
    }
}
