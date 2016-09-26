package com.fortify.fod;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.LookupItemsModel;
import com.fortify.fod.fodapi.models.ReleaseDTO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PollStatus {
    private int releaseId;
    private int pollingInterval;
    private int failCount = 0;
    private final int MAX_FAILS = 3;

    private List<LookupItemsModel> analysisStatusTypes = null;

    PollStatus(int releaseId, int pollingInterval) {
        this.releaseId = releaseId;
        this.pollingInterval = pollingInterval;
    }

    boolean releaseStatus(FodApi fodApi) {
        boolean finished = false; // default is failure

        try
        {
            while(!finished)
            {
                Thread.sleep(pollingInterval*60*1000);
                int status = fodApi.getReleaseController().getRelease(releaseId, "currentAnalysisStatusTypeId")
                        .getCurrentAnalysisStatusTypeId();

                // Get the possible statuses only once
                if(analysisStatusTypes == null) {
                    analysisStatusTypes = Arrays.asList(fodApi.getLookupController().getLookupItems("AnalysisStatusTypes"));
                }

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
                        printPassFail(fodApi);
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

    private void printPassFail(FodApi fodApi) {
        try
        {
            ReleaseDTO requestQueryResponse = fodApi.getReleaseController()
                    .getRelease(releaseId, "isPassed,passFailReasonId,critical,high,medium,low");
            if (requestQueryResponse == null)
                this.failCount++;
            boolean isPassed = requestQueryResponse.isPassed();
            System.out.println("Pass/Fail status: " + (isPassed ? "Passed" : "Failed") );
            if (!isPassed)
            {
                String passFailReason;
                switch(requestQueryResponse.getPassFailReasonTypeId())
                {
                    case 1:
                        passFailReason = "Unassessed";
                        break;
                    case 2:
                        passFailReason = "Override";
                        break;
                    case 3:
                        passFailReason = "GracePeriod";
                        break;
                    case 14:
                        passFailReason = "ScanFrequency";
                        break;
                    default:
                        passFailReason = "Pass/Fail Policy requirements not met ";
                        break;
                }
                System.out.println("Failure Reason: " + passFailReason);
                System.out.println("Number of criticals: " +  requestQueryResponse.getCritical());
                System.out.println("Number of highs: " +  requestQueryResponse.getHigh());
                System.out.println("Number of mediums: " +  requestQueryResponse.getMedium());
                System.out.println("Number of lows: " +  requestQueryResponse.getLow());

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
