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
FodUpload-5.3.jar -e <id> -f <1|2> -u <url> -z <file> [-a <1|2>] -ac <key> <secret> | -uc <username> <password>
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

### Legacy
A legacy tag (-l) is also available if you want to access the old format. Simply append the legacy tag at the beginning of your list of arguments.

```
java -jar FodUpload.jar -l [-version] <username> <password> <bsiUrl> <zipLocation> <entitlementId> 
<entitlementFrequency> [<proxy> <proxyUsername> <proxyPassword> <ntWorkStation> <ntDomain>] 
[-pollingInterval:<interval>] [-scanPreferenceId:<id>] [-auditPreferenceId:<id>] [-runSonatypeScan:<run>] 
[-isRemediationScan:<bool>] [-excludethirdPartyLibs:<bool>]
```
All unnamed arguments above must be presented in the order seen above. The optional arguments can appear in any order. `<username> <password>` can be either an api key/secret or username/password, however if using an api key/secret then the `<username>` field must begin with "key-".

Legacy formatting accepts the same values as above for auditPreferenceId, runSonatypeScan, excludeThirdPartyLibs, isRemediationScan, scanPreferenceId and entitlementFrequency.
