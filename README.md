# MGRS Android

### Military Grid Reference System Android Lib ####

The MGRS Library was developed at the [National Geospatial-Intelligence Agency (NGA)](http://www.nga.mil/) in collaboration with [BIT Systems](https://www.caci.com/bit-systems/). The government has "unlimited rights" and is releasing this software to increase the impact of government investments by providing developers with the opportunity to take things in new directions. The software use, modification, and distribution rights are stipulated within the [MIT license](http://choosealicense.com/licenses/mit/).

### Pull Requests ###
If you'd like to contribute to this project, please make a pull request. We'll review the pull request and discuss the changes. All pull request contributions to this project will be released under the MIT license.

Software source code previously released under an open source license and then modified by NGA staff is considered a "joint work" (see 17 USC § 101); it is partially copyrighted, partially public domain, and as a whole is protected by the copyrights of the non-government authors and must be released according to the terms of the original open source license.

### About ###

[MGRS Android](http://ngageoint.github.io/mgrs-android/) is an Android library providing Military Grid Reference System functionality, a geocoordinate standard used by NATO militaries for locating points on Earth.  [MGRS App](https://github.com/ngageoint/mgrs-android/tree/master/app) is a map implementation utilizing this library.

### Usage ###

View the latest [Javadoc](http://ngageoint.github.io/mgrs-android/docs/api/)

#### Tile Provider ####

```java

// Context context = ...;
// GoogleMap map = ...;

// Tile size determined from display density
MGRSTileProvider tileProvider = MGRSTileProvider.create(context);

// Manually specify tile size
MGRSTileProvider tileProvider2 = MGRSTileProvider.create(512, 512);

// GZD only grid
MGRSTileProvider gzdTileProvider = MGRSTileProvider.createGZD(context);

// Specified grids
MGRSTileProvider customTileProvider = MGRSTileProvider.create(context,
        GridType.GZD, GridType.HUNDRED_KILOMETER);

map.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));

```

#### Tile Provider Options ####

```java

int x = 8;
int y = 12;
int zoom = 5;

// Manually get a tile or draw the tile bitmap
Tile tile = tileProvider.getTile(x, y, zoom);
Bitmap tileBitmap = tileProvider.drawTile(x, y, zoom);

double latitude = 63.98862388;
double longitude = 29.06755082;
LatLng latLng = new LatLng(latitude, longitude);

// MGRS Coordinates
MGRS mgrs = tileProvider.getMGRS(latLng);
String coordinate = tileProvider.getCoordinate(latLng);
String zoomCoordinate = tileProvider.getCoordinate(latLng, zoom);

String mgrsGZD = tileProvider.getCoordinate(latLng, GridType.GZD);
String mgrs100k = tileProvider.getCoordinate(latLng, GridType.HUNDRED_KILOMETER);
String mgrs10k = tileProvider.getCoordinate(latLng, GridType.TEN_KILOMETER);
String mgrs1k = tileProvider.getCoordinate(latLng, GridType.KILOMETER);
String mgrs100m = tileProvider.getCoordinate(latLng, GridType.HUNDRED_METER);
String mgrs10m = tileProvider.getCoordinate(latLng, GridType.TEN_METER);
String mgrs1m = tileProvider.getCoordinate(latLng, GridType.METER);

```

#### Custom Grids ####

```java

Grids grids = Grids.create();

grids.setColor(GridType.GZD, Color.red());
grids.setWidth(GridType.GZD, 5.0);

grids.setLabelMinZoom(GridType.GZD, 3);
grids.setLabelMaxZoom(GridType.GZD, 8);
grids.setLabelTextSize(GridType.GZD, 32.0);

grids.setMinZoom(GridType.HUNDRED_KILOMETER, 4);
grids.setMaxZoom(GridType.HUNDRED_KILOMETER, 8);
grids.setColor(GridType.HUNDRED_KILOMETER, Color.blue());

grids.setLabelColor(GridType.HUNDRED_KILOMETER, Color.orange());
grids.setLabelBuffer(GridType.HUNDRED_KILOMETER, 0.1);
grids.getLabelPaint(GridType.HUNDRED_KILOMETER).setTypeface(Typeface.DEFAULT_BOLD);

grids.setColor(Color.darkGray(), GridType.TEN_KILOMETER, GridType.KILOMETER,
        GridType.HUNDRED_METER, GridType.TEN_METER);

grids.disable(GridType.METER);

grids.enableLabeler(GridType.TEN_KILOMETER);

MGRSTileProvider tileProvider = MGRSTileProvider.create(context, grids);

```

### Installation ###

Pull from the [Maven Central Repository](http://search.maven.org/#artifactdetails|mil.nga.mgrs|mgrs-android|2.2.1|aar) (AAR, POM, Source, Javadoc)

    api 'mil.nga.mgrs:mgrs-android:2.2.1'

### Build ###

[![Build Artifacts](https://github.com/ngageoint/mgrs-android/workflows/Build%20Artifacts/badge.svg)](https://github.com/ngageoint/mgrs-android/actions/workflows/build-artifacts.yml)
[![Test](https://github.com/ngageoint/mgrs-android/workflows/Test/badge.svg)](https://github.com/ngageoint/mgrs-android/actions/workflows/test.yml)

Build this repository using Android Studio and/or Gradle.

#### Project Setup ####

Include as repositories in your project build.gradle:

    repositories {
        google()
        mavenCentral()
        mavenLocal()
    }

##### Normal Build #####

Include the dependency in your module build.gradle with desired version number:

    api 'mil.nga.mgrs:mgrs-android:2.2.1'

As part of the build process, run the "publishToMavenLocal" task on the mgrs-android Gradle script to update the Maven local repository.

##### Local Build #####

Replace the normal build dependency in your module build.gradle with:

    api project(':mgrs')

Include in your settings.gradle:

    include ':mgrs'

From your project directory, link the cloned SDK directory:

    ln -s ../mgrs-android/mgrs mgrs

### Remote Dependencies ###

* [MGRS Java](https://github.com/ngageoint/mgrs-java) (The MIT License (MIT)) - MGRS Library

### MGRS App ###

[APK](https://github.com/ngageoint/mgrs-android/releases/latest/download/mgrs.apk)

The [MGRS App](https://github.com/ngageoint/mgrs-android/tree/master/app) provides a Military Grid Reference System map using this library.
