package com.fortify.fod.fodapi.models;

import java.util.Date;

public class ReleaseDTO {
	private int releaseId;
	private String releaseName;
    private String releaseDescription;
    private Date releaseCreatedDate;
    private int applicationId;
    private String applicationName;
    private int currentAnalysisStatusTypeId;
    private String currentAnalysisStatusType;
    private int rating;
    private int critical;
    private int high;
    private int medium;
    private int low;
    private int currentStaticScanId;
    private int currentDynamicScanId;
    private int currentMobileScanId;
    private String staticAnalysisStatusType;
    private String dynamicAnalysisStatusType;
    private String mobileAnalysisStatusType;
    private int staticAnalysisStatusTypeId;
    private int dynamicAnalysisStatusTypeId;
    private int mobileAnalysisStatusTypeId;
    private Date staticScanDate;
    private Date dynamicScanDate;
    private Date mobileScanDate;
    private int issueCount;
    private boolean isPassed;
    private int passFailReasonTypeId;
    private String passFailReasonType;
    private int sdlcStatusTypeId;
    private String sdlcStatusType;
    private String statusReason;

    public int getReleaseId() {return releaseId;}
    public String getReleaseName() {return releaseName;}
    public String getReleaseDescription() {return releaseDescription;}
    public Date getReleaseCreatedDate() {return releaseCreatedDate;}
    public int getApplicationId() {return applicationId;}
    public String getApplicationName() {return applicationName;}
    public int getCurrentAnalysisStatusTypeId() {return currentAnalysisStatusTypeId;}
    public String getCurrentAnalysisStatusType() {return currentAnalysisStatusType;}
    public int getRating() {return rating;}
    public int getCritical() {return critical;}
    public int getHigh() {return high;}
    public int getMedium() {return medium;}
    public int getLow() {return low;}
    public int getCurrentStaticScanId() {return currentStaticScanId;}
    public int getCurrentDynamicScanId() {return currentDynamicScanId;}
    public int getCurrentMobileScanId() {return currentMobileScanId;}
    public String getStaticAnalysisStatusType() {return staticAnalysisStatusType;}
    public String getDynamicAnalysisStatusType() {return dynamicAnalysisStatusType;}
    public String getMobileAnalysisStatusType() {return mobileAnalysisStatusType;}
    public int getStaticAnalysisStatusTypeId() {return staticAnalysisStatusTypeId;}
    public int getDynamicAnalysisStatusTypeId() {return dynamicAnalysisStatusTypeId;}
    public int getMobileAnalysisStatusTypeId() {return mobileAnalysisStatusTypeId;}
    public Date getStaticScanDate() {return staticScanDate;}
    public Date getDynamicScanDate() {return dynamicScanDate;}
    public Date getMobileScanDate() {return mobileScanDate;}
    public int getIssueCount() {return issueCount;}
    public boolean isPassed() {return isPassed;}
    public int getPassFailReasonTypeId() {return passFailReasonTypeId;}
    public String getPassFailReasonType() {return passFailReasonType;}
    public int getSdlcStatusTypeId() {return sdlcStatusTypeId;}
    public String getSdlcStatusType() {return sdlcStatusType;}
    public String getStatusReason() {return statusReason;}
}
