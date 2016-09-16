# fod-uploader
Java Utility for uploading packages to FoD

## Setup

The FoD-Uploader relies on Maven's Shade Plugin to compile all of its dependencies into a single jar.

Instead of using the package goal, use shade:shade

```
mvn clean install shade:shade
```

If you are behind the HPE firewall, you will need to configure maven's proxy settings in:

*\<user-directory>/.m2/settings.xml*

```xml
<settings>
  ...
  <proxies>
    <proxy>
      <id>http-proxy</id>
      <active>true</active>
      <protocol>http</protocol>
      <host>web-proxy.atl.hp.com</host>
      <port>8080</port>
      <username></username>
      <password></password>
      <nonProxyHosts>localhost,127.0.0.1</nonProxyHosts>
    </proxy>
    <proxy>
      <id>https-proxy</id>
      <active>true</active>
      <protocol>https</protocol>
      <host>web-proxy.atl.hp.com</host>
      <port>8080</port>
      <username></username>
      <password></password>
      <nonProxyHosts>localhost,127.0.0.1</nonProxyHosts>
    </proxy>
  </proxies>
  ...
</settings>
```
