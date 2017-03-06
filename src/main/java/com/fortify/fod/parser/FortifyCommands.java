package com.fortify.fod.parser;

import com.beust.jcommander.Parameter;

import java.util.ArrayList;
import java.util.List;

public class FortifyCommands {
    private static final String HELP = "-help";
    private static final String HELP_SHORT = "-h";
    @Parameter(names = { HELP, HELP_SHORT },
            description = "print this message",
            help = true)
    public boolean help = false;

    private static final String VERSION = "-version";
    private static final String VERSION_SHORT = "-v";
    @Parameter(names = { VERSION, VERSION_SHORT },
            description = "print the version information and exit",
            help = true)
    public boolean version = false;

    private static final String BSI_URL = "-bsiUrl";
    private static final String BSI_URL_SHORT = "-u";
    @Parameter(names = { BSI_URL, BSI_URL_SHORT },
            description = "build server url",
            required = true)
    public String bsiUrl;

    // TODO: Use custom type FileConverter or something
    private static final String ZIP_LOCATION = "-zipLocation";
    private static final String ZIP_LOCATION_SHORT = "-z";
    @Parameter(names = { ZIP_LOCATION, ZIP_LOCATION_SHORT },
            description = "location of scan",
            required = true)
    public String zipLocation;

    private static final String POLLING_INTERVAL = "-pollingInterval";
    private static final String POLLING_INTERVAL_SHORT = "-I";
    @Parameter(names = { POLLING_INTERVAL, POLLING_INTERVAL_SHORT },
            description = "interval between checking scan status")
    public int pollingInterval = 0;

    private static final String RUN_SONATYPE_SCAN = "-runSonatypeScan";
    private static final String RUN_SONATYPE_SCAN_SHORT = "-s";
    @Parameter(names = { RUN_SONATYPE_SCAN, RUN_SONATYPE_SCAN_SHORT},
            description = "whether to run a Sonatype Scan")
    public boolean runSonatypeScan = false;

    private static final String AUDIT_PREFERENCE_ID = "-auditPreferenceId";
    private static final String AUDIT_PREFERENCE_ID_SHORT = "-a";
    @Parameter(names = { AUDIT_PREFERENCE_ID, AUDIT_PREFERENCE_ID_SHORT },
            description = "false positive audit type (Manual or Automated)")
    public int auditPreferenceType = 1;
    public boolean hasAuditPreferenceType() {
//        return auditPreferenceType != null &&
//                auditPreferenceType.getValue() != 0;
        return auditPreferenceType != 0;
    }

    private static final String SCAN_PREFERENCE_ID = "-scanPreferenceId";
    private static final String SCAN_PREFERENCE_ID_SHORT = "-p";
    @Parameter(names = { SCAN_PREFERENCE_ID, SCAN_PREFERENCE_ID_SHORT },
            description = "scan mode (Standard or Express)")
    public int scanPreferenceType = 1;
    public boolean hasScanPreferenceType() {
//        return scanPreferenceType != null &&
//                scanPreferenceType.getValue() != 0;
        return scanPreferenceType != 0;
    }

    private static final String ENTITLEMENT_ID = "-entitlementId";
    private static final String ENTITLEMENT_ID_SHORT = "-e";
    @Parameter(names = { ENTITLEMENT_ID, ENTITLEMENT_ID_SHORT },
            description = "entitlement id")
    public int entitlementId;

    private static final String PARENT_ASSESSMENT_TYPE_ID = "-parentAssessmentTypeId";
    private static final String PARENT_ASSESSMENT_TYPE_ID_SHORT = "-pa";
    @Parameter(names = { PARENT_ASSESSMENT_TYPE_ID, PARENT_ASSESSMENT_TYPE_ID_SHORT },
            description = "Parent Assessment Type ID for Bundled Assessments")
    public int parentAssessmentTypeId;
    public boolean hasParentAssessmentTypeId() {
        return parentAssessmentTypeId != 0;
    }

    private static final String IS_BUNDLED_ASSESSMENT = "-isBundledAssessment";
    private static final String IS_BUNDLED_ASSESSMENT_SHORT = "-b";
    @Parameter(names = { IS_BUNDLED_ASSESSMENT, IS_BUNDLED_ASSESSMENT_SHORT },
            description = "whether the scan is a bundled assessment")
    public boolean isBundledAssessment = false;

    private static final String EXCLUDE_THIRD_PARTY_LIBS = "-excludeThirdPartyApps";
    private static final String EXCLUDE_THIRD_PARTY_LIBS_SHORT = "-x";
    @Parameter(names = { EXCLUDE_THIRD_PARTY_LIBS, EXCLUDE_THIRD_PARTY_LIBS_SHORT },
            description = "whether to exclude third party libraries")
    public boolean excludeThirdPartyLibs = false;

    private static final String IS_REMEDIATION_SCAN = "-isRemediationScan";
    private static final String IS_REMEDIATION_SCAN_SHORT = "--r";
    @Parameter(names = { IS_REMEDIATION_SCAN, IS_REMEDIATION_SCAN_SHORT },
            description = "whether the scan is in remediation")
    public boolean isRemediationScan = false;

    private static final String API_CREDENTIALS = "-apiCredentials";
    private static final String API_CREDENTIALS_SHORT = "-ac";
    @Parameter(names= { API_CREDENTIALS, API_CREDENTIALS_SHORT },
            description = "api credentials",
            arity = 2)
    public List<String> apiCredentials  = new ArrayList<>();
    public boolean hasApiCredentials() {
        return apiCredentials != null &&
                !apiCredentials.isEmpty() &&
                apiCredentials.size() == 2;
    }

    private static final String USER_CREDENTIALS = "-userCredentials";
    private static final String USER_CREDENTIALS_SHORT = "-uc";
    @Parameter(names= { USER_CREDENTIALS, USER_CREDENTIALS_SHORT },
            description = "user credentials",
            arity = 2)
    public List<String> userCredentials = new ArrayList<>();
    public boolean hasUserCredentials() {
        return userCredentials != null &&
                !userCredentials.isEmpty() &&
                userCredentials.size() == 2;
    }

    private static final String PROXY = "-proxy";
    private static final String PROXY_SHORT = "P";
    @Parameter(names= { PROXY, PROXY_SHORT },
            description = "credentials for accessing the proxy",
            arity = 5)
    public List<String> proxy  = new ArrayList<>();
}
