package com.fortify.fod.fodapi.models;

import com.fortify.fod.fodapi.FodEnums;

public class PutStaticScanSetupRequest{
    public int assessmentTypeId;
    public FodEnums.EntitlementFrequencyTypes entitlementFrequencyType;
    public int technologyStackId;
    public int languageLevelId;
    public boolean performOpenSourceAnalysis;
    public FodEnums.AuditPreferenceTypes auditPreferenceType;
    public boolean includeThirdPartyLibraries;
    public boolean useSourceControl;
    public boolean scanBinary;
}
