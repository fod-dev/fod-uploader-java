package com.fortify.fod.parser;

import org.apache.commons.cli.CommandLine;

import java.time.format.FormatStyle;
import java.util.Map;

public class FortifyCommandLine {
    private BsiUrl bsiUrl;
    private String zipLocation;
    private Map<String, String> apiCredentials;
    private Map<String, String> loginCredentials;
    private String auditPreferenceId;
    private String pollingInterval;
    private Proxy proxy;
    private String runSonatypeScan;

    public FortifyCommandLine(CommandLine cmd) {
        bsiUrl = new BsiUrl(cmd.getOptionValue(FortifyParser.BSI_URL));
        proxy = new Proxy(cmd.getOptionValues(FortifyParser.PROXY));
        zipLocation = cmd.getOptionValue(FortifyParser.ZIP_LOCATION);
        auditPreferenceId = cmd.getOptionValue(FortifyParser.AUDIT_PREFERENCE_ID);
        pollingInterval = cmd.getOptionValue(FortifyParser.POLLING_INTERVAL);
        runSonatypeScan = cmd.getOptionValue(FortifyParser.RUN_SONATYPE_SCAN);

        String[] loginValues = cmd.getOptionValues(FortifyParser.USERNAME);
        loginCredentials.put(loginValues[0] == null ? null : loginValues[0],
                loginValues[1] == null ? null : loginValues[1]);

        loginValues = cmd.getOptionValues(FortifyParser.API);
        apiCredentials.put(loginValues[0] == null ? null : loginValues[0],
                loginValues[1] == null ? null : loginValues[1]);
    }

    public BsiUrl getBsiUrl() {
        return bsiUrl;
    }

    public Map<String, String> getLoginCredentials() {
        return loginCredentials;
    }

    public String getAuditPreferenceId() {
        return auditPreferenceId;
    }

    public String getPollingInterval() {
        return pollingInterval;
    }

    public Proxy getProxy() {
        return proxy;
    }

    public String isRunSonatypeScan() {
        return runSonatypeScan;
    }

    public Map<String, String> getApiCredentials() {
        return apiCredentials;
    }


    public String getZipLocation() {
        return zipLocation;
    }
}
