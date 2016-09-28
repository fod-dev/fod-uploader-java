package com.fortify.fod.parser;

import com.fortify.fod.legacy.LegacyParser;
import org.apache.commons.cli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class FortifyCommandLine {
    private BsiUrl bsiUrl = null;
    private String zipLocation = "";
    private Map<String, String> apiCredentials = new HashMap<>();
    private Map<String, String> loginCredentials = new HashMap<>();
    private int auditPreferenceId = 0;
    private int scanPreferenceId = 0;
    private int pollingInterval = 0;
    private int entitlementId = 0;
    private int entitlementFrequencyType = 0;
    private Proxy proxy = null;
    private boolean runSonatypeScan = false;
    private boolean isRemediationScan = false;
    private boolean excludeThirdPartyLibs = false;

    /**
     * Constructor for Fortify CLI
     * @param cmd cli object
     */
    FortifyCommandLine(final CommandLine cmd) {
        // null is passed in in the event that the user wants "-legacy", "-help", or "-version".
        // In any of those cases we don't care what else is here so stop building the object.
        if (cmd == null)
            return;

        if (cmd.hasOption(FortifyParser.BSI_URL))
            bsiUrl = new BsiUrl(cmd.getOptionValue(FortifyParser.BSI_URL));
        if (cmd.hasOption(FortifyParser.PROXY))
            proxy = new Proxy(cmd.getOptionValues(FortifyParser.PROXY));
        if (cmd.hasOption(FortifyParser.ZIP_LOCATION))
            zipLocation = cmd.getOptionValue(FortifyParser.ZIP_LOCATION);
        if (cmd.hasOption(FortifyParser.AUDIT_PREFERENCE_ID))
            auditPreferenceId = Integer.parseInt(cmd.getOptionValue(FortifyParser.AUDIT_PREFERENCE_ID));
        if (cmd.hasOption(FortifyParser.POLLING_INTERVAL))
            pollingInterval = Integer.parseInt(cmd.getOptionValue(FortifyParser.POLLING_INTERVAL));
        if (cmd.hasOption(FortifyParser.RUN_SONATYPE_SCAN))
            runSonatypeScan = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.RUN_SONATYPE_SCAN));
        if (cmd.hasOption(FortifyParser.SCAN_PREFERENCE_ID))
            scanPreferenceId = Integer.parseInt(cmd.getOptionValue(FortifyParser.SCAN_PREFERENCE_ID));
        if (cmd.hasOption(FortifyParser.ENTITLEMENT_ID))
            entitlementId = Integer.parseInt(cmd.getOptionValue(FortifyParser.ENTITLEMENT_ID));
        if (cmd.hasOption(FortifyParser.ENTITLEMENT_FREQUENCY_TYPE))
            entitlementFrequencyType = Integer.parseInt(cmd.getOptionValue(FortifyParser.ENTITLEMENT_FREQUENCY_TYPE));
        if (cmd.hasOption(FortifyParser.EXCLUDE_THIRD_PARTY_LIBS))
            excludeThirdPartyLibs = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.EXCLUDE_THIRD_PARTY_LIBS));
        if (cmd.hasOption(FortifyParser.IS_REMEDIATION_SCAN))
            isRemediationScan = Boolean.parseBoolean((cmd.getOptionValue(FortifyParser.IS_REMEDIATION_SCAN)));

        String[] loginValues = cmd.getOptionValues(FortifyParser.USERNAME);
        if (loginValues != null && loginValues[0] != null && loginValues[1] != null) {
            loginCredentials.put("username", loginValues[0]);
            loginCredentials.put("password", loginValues[1]);
        }
        loginValues = cmd.getOptionValues(FortifyParser.API);
        if (loginValues != null && loginValues[0] != null && loginValues[1] != null) {
            apiCredentials.put("key", loginValues[0]);
            apiCredentials.put("secret", loginValues[1]);
        }
    }

    /**
     * Constructor for translating Legacy CLI
     * @param args string array of arguments
     */
    FortifyCommandLine(String[] args) {
        Map<String, String> legacyArgs = new LegacyParser(args).getArgsMap();

        zipLocation = legacyArgs.get("zipLocation");
        entitlementId = Integer.parseInt(legacyArgs.get("entitlementId"));
        entitlementFrequencyType = Integer.parseInt(legacyArgs.get("entitlementFrequency"));
        bsiUrl = new BsiUrl(legacyArgs.get("bsiUrl"));
        String legacyUsername = legacyArgs.get("username");
        if (legacyUsername.toLowerCase().startsWith("key")) {
            apiCredentials.put("key", legacyUsername.substring(4));
            apiCredentials.put("secret", legacyArgs.get("password"));
        } else {
            loginCredentials.put("username", legacyUsername);
            loginCredentials.put("password", legacyArgs.get("password"));
        }

        String legacyAuditPreferenceId = legacyArgs.get("auditPreferenceId");
        if (legacyAuditPreferenceId != null)
            auditPreferenceId = Integer.parseInt(legacyAuditPreferenceId);

        String legacyScanPreferenceId = legacyArgs.get("scanPreferenceId");
        if (legacyScanPreferenceId != null)
            scanPreferenceId = Integer.parseInt(legacyScanPreferenceId);

        String legacyPollingInterval = legacyArgs.get("pollingInterval");
        if (legacyPollingInterval != null)
            pollingInterval = Integer.parseInt(legacyPollingInterval);

        String legacyRunSonatypeScan = legacyArgs.get("runSonatypeScan");
        if (legacyRunSonatypeScan != null)
            runSonatypeScan = Boolean.parseBoolean(legacyRunSonatypeScan);

        String legacyIsRemediationScan = legacyArgs.get("isRemediationScan");
        if (legacyIsRemediationScan != null)
            isRemediationScan = Boolean.parseBoolean(legacyIsRemediationScan);

        String legacyExcludeThirdPartyLibs = legacyArgs.get("excludeThirdPartyLibs");
        if (legacyExcludeThirdPartyLibs != null)
            excludeThirdPartyLibs = Boolean.parseBoolean(legacyExcludeThirdPartyLibs);

        if (legacyArgs.get("proxy") != null) {
            String[] proxyArgs = {legacyArgs.get("proxy"), legacyArgs.get("proxyUsername"), legacyArgs.get("proxyPassword"),
                    legacyArgs.get("ntDomain"), legacyArgs.get("ntWorkStation")};
            proxy = new Proxy(proxyArgs);
        }
    }

    /**
     * Empty Constructor
     */
    FortifyCommandLine() {}

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
        return loginCredentials.containsKey("username");
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
        return pollingInterval > 0;
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
        return apiCredentials.containsKey("key");
    }

    public String getZipLocation() {
        return zipLocation;
    }
    public boolean hasZipLocation() {
        return !zipLocation.isEmpty();
    }

    public int getScanPreferenceId() {
        return scanPreferenceId;
    }
    public boolean hasScanPreferenceId() {
        return scanPreferenceId != 0;
    }

    public int getEntitlementId() {
        return entitlementId;
    }
    public boolean hasEntitlementId() {
        return entitlementId != 0;
    }

    public int getEntitlementFrequencyType() {
        return entitlementFrequencyType;
    }
    public boolean hasEntitlementFrequencyType() {
        return entitlementFrequencyType != 0;
    }

    public boolean isRemediationScan() {
        return isRemediationScan;
    }
    public boolean hasExcludeThirdPartyLibs() {
        return excludeThirdPartyLibs;
    }
}
