# MGRS App

[APK](https://github.com/ngageoint/mgrs-android/releases/latest/download/mgrs.apk)

The MGRS App provides a Military Grid Reference System map using the [mgrs-android](https://github.com/ngageoint/mgrs-android) library.

<img src="https://user-images.githubusercontent.com/11667761/173627380-548782ea-33a6-4789-aeb6-a8acccef221b.png" width="250">   <img src="https://user-images.githubusercontent.com/11667761/173628358-8c097be3-0892-4e52-9875-70d8821c0a0c.png" width="250">

<img src="https://user-images.githubusercontent.com/11667761/173628655-c508e8c8-5df9-4fad-939f-2418062e1656.png" width="250">   <img src="https://user-images.githubusercontent.com/11667761/173628678-9717ef39-7a01-48f2-8367-204787470200.png" width="250">

### Build ###

Build this repository using Android Studio and/or Gradle. Run the "assembleRelease" task on the app Gradle script.

#### Map Key ####

When building this project locally, a Google API key is required to see map tiles:
 * Maps SDK for Android: [Get API Key](https://developers.google.com/maps/documentation/android-sdk/signup)

On the [Google Cloud Platform Console](https://cloud.google.com/console/google/maps-apis/overview), configure "Maps SDK for Android" credentials (replacing the example fingerprints).

| Package name           | SHA-1 certificate fingerprint                               |
| ---------------------- | ----------------------------------------------------------- |
| mil.nga.mgrs.app       | 12:34:56:78:90:AB:CD:EF:12:34:56:78:90:AB:CD:EF:AA:BB:CC:DD |

In your `gradle.properties`, add your API Key (replacing the example keys)

```
RELEASE_MAPS_MGRS_API_KEY=AIzaSyBdVl-cTICSwYKrZ95SuvNw7dbMuDt1KG0
DEBUG_MAPS_API_KEY=AIzaSyBdVl-cTICSwYKrZ95SuvNw7dbMuDt1KG0
```
