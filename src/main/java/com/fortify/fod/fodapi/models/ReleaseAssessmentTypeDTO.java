package com.fortify.fod.fodapi.models;

public class ReleaseAssessmentTypeDTO {
    private int assessmentTypeId;
    private String name;
    private String scanType;
    private int scanTypeId;
    private int entitlementId;
    private String frequencyType;
    private int frequencyTypeId;
    private int units;
    private int unitsAvailable;
    private int parentAssessmentTypeId;
    private boolean isBundledAssessment;

    public int getAssessmentTypeId() {
        return assessmentTypeId;
    }

    public String getName() {
        return name;
    }

    public String getScanType() {
        return scanType;
    }

    public int getScanTypeId() {
        return scanTypeId;
    }

    public int getEntitlementId() {
        return entitlementId;
    }

    public void setEntitlementId(int value) {
        entitlementId = value;
    }

    public String getFrequencyType() {
        return frequencyType;
    }

    public int getFrequencyTypeId() {
        return frequencyTypeId;
    }

    public int getUnits() {
        return units;
    }

    public int getUnitsAvailable() {
        return unitsAvailable;
    }

    public int getParentAssessmentTypeId() {
        return parentAssessmentTypeId;
    }

    public boolean isBundledAssessment() {
        return isBundledAssessment;
    }

}