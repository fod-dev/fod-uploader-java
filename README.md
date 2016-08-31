# fod-uploader
Java Utility for uploading packages to FoD

## Compiling

The FoD-Uploader relies on Maven's Shade Plugin to compile all of its dependencies into a single jar.

Instead of using the package goal, use shade:shade

```
mvn clean install shade:shade
```
