# fod-uploader
Java Utility for uploading packages to FoD

## Setup

The FoD-Uploader is configured to build a fat jar with the Gradle Shadow plugin as the default gradle task.

To compile, simply use the gradlew or gradlew.bat depending on your operating system.

```
.\gradlew.bat
```

If you are behind the HPE firewall, you will need to configure gradles's proxy settings in:

*/\<user-directory>/.gradle/gradle.properties*

```
systemProp.http.proxyHost=web-proxy.atl.hp.com
systemProp.http.proxyPort=8080

systemProp.https.proxyHost=web-proxy.atl.hp.com
systemProp.https.proxyPort=8080

```
