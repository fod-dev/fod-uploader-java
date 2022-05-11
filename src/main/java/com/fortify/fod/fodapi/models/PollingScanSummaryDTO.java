package com.fortify.fod.fodapi.models;

public class PollingScanSummaryDTO {
    private int ScanId;
    private Integer OpenSourceScanId;
    private int TenantId;
    private int AnalysisStatusId;
    private String AnalysisStatusTypeValue;
    private int AnalysisStatusReasonId;
    private String AnalysisStatusReason;
    private String AnalysisStatusReasonNotes;
    private int IssueCountCritical;
    private int IssueCountHigh;
    private int IssueCountMedium ;
    private int IssueCountLow;
    private boolean PassFailStatus;
    private String PassFailReasonType;
    private ScanPauseDetailPolling[] PauseDetails;

    public int getScanId() {return ScanId;}
    public Integer getOpenSourceScanId() {return OpenSourceScanId ;}
    public int getTenantId() {return TenantId;}
    public int getAnalysisStatusId() {return AnalysisStatusId;}
    public String getAnalysisStatusTypeValue() {return AnalysisStatusTypeValue;}
    public int getAnalysisStatusReasonId(){return AnalysisStatusReasonId;}
    public String getAnalysisStatusReason(){return AnalysisStatusReason;}
    public String getAnalysisStatusReasonNotes(){return AnalysisStatusReasonNotes;}
    public int IssueCountCritical(){return IssueCountCritical;}
    public int IssueCountHigh(){return IssueCountHigh;}
    public int IssueCountMedium (){return IssueCountMedium;}
    public int IssueCountLow(){return IssueCountLow;}
    public boolean PassFailStatus(){return PassFailStatus;}
    public String PassFailReasonType(){return PassFailReasonType;}
    public ScanPauseDetailPolling[] getPauseDetails() {
        ScanPauseDetailPolling[] returnDetails = PauseDetails;
        return returnDetails;
    }
}