package com.fortify.fod;

import java.io.File;

import java.util.Arrays;
import java.util.Map;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.legacy.LegacyMain;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import com.fortify.fod.parser.FortifyParser;

public class Main {

    /**
	 * @param args Required: zip location, bsi url, username/password or api key/secret
	 */
	public static void main(String[] args) {
        FortifyParser fortifyCommands = new FortifyParser();
        FortifyCommandLine cl = fortifyCommands.parse(args);

        final long maxFileSize = 5000 * 1024 * 1024L;
        boolean uploadSucceeded = false;

        try {
            if(cl.hasBsiUrl()) {
                BsiUrl bsiUrl = cl.getBsiUrl();
                FodApi fodApi = new FodApi(bsiUrl.getEndpoint(), cl.getProxy());

                if (cl.hasZipLocation() && (cl.hasApiCredentials() || cl.hasLoginCredentials())) {
                    System.out.println("Authenticating");

                    String zipLocation = cl.getZipLocation();

                    String tenantCode = bsiUrl.getTenantCode();
                    Map<String, String> tempCredentials;
                    // Has username/password
                    String username;
                    String password;
                    if (cl.hasLoginCredentials()) {
                        tempCredentials = cl.getLoginCredentials();
                        username = tempCredentials.get("username");
                        password = tempCredentials.get("password");
                    // Has key/secret
                    } else {
                        tempCredentials = cl.getApiCredentials();
                        username = tempCredentials.get("key");
                        password = tempCredentials.get("secret");
                    }

                    String grantType = cl.hasLoginCredentials() ? fodApi.GRANT_TYPE_PASSWORD : fodApi.GRANT_TYPE_CLIENT_CREDENTIALS;
                    fodApi.authenticate(tenantCode, username, password, grantType);

                    System.out.println("Beginning upload");

                    //first thing check file size
                    File zipFileInfo = new File(zipLocation);
                    if (zipFileInfo.length() > maxFileSize) {
                        System.out.println("Terminating upload. File Exceeds maximum length : " + maxFileSize);
                        return;
                    }

                    uploadSucceeded = fodApi.getStaticScanController().StartStaticScan(bsiUrl, cl);
                }

                //check success status exit appropriately
                if (uploadSucceeded) {
                    // Why do we need to poll for this?
                    if (cl.hasPollingInterval()) {
                        PollStatus listener = new PollStatus(fodApi, cl.getPollingInterval());
                        // Until status is is complete or cancelled
                        listener.releaseStatus(bsiUrl.getProjectVersionId());
                    }
                    fodApi.retireToken();
                    System.exit(1);
                } else {
                    fodApi.retireToken();
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
}