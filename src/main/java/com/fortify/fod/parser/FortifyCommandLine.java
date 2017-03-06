//package com.fortify.fod.parser;
//
//import com.fortify.fod.fodapi.FodEnums;
//import org.apache.commons.cli.CommandLine;
//
//import java.util.HashMap;
//import java.util.Map;
//
//public class FortifyCommandLine {
//    private BsiUrl bsiUrl = null;
//    private String zipLocation = "";
//    private Map<String, String> apiCredentials = new HashMap<>();
//    private Map<String, String> loginCredentials = new HashMap<>();
//    private FodEnums.ScanPreferenceType scanPreferenceType = FodEnums.ScanPreferenceType.Standard;
//    private FodEnums.AuditPreferenceType auditPreferenceType = FodEnums.AuditPreferenceType.Manual;
//    private int pollingInterval = 0;
//    private int entitlementId = 0;
//    private int entitlementFrequencyType = 0;
//    private Proxy proxy = null;
//    private boolean runSonatypeScan = false;
//    private boolean isRemediationScan = false;
//    private boolean excludeThirdPartyLibs = false;
//    private boolean isBundledAssessment = false;
//    private int parentAssessmentTypeId = 0;
//
//    /**
//     * Constructor for Fortify CLI
//     *
//     * @param cmd cli object
//     */
//    FortifyCommandLine(final CommandLine cmd) {
//        // null is passed in in the event that the user wants "-legacy", "-help", or "-version".
//        // In any of those cases we don't care what else is here so stop building the object.
//        if (cmd == null)
//            return;
//        if (cmd.hasOption(FortifyParser.IS_BUNDLED_ASSESSMENT))
//        	isBundledAssessment = Boolean.parseBoolean((cmd.getOptionValue(FortifyParser.IS_BUNDLED_ASSESSMENT)));
//        if (cmd.hasOption(FortifyParser.PARENT_ASSESSMENT_TYPE_ID))
//        	parentAssessmentTypeId = Integer.parseInt(cmd.getOptionValue(FortifyParser.PARENT_ASSESSMENT_TYPE_ID));
//        if (cmd.hasOption(FortifyParser.BSI_URL))
//            bsiUrl = new BsiUrl(cmd.getOptionValue(FortifyParser.BSI_URL));
//        if (cmd.hasOption(FortifyParser.PROXY))
//            proxy = new Proxy(cmd.getOptionValues(FortifyParser.PROXY));
//        if (cmd.hasOption(FortifyParser.ZIP_LOCATION))
//            zipLocation = cmd.getOptionValue(FortifyParser.ZIP_LOCATION);
//        if (cmd.hasOption(FortifyParser.AUDIT_PREFERENCE_ID)) {
//
//            String auditPrefInput = cmd.getOptionValue(FortifyParser.AUDIT_PREFERENCE_ID);
//            try {
//                int auditPreferenceId = Integer.parseInt(auditPrefInput);
//                this.auditPreferenceType = FodEnums.AuditPreferenceType.fromInt(auditPreferenceId);
//
//            } catch (NumberFormatException nfe) {
//                switch (auditPrefInput) {
//                    case "Manual":
//                        auditPreferenceType = FodEnums.AuditPreferenceType.Manual;
//                    case "Automated":
//                        auditPreferenceType = FodEnums.AuditPreferenceType.Automated;
//                }
//            }
//        }
//        if (cmd.hasOption(FortifyParser.POLLING_INTERVAL))
//            pollingInterval = Integer.parseInt(cmd.getOptionValue(FortifyParser.POLLING_INTERVAL));
//        if (cmd.hasOption(FortifyParser.RUN_SONATYPE_SCAN))
//            runSonatypeScan = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.RUN_SONATYPE_SCAN));
//        if (cmd.hasOption(FortifyParser.SCAN_PREFERENCE_ID)) {
//
//            String scanPreferenceInput = cmd.getOptionValue(FortifyParser.SCAN_PREFERENCE_ID);
//            try {
//                int scanPreferenceId = Integer.parseInt(scanPreferenceInput);
//                this.scanPreferenceType = FodEnums.ScanPreferenceType.fromInt(scanPreferenceId);
//            } catch (NumberFormatException nfe) {
//                switch (scanPreferenceInput) {
//                    case "Standard":
//                        this.scanPreferenceType = FodEnums.ScanPreferenceType.Standard;
//                    case "Express":
//                        this.scanPreferenceType = FodEnums.ScanPreferenceType.Express;
//                }
//            }
//        }
//        if (cmd.hasOption(FortifyParser.ENTITLEMENT_ID))
//            entitlementId = Integer.parseInt(cmd.getOptionValue(FortifyParser.ENTITLEMENT_ID));
//        if (cmd.hasOption(FortifyParser.ENTITLEMENT_FREQUENCY_TYPE))
//            entitlementFrequencyType = Integer.parseInt(cmd.getOptionValue(FortifyParser.ENTITLEMENT_FREQUENCY_TYPE));
//        if (cmd.hasOption(FortifyParser.EXCLUDE_THIRD_PARTY_LIBS))
//            excludeThirdPartyLibs = Boolean.parseBoolean(cmd.getOptionValue(FortifyParser.EXCLUDE_THIRD_PARTY_LIBS));
//        if (cmd.hasOption(FortifyParser.IS_REMEDIATION_SCAN))
//            isRemediationScan = Boolean.parseBoolean((cmd.getOptionValue(FortifyParser.IS_REMEDIATION_SCAN)));
//
//        String[] loginValues = cmd.getOptionValues(FortifyParser.USERNAME);
//        if (loginValues != null && loginValues[0] != null && loginValues[1] != null) {
//            loginCredentials.put("username", loginValues[0]);
//            loginCredentials.put("password", loginValues[1]);
//        }
//        loginValues = cmd.getOptionValues(FortifyParser.API);
//        if (loginValues != null && loginValues[0] != null && loginValues[1] != null) {
//            apiCredentials.put("key", loginValues[0]);
//            apiCredentials.put("secret", loginValues[1]);
//        }
//    }
//
//    /**
//     * Empty Constructor
//     */
//    FortifyCommandLine() {
//    }
//
//    public BsiUrl getBsiUrl() {
//        return bsiUrl;
//    }
//
//    public boolean hasBsiUrl() {
//        return bsiUrl != null;
//    }
//
//    public Map<String, String> getLoginCredentials() {
//        return loginCredentials;
//    }
//
//    public boolean hasLoginCredentials() {
//        return loginCredentials.containsKey("username");
//    }
//
//    public FodEnums.AuditPreferenceType getAuditPreferenceType() {
//        return this.auditPreferenceType;
//    }
//
//    public boolean hasAuditPreference() {
//        return this.auditPreferenceType != null;
//    }
//
//    public int getPollingInterval() {
//        return pollingInterval;
//    }
//
//    public boolean hasPollingInterval() {
//        return pollingInterval > 0;
//    }
//
//    public Proxy getProxy() {
//        return proxy;
//    }
//
//    public boolean hasProxy() {
//        return proxy != null;
//    }
//
//    public boolean hasRunSonatypeScan() {
//        return runSonatypeScan;
//    }
//
//    public Map<String, String> getApiCredentials() {
//        return apiCredentials;
//    }
//
//    public boolean hasApiCredentials() {
//        return apiCredentials.containsKey("key");
//    }
//
//    public String getZipLocation() {
//        return zipLocation;
//    }
//
//    public boolean hasZipLocation() {
//        return !zipLocation.isEmpty();
//    }
//
//    public FodEnums.ScanPreferenceType getScanPreferenceType() {
//        return this.scanPreferenceType;
//    }
//
//    public boolean hasScanPreference() {
//        return this.scanPreferenceType != null;
//    }
//
//    public int getEntitlementId() {
//        return entitlementId;
//    }
//
//    public boolean hasEntitlementId() {
//        return entitlementId != 0;
//    }
//
//    public int getEntitlementFrequencyType() {
//        return entitlementFrequencyType;
//    }
//
//    public boolean hasEntitlementFrequencyType() {
//        return entitlementFrequencyType != 0;
//    }
//
//    public boolean isRemediationScan() {
//        return isRemediationScan;
//    }
//
//    public boolean hasExcludeThirdPartyLibs() {
//        return excludeThirdPartyLibs;
//    }
//
//    public boolean isBundledAssessment() {
//    	return isBundledAssessment;
//    }
//
//    public int getParentAssessmentTypeId() {
//    	return parentAssessmentTypeId;
//    }
//
//    public boolean hasParentAssessmentTypeId() {
//    	return parentAssessmentTypeId != 0;
//    }
//}
