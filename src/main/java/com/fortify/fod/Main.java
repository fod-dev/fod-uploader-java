package com.fortify.fod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fortify.fod.parser.FortifyCommandLine;
import com.fortify.fod.parser.FortifyParser;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

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
	 * @param args
	 */
	public static void main(String[] args) {
        FortifyParser fortifyCommands = new FortifyParser();
        FortifyCommandLine cl = fortifyCommands.parse(args);

		final int seglen = 1024*1024;        // chunk size
		final long maxFileSize = 5000*1024*1024L;
		boolean uploadSucceeded = false;
		boolean sendPostFailed = false;
		boolean lastFragment = false;
		long bytesSent = 0;
		String errorMessage = "";
		boolean authenticationSucceeded = false;

        URI proxyUri = null;
        String proxyUsername = "";
        String proxyPassword = "";
        String ntDomain = "";
        String ntWorkstation = "";
        long pollingInterval = 0;
        try
        {
            httpclient = new DefaultHttpClient();
            Map<String,String> argMap = parseArgs(args);

            if(argMap.containsKey("tenantId") &&
               argMap.containsKey("username") &&
               argMap.containsKey("password") &&
               argMap.containsKey("releaseId") &&
               argMap.containsKey("technologyType") &&
               argMap.containsKey("endpoint") &&
               argMap.containsKey("assessmentTypeId") &&
               argMap.containsKey("zipLocation"))
            {

                url = argMap.get("endpoint");
                String zipLocation = argMap.get("zipLocation");
                username = argMap.get("username");
                password = argMap.get("password");
                String techType = argMap.get("technologyType");
                String assessmentTypeId = argMap.get("assessmentTypeId");
                String tenantId = argMap.get("tenantId");
                tenantCode = argMap.get("tenantCode");
                releaseId = argMap.get("releaseId");
                String languageLevel = "";
                if(argMap.containsKey("pollingInterval"))
                {
                    pollingInterval = Long.parseLong(argMap.get("pollingInterval"));
                }
                if(argMap.containsKey("languageLevel"))
                {
                    languageLevel = argMap.get("languageLevel");
                }

                if(argMap.containsKey("proxy"))
                {
                    proxyUri = new URI(argMap.get("proxy"));
                    HttpHost proxy = new HttpHost(proxyUri.getHost(), proxyUri.getPort(), proxyUri.getScheme());
                    httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
                }

                if(argMap.containsKey("proxyUsername")  && argMap.containsKey("proxyPassword"))
                {
                    proxyUsername = argMap.get("proxyUsername");
                    proxyPassword = argMap.get("proxyPassword");

                    if(argMap.containsKey("ntDomain") && argMap.containsKey("ntWorkstation"))
                    {
                        ntWorkstation = argMap.get("ntWorkstation");
                        ntDomain = argMap.get("ntDomain");
                        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new NTCredentials(proxyUsername , proxyPassword, ntWorkstation, ntDomain));
                    }
                    else
                    {
                        httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(proxyUsername , proxyPassword));
                    }
                }
                //first thing check file size
                File zipFileInfo = new File(zipLocation);
                if(zipFileInfo.length() > maxFileSize)
                {
                    System.out.println("Terminating upload. File Exceeds maximum length : " + maxFileSize);
                    return;
                }

                token = authorize(url, tenantCode, username, password, httpclient);
                if(token != null && !token.isEmpty())
                {
                    authenticationSucceeded = true;
                    FileInputStream fs = new FileInputStream(zipLocation);
                    byte[] readByteArray = new byte[seglen];
                    byte[] sendByteArray = null;
                    int fragmentNumber = 0;
                    int byteCount = 0;
                    long offset = 0;
                    while((byteCount = fs.read(readByteArray)) != -1)
                    {
                      if(byteCount < seglen)
                      {
                          fragmentNumber = -1;
                          lastFragment = true;
                          sendByteArray = Arrays.copyOf(readByteArray, byteCount);
                      }
                      else
                      {
                          sendByteArray = readByteArray;
                      }
                      String fragUrl = "";
                      if(languageLevel != null)
                      {
                          fragUrl = url + "/api/v1/release/" + releaseId + "/scan/?assessmentTypeId=" + assessmentTypeId + "&technologyStack=" + techType + "&languageLevel=" + languageLevel +  "&fragNo=" + fragmentNumber++ + "&len=" + byteCount + "&offset=" + offset;
                      }
                      else
                      {
                          fragUrl = url + "/api/v1/release/" + releaseId + "/scan/?assessmentTypeId=" + assessmentTypeId + "&technologyStack=" + techType +  "&fragNo=" + fragmentNumber++ + "&len=" + byteCount + "&offset=" + offset;
                      }
                      if(argMap.containsKey("scanPreferenceId"))
                      {
                          fragUrl += "&scanPreferenceId=" + argMap.get("scanPreferenceId");
                      }
                      if(argMap.containsKey("auditPreferenceId"))
                      {
                          fragUrl += "&auditPreferenceId=" + argMap.get("auditPreferenceId");
                      }
                      if(argMap.containsKey("runSonatypeScan"))
                      {
                          fragUrl += "&doSonatypeScan=" + argMap.get("runSonatypeScan");
                      }
                      String postErrorMessage = "";
                      SendPostResponse postResponse = sendPost(fragUrl, sendByteArray, httpclient, token, postErrorMessage);
                      HttpResponse response = postResponse.getResponse();
                      if(response == null)
                      {
                          errorMessage = postResponse.getErrorMessage();
                          sendPostFailed = true;
                          break;
                      }
                      else
                      {

                        StatusLine sl = response.getStatusLine();
                        Integer statusCode = Integer.valueOf(sl.getStatusCode());
                        if( !statusCode.toString().startsWith("2") )
                        {
                            errorMessage = sl.toString();
                            break;
                        }
                        else
                        {
                            if(fragmentNumber != 0 && fragmentNumber % 5 == 0)
                            {
                                System.out.println("Upload Status - Bytes sent:" + offset);
                            }
                            if(lastFragment == true)
                            {
                                 HttpEntity entity = response.getEntity();
                                 String finalResponse = EntityUtils.toString(entity).trim();
                                 if(finalResponse.toUpperCase().equals("ACK") )
                                 {
                                     uploadSucceeded = true;
                                 }
                                 else
                                 {
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

                }
                else
                {
                    errorMessage = "Failed to authenticate";
                }
            }
        }
        catch (URISyntaxException e)
        {
            e.printStackTrace();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        //check success status exit appropriately
        if(uploadSucceeded == true)
        {
            System.out.println("Upload completed successfully. Total bytes sent: " + bytesSent );
            int completionsStatus = pollServerForScanStatus(pollingInterval);
            retireToken();
            System.exit(completionsStatus);
        }
        else
        {
            System.out.println("Package upload failed. Message: " + errorMessage);
            if(authenticationSucceeded)
            {
                retireToken();
            }
            System.exit(1);
        }
	}

	@Deprecated
	private static void retireToken() {
		String statusUrl = url + "/oauth/retireToken";
		HttpGet get = new HttpGet(statusUrl);
		get.addHeader("Authorization","Bearer " + token);
		HttpResponse response;
		try {
			response = httpclient.execute(get);
			HttpEntity entity = response.getEntity();
			String responseString = EntityUtils.toString(entity);
			Gson gson = new Gson();
			MessageResponse messageResponse = gson.fromJson(responseString, MessageResponse.class);
			if(messageResponse != null)  // did not get back the expected response
			{
				System.out.println("Retiring Token : " + messageResponse.getMessage());
			}
			
		} catch (IOException e) {
			
			e.printStackTrace();
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
	
	private static int pollServerForScanStatus(long pollingInterval) {
		boolean finished = false;
        int completionStatus = 1; // default is failure
		if(pollingInterval == 0)
			return 0;
	
		try 
		{
			while(!finished)
			{
				Thread.sleep(pollingInterval*60*1000);
				int status = getScanStatus();
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

	private static int getScanStatus() {
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
					token = authorize(url, tenantCode, username, password, httpclient);
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

	@Deprecated
	private static Map<String, String> parseArgs(String[] args) {
		
		Map<String,String> result = new HashMap<String,String>();
		result.put("username", args[0]);
		result.put("password", args[1]);
		String urlString = args[2];
		result.put("zipLocation",args[3]);
		processOptionalArgs(args,result);
		URL url;
		try {
			url = new URL(urlString);
			result.put("endpoint",url.getProtocol() + "://" + url.getAuthority());
			String query = url.getQuery();
			String[] queryNVPairs = query.split("&");
			Map<String,String> queryMap = new HashMap<String, String>();
			for(String pair : queryNVPairs)
				{
				String[] nvPair = pair.split("=");
				if(nvPair.length == 2)
					{
					queryMap.put(nvPair[0], nvPair[1]);
					}
				else
				{
					queryMap.put(nvPair[0], "");
				}
			}
			result.put("releaseId", queryMap.get("pv"));
			result.put("technologyType", queryMap.get("ts"));
			result.put("assessmentTypeId", queryMap.get("astid"));
			result.put("tenantId", queryMap.get("tid"));
			result.put("tenantCode", queryMap.get("tc"));
			result.put("languageLevel", queryMap.get("ll"));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		return result;
	}

	@Deprecated
	private static void processOptionalArgs(String[] args, Map<String, String> result) 
	{
		ArrayList<String> unnamedArgs = removeNamedOptionalArgs(args,result);
		if(unnamedArgs.size() >= 5)
		{
			result.put("proxy",unnamedArgs.get(4));
			if(unnamedArgs.size() >= 7)
			{
				result.put("proxyUsername",unnamedArgs.get(5));
				result.put("proxyPassword",unnamedArgs.get(6));
				if(unnamedArgs.size() == 9)
				{
					result.put("ntWorkStation",unnamedArgs.get(7));
					result.put("ntDomain",unnamedArgs.get(8));
				}
			}
		}
		
	}

    @Deprecated
	private static ArrayList removeNamedOptionalArgs(String[] args, Map<String,String> argMap ) 
	{
		ArrayList<String> result = new ArrayList<String>();
		for(String arg : args)
		{
			if(arg.startsWith("-pollingInterval"))
			{
				String[] split = arg.split(":");
				if(split.length == 2)
				{
					argMap.put("pollingInterval", split[1]);
				}
			}
			else if(arg.startsWith("-scanPreferenceId"))
			{
				String[] split = arg.split(":");
				if(split.length == 2)
				{
					argMap.put("scanPreferenceId", split[1]);
				}
			}
			else if(arg.startsWith("-auditPreferenceId"))
			{
				String[] split = arg.split(":");
				if(split.length == 2)
				{
					argMap.put("auditPreferenceId", split[1]);
				}
			}
			else if(arg.startsWith("-runSonatypeScan"))
			{
				String[] split = arg.split(":");
				if(split.length == 2)
				{
					argMap.put("runSonatypeScan", split[1]);
				}
			}
			else  // unnamed argument
			{
				result.add(arg);
			}
		}
		return result;
	}

	@Deprecated
	private static String authorize(String baseUrl, String tenantCode, String username, String password, HttpClient client)
	{
		String accessToken = "";
		try {
			String endpoint = baseUrl + "/oauth/token";
			HttpPost httppost = new HttpPost(endpoint);
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			if(username.toLowerCase().startsWith("key"))
			{
                 formparams.add(new BasicNameValuePair("scope", "https://hpfod.com/tenant"));
 				 formparams.add(new BasicNameValuePair("grant_type", "client_credentials"));
 				 formparams.add(new BasicNameValuePair("client_id", username.substring(4)));
 				 formparams.add(new BasicNameValuePair("client_secret", password));	
			}
			else
			{
				formparams.add(new BasicNameValuePair("scope", "https://hpfod.com/tenant"));
				formparams.add(new BasicNameValuePair("grant_type", "password"));
				formparams.add(new BasicNameValuePair("username",  tenantCode + "\\" + username));
				formparams.add(new BasicNameValuePair("password", password));	
			}
			
			
			
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, "UTF-8");
			httppost.setEntity(entity);
			HttpResponse response = client.execute(httppost);
			StatusLine sl = response.getStatusLine();
			Integer statusCode = Integer.valueOf(sl.getStatusCode()); 
			if(statusCode.toString().startsWith("2") )
			{
				HttpEntity respopnseEntity = response.getEntity();
				InputStream is = respopnseEntity.getContent();
				 BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			        String line;
			        StringBuffer content = new StringBuffer();
			        while ((line = rd.readLine()) != null) {
			        	content.append(line);
			        	content.append('\r');
			        }
			        rd.close();
			        String x=content.toString();
			        JsonParser parser=new JsonParser();
			        JsonElement jsonElement = parser.parse(x);
			        JsonObject jsonObject = jsonElement.getAsJsonObject();
			        JsonPrimitive tokenPrimitive=jsonObject.getAsJsonPrimitive("access_token");
			        if( tokenPrimitive!=null )
			        {
			            accessToken=tokenPrimitive.getAsString();
			        }
			}
				
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return accessToken;
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
