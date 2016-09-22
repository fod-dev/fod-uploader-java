package com.fortify.fod;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.Arrays;
import java.util.Map;

import com.fortify.fod.fodapi.Api;
import com.fortify.fod.legacy.LegacyMain;
import com.fortify.fod.parser.BsiUrl;
import com.fortify.fod.parser.FortifyCommandLine;
import com.fortify.fod.parser.FortifyParser;
import com.fortify.fod.parser.Proxy;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

public class Main {

	
	private static String token = "";
	private static String url = "";
	private static String releaseId = "";
	private static int consecutiveGetStatusFailureCount = 0;
	private static DefaultHttpClient httpclient;
	private static String username = "";
	private static String password = "";
	private static String tenantCode = "";
	
	/**
	 * @param args Required:  zip location, bsi url, username/password or api key/secret
	 */
	public static void main(String[] args) {
        FortifyParser fortifyCommands = new FortifyParser();
        FortifyCommandLine cl = fortifyCommands.parse(args);

        // Use legacy commands
        if (fortifyCommands.useLegacy()) {
            // Remove the "-l" argument
            String[] newArgs = Arrays.copyOfRange(args, 1, args.length);
            LegacyMain.main(newArgs);
        // Use new stuff
        } else {
            final int segmentLength = 1024 * 1024;        // chunk size
            final long maxFileSize = 5000 * 1024 * 1024L;
            boolean uploadSucceeded = false;
            boolean lastFragment = false;
            long bytesSent = 0;
            String errorMessage = "";
            boolean authenticationSucceeded = false;

            try {
                httpclient = new DefaultHttpClient();
                if(cl.hasBsiUrl()) {
                    BsiUrl bsiUrl = cl.getBsiUrl();
                    Api fodApi = new Api(bsiUrl.getEndpoint(), cl.getProxy());

                    if (cl.hasZipLocation() && (cl.hasApiCredentials() || cl.hasLoginCredentials())) {

                        String zipLocation = cl.getZipLocation();

                        tenantCode = bsiUrl.getTenantCode();
                        Map<String, String> tempCredentials;
                        // Has username/password
                        if (cl.hasLoginCredentials()) {
                            tempCredentials = cl.getLoginCredentials();
                            username = tempCredentials.get("username");
                            password = tempCredentials.get("password");
                        // Has key/secret
                        } else {
                            tempCredentials = cl.getApiCredentials();
                            username = tempCredentials.get("key");
                            password = tempCredentials.get("secret");
                        }

                        token = fodApi.authenticate(tenantCode, username, password, cl.hasLoginCredentials());

                        //first thing check file size
                        File zipFileInfo = new File(zipLocation);
                        if (zipFileInfo.length() > maxFileSize) {
                            System.out.println("Terminating upload. File Exceeds maximum length : " + maxFileSize);
                            return;
                        }

                        if (token != null && !token.isEmpty()) {
                            authenticationSucceeded = true;
                            FileInputStream fs = new FileInputStream(zipLocation);
                            byte[] readByteArray = new byte[segmentLength];
                            byte[] sendByteArray;
                            int fragmentNumber = 0;
                            int byteCount = 0;
                            long offset = 0;
                            while ((byteCount = fs.read(readByteArray)) != -1) {
                                if (byteCount < segmentLength) {
                                    fragmentNumber = -1;
                                    lastFragment = true;
                                    sendByteArray = Arrays.copyOf(readByteArray, byteCount);
                                } else {
                                    sendByteArray = readByteArray;
                                }
                                String fragUrl;
                                if (bsiUrl.hasLanguageLevel()) {
                                    fragUrl = bsiUrl.getEndpoint() + "/api/v1/release/" + releaseId + "/scan/?assessmentTypeId="
                                            + bsiUrl.getAssessmentTypeId() + "&technologyStack=" + bsiUrl.getTechnologyStack()
                                            + "&languageLevel=" + bsiUrl.getLanguageLevel() + "&fragNo=" + fragmentNumber++
                                            + "&len=" + byteCount + "&offset=" + offset;
                                } else {
                                    fragUrl = bsiUrl.getEndpoint() + "/api/v1/release/" + releaseId + "/scan/?assessmentTypeId="
                                            + bsiUrl.getAssessmentTypeId() + "&technologyStack=" + bsiUrl.getTechnologyStack()
                                            + "&fragNo=" + fragmentNumber++ + "&len=" + byteCount + "&offset=" + offset;
                                }
                                if (cl.hasScanPreferenceId()) {
                                    fragUrl += "&scanPreferenceId=" + cl.getScanPreferenceId();
                                }
                                if (cl.hasAuditPreferencesId()) {
                                    fragUrl += "&auditPreferenceId=" + cl.getAuditPreferenceId();
                                }
                                if (cl.hasRunSonatypeScan()) {
                                    fragUrl += "&doSonatypeScan=" + cl.hasRunSonatypeScan();
                                }
                                String postErrorMessage = "";
                                SendPostResponse postResponse = sendPost(fragUrl, sendByteArray, httpclient, token, postErrorMessage);
                                HttpResponse response = postResponse.getResponse();
                                if (response == null) {
                                    errorMessage = postResponse.getErrorMessage();
                                    break;
                                } else {

                                    StatusLine sl = response.getStatusLine();
                                    Integer statusCode = sl.getStatusCode();
                                    if (!statusCode.toString().startsWith("2")) {
                                        errorMessage = sl.toString();
                                        break;
                                    } else {
                                        if (fragmentNumber != 0 && fragmentNumber % 5 == 0) {
                                            System.out.println("Upload Status - Bytes sent:" + offset);
                                        }
                                        if (lastFragment) {
                                            HttpEntity entity = response.getEntity();
                                            String finalResponse = EntityUtils.toString(entity).trim();
                                            if (finalResponse.toUpperCase().equals("ACK")) {
                                                uploadSucceeded = true;
                                            } else {
                                                errorMessage = finalResponse;
                                            }
                                        }
                                    }
                                    EntityUtils.consume(response.getEntity());
                                }
                                offset += byteCount;
                            }
                            bytesSent = offset;
                            fs.close();

                        } else {
                            errorMessage = "Failed to authenticate";
                        }
                    }

                    //check success status exit appropriately
                    if (uploadSucceeded) {
                        System.out.println("Upload completed successfully. Total bytes sent: " + bytesSent);
                        //TODO: WIP api integration
                        int completionsStatus = pollServerForScanStatus(cl.getPollingInterval(), fodApi);
                        fodApi.retireToken();
                        System.exit(completionsStatus);
                    } else {
                        System.out.println("Package upload failed. Message: " + errorMessage);
                        if (authenticationSucceeded) {
                            fodApi.retireToken();
                        }
                        System.exit(1);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}

	// arg 0: username
	// arg 1: password
	// arg 2: url
    // arg 3: zip location
	// arg 4: proxy url 
	// arg 5: proxy username
	// arg 6: proxy password
    // arg 7: nt workstation
	// arg 8: nt domain
	//need to parse urls like this:
	//http://www.fod.local/bsi2.aspx?tid=1&tc=tt0@qweqwe.com&pv=187&payloadType=ANALYSIS_PAYLOAD&astid=1&ts=JAVA/J2EE&ll=1.7
	
	private static int pollServerForScanStatus(long pollingInterval, Api fodApi) {
		boolean finished = false;
        int completionStatus = 1; // default is failure
		if(pollingInterval == 0)
			return 0;
	
		try 
		{
			while(!finished)
			{
				Thread.sleep(pollingInterval*60*1000);
				int status = getScanStatus(fodApi);
				if(consecutiveGetStatusFailureCount < 3)
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
						printPassFail();
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

	private static void printPassFail() {
		try 
		{
			String passFailUrl = url + "/api/v2/releases?q=releaseId:" + releaseId + "&fields=isPassed,passFailReasonId,critical,high,medium,low";
			HttpGet get = new HttpGet(passFailUrl);
			get.addHeader("Authorization","Bearer " + token);
			HttpResponse response  = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			Gson gson = new Gson();
			ReleaseQueryResponse requestQueryResponse = gson.fromJson(responseString, ReleaseQueryResponse.class);
			boolean isPassed = requestQueryResponse.getData()[0].isPassed();
			System.out.println("Pass/Fail status: " + (isPassed ? "Passed" : "Failed") );
			if(isPassed == false)
			{
				String passFailReason = "";
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
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private static int getScanStatus(Api fodApi) {
		int result = -1;
		try {
			String statusUrl = url + "/api/v2/releases?q=releaseId:" + releaseId + "&fields=status";
			HttpGet get = new HttpGet(statusUrl);
			get.addHeader("Authorization","Bearer " + token);
			HttpResponse response  = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			Gson gson = new Gson();
			ReleaseQueryResponse requestQueryResponse = gson.fromJson(responseString, ReleaseQueryResponse.class);
			if(requestQueryResponse != null)  // did not get back the expected response
			{
				int responseCode = requestQueryResponse.getResponseCode();
				if(responseCode == 200)
				{
					ReleaseInfo[] releaseInfo = requestQueryResponse.getData();
					if(releaseInfo != null && releaseInfo.length == 1 )
					{
						result = requestQueryResponse.getData()[0].getStatus();
						consecutiveGetStatusFailureCount = 0;
					}
				}
				else if(responseCode == 401)   // got logged out during polling so log back in
				{
					System.out.println("Token expired re-authorizing");
                    //TODO: this is lame will clean up
                    token = fodApi.authenticate(tenantCode, username, password, fodApi.useClientId());
					if(token == null || token.isEmpty() )
					{
						System.out.println("Failed to reauthorize");
						consecutiveGetStatusFailureCount++;
					}
					else
					{
						result = 1;  // set status to in progress
					}
				}
				else
				{
					consecutiveGetStatusFailureCount++;
				}
				
			}
			else
			{
				consecutiveGetStatusFailureCount++;
			}
		}
		catch (IOException e) 
		{	
			e.printStackTrace();
		}
		return result;
	}

	private static SendPostResponse sendPost(String url, byte[] bytesToSend, HttpClient client, String token, String errorMessage)
	{
		SendPostResponse result = new SendPostResponse();
		try {
			HttpPost httppost = new HttpPost(url);
			httppost.addHeader("Authorization","Bearer " + token);
			ByteArrayEntity entity = new ByteArrayEntity(bytesToSend);		
			httppost.setEntity(entity);
			HttpResponse response = client.execute(httppost);
			result.setResponse(response);
			result.setErrorMessage("");
		} catch (ParseException e) {
			errorMessage = e.getMessage();
			result.setResponse(null);
			result.setErrorMessage(errorMessage);
		} catch (IOException e) {
			errorMessage = e.getMessage();
			result.setResponse(null);
			result.setErrorMessage(errorMessage);
		}	catch (Exception e) {
			errorMessage = e.getMessage();
			result.setResponse(null);
			result.setErrorMessage(errorMessage);
		}
		return result;
	}
	
	
}
