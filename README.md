# fod-uploader
Java Utility for uploading packages to FoD

## Setup

The FoD-Uploader is configured to build a fat jar with the Gradle Shadow plugin as the default gradle task.

To compile, simply use the gradlew or gradlew.bat depending on your operating system.

```
.\gradlew.bat
```

For a better breakdown of the build proccess, compile gradle with the following:

```
.\gradlew.bat -I init.gradle build
```

If you are behind the HPE firewall, you will need to configure gradles's proxy settings in:

*/\<user-directory>/.gradle/gradle.properties*

```
systemProp.http.proxyHost=web-proxy.atl.hp.com
systemProp.http.proxyPort=8080

systemProp.https.proxyHost=web-proxy.atl.hp.com
systemProp.https.proxyPort=8080

```


## Usage

### Current
The command line arguments have been completely reworked for 5.3. Arguments are now named and can be in any order: 

```
FodUpload.jar -e <id> -f <1|2> -u <url> -z <file> [-a <1|2>] -ac <key> <secret> | -uc <username> <password>
[-h] [-I <minutes>] [-p <1|2>] [-P <proxyUrl> <username> <password> <ntDomain> <ntWorkstation>] [-s <true|false>]
[-v] [-x <true|false>] [-r <true|false]
```
Each option has a short and long name:

Short Name | Long Name              | Required? | Description                                                      
---------- | ---------------------- |:---------:| --------------------------------------------------------
 -u        | -bsiUrl                | Yes       | Build server url                                                 
 -z        | -zipLocation           | Yes       | Location of scan 
 -e        | -entitlementId         | Yes       | Entitlement Id
 -f        | -entitlementFrequency  | Yes       | Entitlement Freqeuncy Type (Single = 1, Subscription = 2)
 -ac       | -apiCredentials        | Yes*      | Api credentials                                                  
 -uc       | -userCredentials       | Yes*      | User login credentials                                           
 -a        | -auditPreferenceId     | No        | False positive audit type (Manual = 1, Automated = 2)            
 -p        | -scanPreferenceId      | No        | Scan mode (Standard = 1, Express = 2)                            
 -I        | -pollingInterval       | No        | Interval between checking scan status in minutes                 
 -P        | -proxy                 | No        | Credentials for accessing the proxy                   
 -s        | -runSonatypeScan       | No        | Whether to run a Sonatype Scan (can be 'true' or 'false')        
 -h        | -help                  | No        | Print help dialog                                                
 -v        | -version               | No        | Print jar version   
 -x        | -excludeThirdPartyLibs | No        | Exclude Third Party Librarys from scan (can be 'true' or 'false')
 -r        | -isRemediationScan     | No        | Whether the scan is in remediation (can be 'true' or 'false')

*One of either apiCredentials or userCredentials is required.

### Migration to 5.3
If moving from the old version to this version, there are a few changes required:

Please be sure to adjust your arguments to fit the format described under **Current**.

