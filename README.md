# fod-uploader-java
Java utility for uploading packages to FoD

## Usage

### Current

*Note*: Command line arguments have been reworked since 3.1.0. When moving from an older version to the latest version, make sure to adjust your arguments to the current format.

Arguments are named and can be in any order: 

```
FodUpload.jar -ac <key> <secret> | -uc <username> <password> -rid <relID>|-bsi <token> -purl <purl> -aurl <aurl> -tc <tcode> -z <file> -ep <0|SingleScanOnly|1|SubscriptionOnly|2|SingleScanFirstThenSubscription|3|SubscriptionFirstThenSingleScan> -rp <0|RemediationScanIfAvailable|1|RemediationScanOnly|2|NonRemediationScanOnly> -pp <0|DoNotStartScan|1|CancelScanInProgress> [-purchase] [-b] [-I <minutes>] [-p <1|Standard|2|Express>] [-a <1|Manual|2|Automated>] [-P <proxyUrl> <username> <password> <ntDomain> <ntWorkstation>] [-itp] [-os] [-n] [-h] [-v]
```

Each argument has a short and long name:

Short Name | Long Name                     | Required? | Description                                                      
---------- | ----------------------        |:---------:| --------------------------------------------------------
 -ac       | -apiCredentials               | Yes<sup>1</sup>   | API credentials ("key:" does not need to be appended to `<key>`)                                                  
 -uc       | -userCredentials              | Yes<sup>1</sup>   | User credentials (wrap each in quotations to avoid escaping characters in the CLI) 
 -bsi      | -bsiToken                     | Yes<sup>2</sup>   | Build server token
 -rid      | -releaseId                    | Yes<sup>2</sup>   | Release ID
 -purl     | -portalurl                    | Yes<sup>3</sup>   | Domain URL
 -aurl     | -apiurl                       | Yes<sup>3</sup>   | API root URL
 -tc       | -tenantCode                   | Yes<sup>3</sup>   | Tenant ID if using user credentials 
 -z        | -zipLocation                  | Yes               | Location of scan
 -ep       | -entitlementPreferenceType    | Yes               | Whether to use a single scan or subscription assessment: 1/SingleScanOnly, 2/SubscriptionOnly, 3/SingleScanFirstThenSubscription, 4/SubscriptionFirstThenSingleScan
 -rp       | -remediationScanPreferenceType| No                | Whether to run a remediation scan: 0/RemediationScanIfAvailable, 1/RemediationScanOnly, 2/NonRemediationScanOnly (default)
 -pp       | -inProgressScanActionType     | No                | Whether to cancel an in-progress scan and start a new scan or not start a scan: 0/DoNotStartScan (default), 1/CancelScanInProgress                    
 -I        | -pollingInterval              | No                | Interval between checking scan status in minutes                 
 -P        | -proxy                        | No                | Credentials for accessing the proxy                   
 -h        | -help                         | No                | Print help dialog                                                
 -v        | -version                      | No                | Print jar version   
 -purchase | -purchaseEntitlement          | No		              | Whether to purchase an entitlement (if available)
 -n        | -notes                        | No                | The notes about the scan

<sup>1</sup>One of either apiCredentials or userCredentials is required.  
<sup>2</sup>Use either release ID or BSI token. If both are provided, then the scan settings that are retrieved from the release ID will be used.  
<sup>3</sup>Required if BSI token is not provided.

### Previous

If you are using 3.1.0, the arguments are:

Short Name | Long Name              | Required? | Description                                                      
---------- | ---------------------- |:---------:| --------------------------------------------------------
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

## Developer Setup

The FoD-Uploader is configured to build a fat jar with the Gradle Shadow plugin as the default gradle task.

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
