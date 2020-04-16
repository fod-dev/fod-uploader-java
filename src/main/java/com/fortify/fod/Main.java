package com.fortify.fod;

import com.beust.jcommander.JCommander;
import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.parser.FortifyCommands;
import com.fortify.fod.parser.Proxy;
import com.fortify.fod.fodapi.controllers.*;
import com.fortify.fod.parser.converters.BsiTokenConverter;
import com.fortify.fod.parser.BsiToken;

public class Main {

    /**
     * @param args Required: zip location, bsi url, username/password or api key/secret
     */
    public static void main(String[] args) {
        FortifyCommands fc = new FortifyCommands();
        JCommander jc = new JCommander(fc);

        try {
            jc.parse(args);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            jc.usage();
            System.exit(1);
        }

        if (fc.version) {
            fc.version();
            System.exit(1);
        }

        if (fc.help) {
            jc.usage();
            System.exit(1);
        }

        if (fc.bsiToken == null && (fc.url == null && ((fc.url.contains("http://") || fc.url.contains("https://"))))) {
            System.err.println("Please provide Valid Fortify Url Parameter ( Example : https://ams.fotify.com/");
            System.exit(1);
        }

        if (!fc.hasApiCredentials() && !fc.hasUserCredentials()) {
            System.err.println("The following options are required: -apiCredentials, -ac or -userCredentials, -uc");
            jc.usage();
            System.exit(1);
        }

        if (fc.hasUserCredentials() && fc.bsiToken == null && fc.tenantCode == null) {
            System.err.println("The following options are required: -tenantCode -tc if using -userCredentials -uc for API Authentication");
            jc.usage();
            System.exit(1);
        }

        if (fc.entitlementPreference == null) {
            System.err.println("The entitlement preference option needs to be have the following values");
            jc.usage();
            System.exit(1);
        }

        if (fc.isBundledAssessment || fc.auditPreferenceType != null || fc.includeThirdPartyLibs || fc.runOpenSourceScan || fc.scanPreferenceType != null) {
            System.out.println("The following parameters are deprecated and will be ignored:   -auditPreferenceId -a, -runOpenSourceScan -os, -scanPreferenceId -p, -includeThirdPartyApps -itp, -isBundledAssessment -b");
        }

        if (fc.isRemediationScan && fc.remediationScanPreference != null) {
            System.err.println("Both --r and -rp cannot be used at the same time , --r is deprecated instead we would prefer you to use -rp for selecting Remediation Scan Preference ");
            System.exit(1);
        }

        // Both bsiToken and ReleaseId are not provided - Exit
        if (fc.bsiToken == null && fc.releaseId == 0) {
            System.out.print(com.sun.prism.paint.Color.RED);
            System.err.println("Release ID or BSI token should be required. The Release ID is HIGHLY recommended for CI usage as the BSI token is being sunset in 2020");
            System.exit(1);
        }

        //Both BsiToken and ReleaseId ar provided , call is executed but with a warning message
        if (fc.bsiToken != null && fc.releaseId != 0) {
            System.out.println("The BsiToken provided will be ignored and the Scan Settings from the entered Release Id will be used for triggering the current scan.");
        }

        boolean uploadSucceeded;
        boolean passFailPolicy = true;
        int triggeredscanId;
        String portalUrl = null;
        String apiUrl = null;
        FodApi fodApi;
        try {
            Proxy proxy = new Proxy(fc.proxy);
            BsiToken bsiToken = fc.bsiToken != null ? new BsiTokenConverter().convert(fc.bsiToken) : null;
            if (fc.bsiToken != null) {
                fodApi = new FodApi(bsiToken.getApiUri(), proxy.getProxyUri() == null ? null : proxy, bsiToken.getPortalUri());
                apiUrl = bsiToken.getApiUri();
                portalUrl = bsiToken.getPortalUri();
            } else {
                // If user provides Api Uri
                if (fc.url.contains("api")) {
                    apiUrl = fc.url;
                    portalUrl = fc.url.replaceFirst("api.", "");
                } else {
                    portalUrl = fc.url;
                    apiUrl = fc.url.contains("http:")
                            ? fc.url.replaceFirst("http://", "http://api.")
                            : fc.url.replaceFirst("https://", "https://api.");
                }
                //Removing trail slash
                apiUrl = apiUrl.endsWith("/") ? apiUrl.substring(0, apiUrl.length() - 1) : apiUrl;
            }

            fodApi = new FodApi(apiUrl, proxy.getProxyUri() == null ? null : proxy, portalUrl);

            System.out.println("Authenticating");

            // Has username/password
            String username, password, grantType;
            if (fc.hasUserCredentials()) {
                username = fc.userCredentials.get(0);
                password = fc.userCredentials.get(1);
                grantType = fodApi.GRANT_TYPE_PASSWORD;
                // Has key/secret
            } else {
                username = fc.apiCredentials.get(0);
                password = fc.apiCredentials.get(1);
                grantType = fodApi.GRANT_TYPE_CLIENT_CREDENTIALS;
            }

            String tenantCode = bsiToken != null ? bsiToken.getTenantCode() : fc.tenantCode;
            fodApi.authenticate(tenantCode, username, password, grantType);

            System.out.println("Beginning upload");

            StaticScanController s = fodApi.getStaticScanController();
            uploadSucceeded = s.StartStaticScan(fc);
            triggeredscanId = s.getTriggeredScanId();
            //check success status exit appropriately
            if (uploadSucceeded) {
                // Why do we need to poll for this?
                if (fc.pollingInterval > 0) {
                    PollStatus listener = new PollStatus(fodApi, fc.pollingInterval);
                    // Until status is complete or cancelled
                    passFailPolicy = listener.releaseStatus((fc.bsiToken != null) ? bsiToken.getProjectVersionId() : fc.releaseId, triggeredscanId);
                }
                fodApi.retireToken();
                if (passFailPolicy) {
                    System.exit(0);
                } else {
                    System.exit(1);
                }
            } else {
                fodApi.retireToken();
                System.exit(1);
            }

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
