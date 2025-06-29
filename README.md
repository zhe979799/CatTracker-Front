# CatTracker Frontend

This Android application starts a small HTTP server for reporting cat data over the local network. Reports can also be submitted over Bluetooth. The app shows collected cat positions on an AMap map and supports geofences.

## Features
- Embedded HTTP server running on port 8080
- Incoming report log viewer
- Bluetooth data upload support
- AMap map display of cat locations
- Simple geofence example

## Build

```
./gradlew assembleDebug
```

Gradle may require Android SDK and NDK to be installed. Place your AMap API key in your `AndroidManifest.xml` as described in the AMap documentation.
