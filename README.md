# fod-uploader-java
Java utility for uploading code to Fortify on Demand

## Usage

### Current

**Note**: Command-line arguments have been reworked since 3.1.0. If you are upgrading from an older version to the latest version, make sure to update your arguments.

The following table describes the FoDUploader arguments. Arguments are named and can be in any order: 

Short Name	| Long Name                     | Required?         | Description                                                      
----------	| ----------                    |----------         | ----------
 -z			     | -zipLocation                  | Yes               | Location of payload zip file
 -ep		     | -entitlementPreferenceType    | Yes               | Whether to use a single scan or subscription assessment: 1/SingleScanOnly, 2/SubscriptionOnly, 3/SingleScanFirstThenSubscription, 4/SubscriptionFirstThenSingleScan 
 -ac		     | -apiCredentials               | Yes<sup>1</sup>   | API credentials credentials                                                 
 -uc		     | -userCredentials              | Yes<sup>1</sup>   | User credentials (wrap each in quotations to handle certain non-alphanumeric characters in the CLI) 
 -bsi	    	| -bsiToken                     | Yes<sup>2</sup>   | BSI token
 -rid	    	| -releaseId                    | Yes<sup>2</sup>   | Release ID
 -purl		   | -portalurl                    | Yes<sup>3</sup>   | Domain URL
 -aurl		   | -apiurl                       | Yes<sup>3</sup>   | API root URL
 -tc		     | -tenantCode                   | Yes<sup>3</sup>   | Tenant ID if using user credentials 
 -at		     | -assessmentTypeId				         | Yes<sup>4</sup>   | Assessment type ID
 -eid	    	| -entitlement					             | Yes<sup>4</sup>   | Entitlement ID
 -ts		     | -technologyStackId 			        | Yes<sup>4</sup>   | Technology stack as an integer: 1 (.NET), 23 (.Net Core), 2 (ABAP), 21 (Apex/Visualforce), 3 (ASP), 5 (CFML), 6 (COBOL), 22 (Go), 7 (JAVA/J2EE), 16 (JS/TS/HTML), 18 (MBS/C/C++/Scala), 9 (PHP), 10 (PYTHON), 17 (Ruby), 12 (Swift/Objective C/C++), 11 (VB6), 14 (VBScript)
 -l			     | -languageLevelId				          | Yes<sup>4</sup>   | Language level as an integer:<br />.NET: 2 (2.0), 3 (3.0), 4 (3.5), 5 (4.0), 11 (4.5), 15 (4.6), 16 (4.7), 30, (4.8), 32 (5.0)<br /> .NET Core: 23 (1.0), 24 (1.1), 25 (2.0), 26 (2.1), 27 (2.2), 28 (3.0), 29 (3.1)<br />Java: 8 (1.5), 9 (1.6), 10 (1.7), 12 (1.8), 17 (1.9), 19 (10), 20 (11), 21 (12), 22 (13)<br /> Python: 13 (2), 14 (2 Django), 18 (3)
 -a			     | -auditPreferenceId 		        	| Yes<sup>4</sup>	  | Audit preference: Manual, Automated
 -bs		     | -isBinaryScan					            | No<sup>4</sup>	   | Scan compiled and source code (the feature must be enabled)
 -os 		    | -allowopenSourceComponentAnalysis| No<sup>4</sup>	| Include open source component analysis
 -rp       | -remediationScanPreferenceType| No                | Remediation scan preference: 0/RemediationScanIfAvailable, 1/RemediationScanOnly, 2/NonRemediationScanOnly (default)
 -pp		     | -inProgressScanActionType     | No                | If in-progress scan exists, the action to take for a new scan: 0/DoNotStartScan (default), 1/CancelScanInProgress, 2/Queue
 -purchase	| -purchaseEntitlement          | No				            | Whether to purchase an entitlement (if available)
 -apf		| -allowPolicyFail                  | No                | Whether to return exit(0) instead of exit(1) if the scan fails the security policy specified in Fortify on Demand 
 -n			     | -notes                        | No                | The notes about the scan
 -I		     	| -pollingInterval              | No                | Interval between checking scan status in minutes                 
 -P			     | -proxy                        | No                | Proxy connection details (order dependent): <proxy_url> <username> <password> <nt_domain> <nt_workstation>             
 -h			     | -help                         | No                | Print help dialog                                                
 -v			     | -version                      | No                | Print jar version   

 <sup>1</sup>Use either apiCredentials or userCredentials.  
 <sup>2</sup>Use either releaseId or bsiToken. If both are provided, then the scan settings that are retrieved from the release ID will be used.  
 <sup>3</sup>Required if BSI token is not provided.  
 <sup>4</sup>Required if neither release ID nor BSI token is provided. Values override existing release ID or BSI token settings.

Syntax:
```
FodUpload.jar -z <zip_file_path> -ep {1|SingleScanOnly|2|SubscriptionOnly|3|SingleScanFirstThenSubscription|4|SubscriptionFirstThenSingleScan} {-ac <key> <secret> | -uc <username> <password>} {-rid <release_id> | -bsi <token> | -at <assessment_id> -eid <entitlement_id> -ts <tstack_id> -l <lang_id> <-a {Manual|Automated} bs -os} -purl <domain_url> -aurl <api_url> -tc <tenant_id>  [-rp {0|RemediationScanIfAvailable|1|RemediationScanOnly|2|NonRemediationScanOnly}] [-pp {0|DoNotStartScan|1|CancelScanInProgress|2|Queue}] [-purchase] [-apf] [-n] [-I <minutes>] [-P <proxyUrl> <username> <password> <nt_domain> <nt_workstation>] [-h] [-v]
```

### Previous

The following table describes the FoDUploader arguments for 3.1.0:

Short Name | Long Name              | Required? | Description                                                      
---------- | ---------------------- |---------  | --------------------------------------------------------
 -bsi      | -bsiToken              | Yes       | Build server token
 -z        | -zipLocation           | Yes       | Location of scan 
 -ep       | -entitlementPreference | Yes       | Whether to use a single scan or subscription assessment (if available) (1/Single, 2/Subscription)
 -ac       | -apiCredentials        | Yes*      | Api credentials ("key:" does not need to be appended to `<key>`)                                                  
 -uc       | -userCredentials       | Yes*      | User login credentials (wrap each in quotations to avoid escaping characters in the CLI)
 -a        | -auditPreferenceId     | No        | False positive audit type (1/Manual, 2/Automated)            
 -p        | -scanPreferenceId      | No        | Scan mode (1/Standard, 2/Express)                            
 -I        | -pollingInterval       | No        | Interval between checking scan status in minutes                 
 -P        | -proxy                 | No        | Credentials for accessing the proxy                   
 -os       | -runOpenSourceScan     | No        | Whether to run an Open Source Scan
 -h        | -help                  | No        | Print help dialog                                                
 -v        | -version               | No        | Print jar version   
 -itp      | -includeThirdPartyLibs | No        | Include Third Party Libraries from scan
 -r        | -isRemediationScan     | No        | Whether the scan is in remediation 
 -b        | -isBundledAssessment   | No        | Whether the scan is a bundled assessment
 -purchase | -purchaseEntitlement   | No		| Whether to purchase an entitlement (if available)
 -n        | -notes                 | No        | The notes about the scan.

*Use either apiCredentials or userCredentials.

Syntax:
```
FodUpload.jar -bsi <token> -z <file> {-ac <key> <secret> | -uc <username> <password>} -ep {1|SingleScan|2|Subscription} [-p {1|Standard|2|Express}] [-a {1|Manual|2|Automated}] [-itp] [-os] [-b] [-r] [-purchase] [-n] [-I <minutes>] [-P <proxy_url> <username> <password> <nt_domain> <nt_workstation>] [-h] [-v] 
```
 
## Developer Setup

FoDUploader is configured to build a fat jar with the Gradle Shadow plugin as the default gradle task.

To compile, simply use the gradlew or gradlew.bat depending on your operating system.

```
.\gradlew.bat
```

For a better breakdown of the build process, compile gradle with the following:

```
.\gradlew.bat -I init.gradle build
```

If you are behind a firewall, you will need to configure gradle's proxy settings in:

*/\<user-directory>/.gradle/gradle.properties*

```
systemProp.http.proxyHost=<web-proxy-host>
systemProp.http.proxyPort=<web-proxy-port>

systemProp.https.proxyHost=<web-proxy-host>
systemProp.https.proxyPort=<web-proxy-port>
```
