package com.fortify.fod;

import com.beust.jcommander.JCommander;
import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.parser.FortifyCommands;
import com.fortify.fod.parser.Proxy;

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

        if (!fc.hasApiCredentials() && !fc.hasUserCredentials()) {
            System.out.println("The following options are required: -apiCredentials, -ac or -userCredentials, -uc");
            jc.usage();
            System.exit(1);
        }

        boolean uploadSucceeded;
        try {
            Proxy proxy = new Proxy(fc.proxy);
            FodApi fodApi = new FodApi(fc.bsiToken.getApiUri(), proxy.getProxyUri() == null ? null : proxy);

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

            String tenantCode = fc.bsiToken.getTenantCode();
            fodApi.authenticate(tenantCode, username, password, grantType);

            System.out.println("Beginning upload");

            uploadSucceeded = fodApi.getStaticScanController().StartStaticScan(fc);

            //check success status exit appropriately
            if (uploadSucceeded) {
                // Why do we need to poll for this?
                if (fc.pollingInterval > 0) {
                    PollStatus listener = new PollStatus(fodApi, fc.pollingInterval);
                    // Until status is complete or cancelled
                    listener.releaseStatus(fc.bsiToken.getProjectVersionId());
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