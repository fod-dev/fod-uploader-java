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
	 * @param args Required:  zip location, bsi url, username/password or api key/secret
	 */
	public static void main(String[] args) {
        FortifyParser fortifyCommands = new FortifyParser();
        FortifyCommandLine cl = fortifyCommands.parse(args);

        // Use legacy commands
        if (fortifyCommands.useLegacy()) {
            // Remove the "-l" argument
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            LegacyMain.main(newArgs);
        // Use new stuff
        } else {
            final long maxFileSize = 5000 * 1024 * 1024L;
            boolean uploadSucceeded = false;
            long bytesSent = 0;

            try {
                if(cl.hasBsiUrl()) {
                    BsiUrl bsiUrl = cl.getBsiUrl();
                    FodApi fodApi = new FodApi(bsiUrl.getEndpoint(), cl.getProxy());

                    if (cl.hasZipLocation() && (cl.hasApiCredentials() || cl.hasLoginCredentials())) {

                        String zipLocation = cl.getZipLocation();

                        String tenantCode = bsiUrl.getTenantCode();
                        Map<String, String> tempCredentials;
                        // Has username/password
                        String username = "";
                        String password = "";
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
                        System.out.println("Upload completed successfully. Total bytes sent: " + bytesSent);

                        // Why do we need to poll for this?
                        if (cl.hasPollingInterval()) {
                            PollStatus listener = new PollStatus(bsiUrl.getProjectVersionId(), cl.getPollingInterval());
                            // Until status is is complete or cancelled
                            listener.releaseStatus(fodApi);
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
}
