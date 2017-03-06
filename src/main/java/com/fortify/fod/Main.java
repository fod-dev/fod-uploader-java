package com.fortify.fod;

import com.beust.jcommander.JCommander;
import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommands;
import com.fortify.fod.parser.Proxy;

import java.io.File;

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

        final long maxFileSize = 5000 * 1024 * 1024L;
        boolean uploadSucceeded = false;

        try {
            BsiUrl bsiUrl = new BsiUrl(fc.bsiUrl);
            Proxy proxy = new Proxy(fc.proxy);
            FodApi fodApi = new FodApi(bsiUrl.getEndpoint(), proxy.getProxyUri() == null ? null : proxy);

            if (fc.hasApiCredentials() || fc.hasUserCredentials()) {
                System.out.println("Authenticating");

                // Has username/password
                String username, password;
                if (fc.hasUserCredentials()) {
                    username = fc.userCredentials.get(0);
                    password = fc.userCredentials.get(1);
                // Has key/secret
                } else {
                    username = fc.apiCredentials.get(0);
                    password = fc.apiCredentials.get(1);
                }

                String grantType = fc.hasUserCredentials() ? fodApi.GRANT_TYPE_PASSWORD : fodApi.GRANT_TYPE_CLIENT_CREDENTIALS;

                String tenantCode = bsiUrl.getTenantCode();
                fodApi.authenticate(tenantCode, username, password, grantType);

                System.out.println("Beginning upload");

                //first thing check file size
                File zipFileInfo = new File(fc.zipLocation);
                if (zipFileInfo.length() > maxFileSize) {
                    System.out.println("Terminating upload. File Exceeds maximum length : " + maxFileSize);
                    return;
                }

                uploadSucceeded = fodApi.getStaticScanController().StartStaticScan(bsiUrl, fc);
            }

            //check success status exit appropriately
            if (uploadSucceeded) {
                // Why do we need to poll for this?
                if (fc.pollingInterval > 0) {
                    PollStatus listener = new PollStatus(fodApi, fc.pollingInterval);
                    // Until status is complete or cancelled
                    listener.releaseStatus(bsiUrl.getProjectVersionId());
                }
                fodApi.retireToken();
                System.exit(0);
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