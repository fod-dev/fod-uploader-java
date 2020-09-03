package com.fortify.fod;

import com.fortify.fod.fodapi.FodEnums.APILookupItemTypes;
import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.LookupItemsModel;
import com.fortify.fod.fodapi.models.PollingScanSummaryDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PollStatus {
    private FodApi fodApi;
    private int pollingInterval;
    private int failCount = 0;
    private final int MAX_FAILS = 3;

    private List<LookupItemsModel> analysisStatusTypes = null;

    /**
     * Constructor
     * @param api api connection to use
     * @param pollingInterval interval to poll
     */
    PollStatus(final FodApi api, final int pollingInterval) {
        fodApi = api;
        this.pollingInterval = pollingInterval;
    }

    /**
     * Prints some info about the release including a vuln breakdown and pass/fail reason
     * @param pollingSummary release to print info on
     */
    private int printPassFail(PollingScanSummaryDTO pollingSummary,int releaseId) {
        try
        {
            // Break if release is null
            if (pollingSummary == null) {
                this.failCount++;
                return 1;
            }
            System.out.println("Number of criticals: " +  pollingSummary.IssueCountCritical());
            System.out.println("Number of highs: " +  pollingSummary.IssueCountHigh());
            System.out.println("Number of mediums: " +  pollingSummary.IssueCountMedium());
            System.out.println("Number of lows: " +  pollingSummary.IssueCountLow());
            System.out.println("For application status details see the customer portal: ");
            System.out.println(String.format("%s/Redirect/Releases/%d", fodApi.getPortalUri(), releaseId));
            boolean isPassed = pollingSummary.PassFailStatus();
            System.out.println("Pass/Fail status: " + (isPassed ? "Passed" : "Failed") );
            if (!isPassed)
            {
                String passFailReason = pollingSummary.PassFailReasonType() == null ?
                        "Pass/Fail Policy requirements not met " :
                        pollingSummary.PassFailReasonType();

                System.out.println("Failure Reason: " + passFailReason);
                return 1;
            }
            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    /**
     * Polls the release status
     * @param releaseId release to poll
     * @param triggeredScanId scan to poll
     * @return true if status is completed | cancelled.
     */


    int releaseStatus(final int releaseId, final int triggeredScanId) {
        boolean finished = false;  // default is failure
        int policyPass = 0;
        try
        {
            while(!finished) {
                Thread.sleep(pollingInterval * 60 * 1000);
                // Get the status of the release
                PollingScanSummaryDTO pollingsummary = fodApi.getReleaseController().getReleaseScanForPolling(releaseId, triggeredScanId);

                if (pollingsummary == null) {
                    failCount++;
                    continue;
                }

                int status = pollingsummary.getAnalysisStatusId();

                // Get the possible statuses only once
                if (analysisStatusTypes == null)
                    analysisStatusTypes = Arrays.asList(fodApi.getLookupController().getLookupItems(APILookupItemTypes.AnalysisStatusTypes));

                if(failCount < MAX_FAILS)
                {
                    String statusString = "";

                    // Create a list of values that will be used to break the loop if found
                    // This way if any of this changes we don't need to redo the keys or something
                    List<String> complete = analysisStatusTypes.stream()
                            .filter(p -> p.getText().equals("Completed") || p.getText().equals("Canceled") || p.getText().equals("Waiting"))
                            .map(l -> l.getValue())
                            .collect(Collectors.toCollection(ArrayList::new));

                    // Look for and print the status OR break the loop.
                    for(LookupItemsModel o: analysisStatusTypes) {
                        if(o != null) {
                            int analysisStatus = Integer.parseInt(o.getValue());
                            if (analysisStatus == status) {
                                statusString = o.getText().replace("_", " ");
                            }
                            if (complete.contains(Integer.toString(status))) {
                                finished = true;
                            }
                        }
                    }
                    System.out.println("Poll Status: " + statusString);
                    if (statusString.equals("Canceled") || statusString.equals("Waiting") ) {
                        policyPass = status;
                        String message = statusString.equals("Canceled") ? "-------Scan Canceled-------" : "-------Scan Paused-------";
                        String reason = statusString.equals("Canceled") ? "Cancel reason:        %s" : "Pause reason:        %s";
                        String reasonNotes = statusString.equals("Canceled") ? "Cancel reason notes:  %s" : "Pause reason notes:  %s";

                        System.out.println(message);
                        int pauseDetailsLength = 0;

                        if(statusString.equals("Waiting")){
                            pauseDetailsLength = pollingsummary.getPauseDetails().length > 0 ? pollingsummary.getPauseDetails().length : 0;
                        }
                        System.out.println(String.format(reason, statusString.equals("Canceled") ? (pollingsummary.getAnalysisStatusReason() == null ? "" : pollingsummary.getAnalysisStatusReason())
                                :  ((pauseDetailsLength > 0 ) ? (pollingsummary.getPauseDetails()[pauseDetailsLength-1].getReason() == null) ?"" : pollingsummary.getPauseDetails()[pauseDetailsLength-1].getReason(): "")));
                        System.out.println(String.format(reasonNotes, statusString.equals("Canceled") ? (pollingsummary.getAnalysisStatusReasonNotes() == null ? "" : pollingsummary.getAnalysisStatusReasonNotes())
                                :  ((pauseDetailsLength > 0 ) ? (pollingsummary.getPauseDetails()[pauseDetailsLength-1].getNotes() == null) ? "" : pollingsummary.getPauseDetails()[pauseDetailsLength-1].getNotes()  : "")));
                        System.out.println();


                    }
                    if(statusString.equals("Completed"))
                    {
                        policyPass = printPassFail(pollingsummary, releaseId);
                    }
                }
                else
                {
                    System.out.println("getStatus failed 3 consecutive times terminating polling");
                    finished = true;
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return policyPass;
    }
}
