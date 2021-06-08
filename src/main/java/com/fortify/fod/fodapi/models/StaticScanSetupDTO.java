package com.fortify.fod.fodapi.models;

import com.fortify.fod.fodapi.FodEnums.EntitlementFrequencyTypes;
import com.fortify.fod.fodapi.FodEnums.AuditPreferenceTypes;

public class StaticScanSetupDTO {
    private int assessmentTypeId;
    private int entitlementId;
    private int entitlementFrequencyType;
    private int releaseId;
    private int technologyStackId;
    private String technologyStack;
    private int languageLevelId;
    private String languageLevel;
    private boolean performOpenSourceAnalysis;
    private int auditPreferenceType;
    private boolean includeThirdPartyLibraries;
    private boolean useSourceControl;
    private boolean scanBinary;
    private String bsiToken;

    public int getAssessmentTypeId(){return assessmentTypeId;};
    public int getEntitlementId(){return entitlementId;};
    public int getEntitlementFrequencyType(){return entitlementFrequencyType;};
    public int getReleaseId(){return releaseId;};
    public int getTechnologyStackId(){return technologyStackId;};
    public String getTechnologyStack(){return technologyStack;};
    public int getLanguageLevelId(){return languageLevelId;};
    public String getLanguageLevel(){return languageLevel;};
    public boolean getPerformOpenSourceAnalysis(){return performOpenSourceAnalysis;};
    public int getauditPreferenceType(){return auditPreferenceType;};
    public boolean getincludeThirdPartyLibraries(){return includeThirdPartyLibraries;};
    public boolean getUseSourceControl(){return useSourceControl;};
    public boolean getScanBinary(){return scanBinary;};
    public String getBsiToken(){return bsiToken;};
}
