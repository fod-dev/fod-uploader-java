package com.fortify.fod.parser;

import org.apache.commons.cli.CommandLine;

import java.time.format.FormatStyle;
import java.util.Map;

public class FortifyCommandLine {
    private BsiUrl bsiUrl;
    private String zipLocation;
    private Map<String, String> apiCredentials;
    private Map<String, String> loginCredentials;
    private int auditPreferenceId;
    private int pollingInterval;
    private Proxy proxy;
    private boolean runSonatypeScan;

    public FortifyCommandLine(CommandLine cmd) {
        bsiUrl = new BsiUrl(cmd.getOptionValue(FortifyParser.BSI_URL));
        proxy = new Proxy(cmd.getOptionValues(FortifyParser.PROXY));
        zipLocation = cmd.getOptionValue(FortifyParser.ZIP_LOCATION);
        auditPreferenceId = Integer.parseInt(cmd.getOptionValue(FortifyParser.AUDIT_PREFERENCE_ID));
        pollingInterval = Integer.parseInt(cmd.getOptionValue(FortifyParser.POLLING_INTERVAL));
        runSonatypeScan = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.RUN_SONATYPE_SCAN));

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
    public boolean hasBsiUrl() {
        return bsiUrl != null;
    }

    public Map<String, String> getLoginCredentials() {
        return loginCredentials;
    }
    public boolean hasLoginCredentials() {
        return !loginCredentials.isEmpty();
    }

    public int getAuditPreferenceId() {
        return auditPreferenceId;
    }
    public boolean hasAuditPreferencesId() {
        return auditPreferenceId != 0;
    }

    public int getPollingInterval() {
        return pollingInterval;
    }
    public boolean hasPollingInterval() {
        return pollingInterval != 0;
    }

    public Proxy getProxy() {
        return proxy;
    }
    public boolean hasProxy() {
        return proxy != null;
    }

    public boolean hasRunSonatypeScan() {
        return runSonatypeScan;
    }

    public Map<String, String> getApiCredentials() {
        return apiCredentials;
    }
    public boolean hasApiCredentials() {
        return !apiCredentials.isEmpty();
    }

    public String getZipLocation() {
        return zipLocation;
    }
    public boolean hasZipLocation() {
        return !(zipLocation.isEmpty() || zipLocation == null);
    }
}
