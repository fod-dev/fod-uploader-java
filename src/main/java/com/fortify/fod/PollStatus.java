package com.fortify.fod;

import com.fortify.fod.fodapi.FodApi;
import com.fortify.fod.fodapi.models.ReleaseModel;

class PollStatus {
    private int releaseId;
    private int pollingInterval;
    private int failCount = 0;
    private final int MAX_FAILS = 3;

    PollStatus(int releaseId, int pollingInterval) {
        this.releaseId = releaseId;
        this.pollingInterval = pollingInterval;
    }

    int releaseStatus(FodApi fodApi) {
        boolean finished = false;
        int completionStatus = 1; // default is failure

        try
        {
            while(!finished)
            {
                Thread.sleep(pollingInterval*60*1000);
                int status = fodApi.getReleaseController().getRelease(releaseId, "status")
                        .getData()[0].getStatus();
                if(failCount < MAX_FAILS)
                {
                    String statusString = "";
                    switch(status)
                    {
                        case 1:
                            finished = false;
                            statusString = "In Progress";
                            break;
                        case 2:
                            finished = true;
                            statusString = "Completed";
                            completionStatus = 0;
                            break;
                        case 3:
                            finished = true;
                            statusString = "Cancelled";
                            break;
                        case 4:
                            finished = false;
                            statusString = "Waiting";
                            break;
                        default:  // for every other status value continue polling
                            break;
                    }
                    System.out.println("Status: " + statusString);
                    if(completionStatus == 0)
                    {
                        printPassFail(fodApi);
                    }
                }
                else
                {
                    finished = true;
                    System.out.println("getStatus failed 3 consecutive times terminating polling");
                    completionStatus = 1;
                }
            }
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        return completionStatus;
    }

    private void printPassFail(FodApi fodApi) {
        try
        {
            ReleaseModel requestQueryResponse = fodApi.getReleaseController()
                    .getRelease(releaseId, "isPassed,passFailReasonId,critical,high,medium,low");

            boolean isPassed = requestQueryResponse.getData()[0].isPassed();
            System.out.println("Pass/Fail status: " + (isPassed ? "Passed" : "Failed") );
            if (!isPassed)
            {
                String passFailReason;
                switch(requestQueryResponse.getData()[0].getPassedFailReasonId())
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
                System.out.println("Number of criticals: " +  requestQueryResponse.getData()[0].getCritical());
                System.out.println("Number of highs: " +  requestQueryResponse.getData()[0].getHigh());
                System.out.println("Number of mediums: " +  requestQueryResponse.getData()[0].getMedium());
                System.out.println("Number of lows: " +  requestQueryResponse.getData()[0].getLow());

            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
