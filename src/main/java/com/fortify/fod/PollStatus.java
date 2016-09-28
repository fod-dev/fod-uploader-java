package com.fortify.fod;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.LookupItemsModel;
import com.fortify.fod.fodapi.models.ReleaseDTO;

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
     * Polls the release status
     * @param releaseId release to poll
     * @return true if status is completed | cancelled.
     */
    boolean releaseStatus(final int releaseId) {
        boolean finished = false; // default is failure

        try
        {
            while(!finished)
            {
                Thread.sleep(pollingInterval*60*1000);
                // Get the status of the release
                ReleaseDTO release = fodApi.getReleaseController().getRelease(releaseId,
                            "currentAnalysisStatusTypeId,isPassed,passFailReasonId,critical,high,medium,low");
                if (release == null) {
                    failCount++;
                    continue;
                }

                int status = release.getCurrentAnalysisStatusTypeId();

                // Get the possible statuses only once
                if(analysisStatusTypes == null)
                    analysisStatusTypes = Arrays.asList(fodApi.getLookupController().getLookupItems("AnalysisStatusTypes"));

                if(failCount < MAX_FAILS)
                {
                    String statusString = "";

                    // Create a list of values that will be used to break the loop if found
                    // This way if any of this changes we don't need to redo the keys or something
                    List<String> complete = analysisStatusTypes.stream()
                            .filter(p -> p.getText().equals("Completed") || p.getText().equals("Canceled"))
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
                    System.out.println("Status: " + statusString);
                    if(finished)
                    {
                        printPassFail(release);
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
        return finished;
    }

    /**
     * Prints some info about the release including a vuln breakdown and pass/fail reason
     * @param release release to print info on
     */
    private void printPassFail(ReleaseDTO release) {
        try
        {
            // Break if release is null
            if (release == null) {
                this.failCount++;
                return;
            }
            boolean isPassed = release.isPassed();
            System.out.println("Pass/Fail status: " + (isPassed ? "Passed" : "Failed") );
            if (!isPassed)
            {
                String passFailReason = release.getPassFailReasonType() == null ?
                        "Pass/Fail Policy requirements not met " :
                        release.getPassFailReasonType();

                System.out.println("Failure Reason: " + passFailReason);
                System.out.println("Number of criticals: " +  release.getCritical());
                System.out.println("Number of highs: " +  release.getHigh());
                System.out.println("Number of mediums: " +  release.getMedium());
                System.out.println("Number of lows: " +  release.getLow());

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
