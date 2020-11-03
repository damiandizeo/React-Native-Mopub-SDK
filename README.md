# react-native-mopub-sdk

## Getting started

`$ npm install react-native-mopub-sdk --save`

1. Then run the following command in termainal in the same location
```
cd ios && pod install
```

### Android
2. Add following permissions to your android AndroidManifest.xml.
```
<!-- Required permissions -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>

<!-- Optional permissions. Will pass Lat/Lon values when available. Choose either Coarse or Fine -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"/>
```
3. Declare the following activities in your <application> AndroidManifest.xml.
```
<!-- MoPub's consent dialog -->
<activity android:name="com.mopub.common.privacy.ConsentDialogActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

<!-- All ad formats -->
<activity android:name="com.mopub.common.MoPubBrowser" android:configChanges="keyboardHidden|orientation|screenSize"/>

<!-- Interstitials -->
<activity android:name="com.mopub.mobileads.MoPubActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
<activity android:name="com.mopub.mobileads.MraidActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

<!-- Rewarded Video and Rewarded Playables -->
<activity android:name="com.mopub.mobileads.RewardedMraidActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>
<activity android:name="com.mopub.mobileads.MraidVideoPlayerActivity" android:configChanges="keyboardHidden|orientation|screenSize"/>

<meta-data android:name="com.google.android.gms.version"
android:value="15.0.90" />
```
4. Add following lines to build.gradle `allprojects`➜  `repositories`
```java
maven { url 'https://jitpack.io' }
maven { url 'https://dl.bintray.com/ironsource-mobile/android-sdk'}
```