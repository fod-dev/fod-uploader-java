package com.fortify.fod.parser;

import org.apache.commons.cli.CommandLine;

import java.util.Collections;
import java.util.Map;

public class FortifyCommandLine {
    private BsiUrl bsiUrl = null;
    private String zipLocation = "";
    private Map<String, String> apiCredentials = Collections.emptyMap();
    private Map<String, String> loginCredentials = Collections.emptyMap();
    private int auditPreferenceId = 0;
    private int scanPreferenceId = 0;
    private int pollingInterval = 0;
    private Proxy proxy = null;
    private boolean runSonatypeScan = false;

    FortifyCommandLine(CommandLine cmd) {
        // null is passed in in the event that the user wants "-legacy", "-help", or "-version".
        // In any of those cases we don't care what else is here so stop building the object.
        if (cmd == null)
            return;

        bsiUrl = new BsiUrl(cmd.getOptionValue(FortifyParser.BSI_URL));
        proxy = new Proxy(cmd.getOptionValues(FortifyParser.PROXY));
        zipLocation = cmd.getOptionValue(FortifyParser.ZIP_LOCATION);
        auditPreferenceId = Integer.parseInt(cmd.getOptionValue(FortifyParser.AUDIT_PREFERENCE_ID));
        pollingInterval = Integer.parseInt(cmd.getOptionValue(FortifyParser.POLLING_INTERVAL));
        runSonatypeScan = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.RUN_SONATYPE_SCAN));
        scanPreferenceId = Integer.parseInt(cmd.getOptionValue(FortifyParser.SCAN_PREFERENCE_ID));
        String[] loginValues = cmd.getOptionValues(FortifyParser.USERNAME);
        if (loginValues[0] != null && loginValues[1] != null) {
            loginCredentials.put("username", loginValues[0]);
            loginCredentials.put("password", loginValues[1]);
        }
        loginValues = cmd.getOptionValues(FortifyParser.API);
        if (loginValues[0] != null && loginValues[1] != null) {
            apiCredentials.put("key", loginValues[0]);
            apiCredentials.put("secret", loginValues[1]);
        }
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
        return !loginCredentials.containsKey("username");
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
        return !apiCredentials.containsKey("key");
    }

    public String getZipLocation() {
        return zipLocation;
    }
    public boolean hasZipLocation() {
        return zipLocation.isEmpty();
    }

    public int getScanPreferenceId() {
        return scanPreferenceId;
    }
    public boolean hasScanPreferenceId() {
        return scanPreferenceId != 0;
    }
}
