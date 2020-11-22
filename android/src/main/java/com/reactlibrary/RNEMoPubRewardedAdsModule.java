
package com.reactlibrary;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mopub.common.MoPub;
import com.mopub.common.MoPubReward;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RNEMoPubRewardedAdsModule extends ReactContextBaseJavaModule implements MoPubRewardedVideoListener {


    private static final String EVENT_INIT = "onInit";
    private static final String EVENT_LOADED = "onLoaded";
    private static final String EVENT_FAILED = "onFailed";
    private static final String EVENT_DISAPEARED = "onDidDisappear";
    private static final String EVENT_EXPIRED = "onDidExpire";
    private static final String EVENT_SHOULD_REWARD = "onShouldReward";

    private final ReactApplicationContext reactContext;

    public RNEMoPubRewardedAdsModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNEMoPubRewardedAds";
    }


    @ReactMethod
    public void initialize(String adUnitID) {

        Context context = this.getCurrentActivity();
        MoPubRewardedVideoListener listener = this;

        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(adUnitID).build();
                MoPub.initializeSdk(context, sdkConfiguration, initSdkListener(adUnitID, listener));


            }
        };
        mainHandler.post(myRunnable);

    }

    private SdkInitializationListener initSdkListener(String adUnitID, MoPubRewardedVideoListener listener) {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                /* MoPub SDK initialized.*/
                WritableMap event = Arguments.createMap();
                PersonalInfoManager mPersonalInfoManager = MoPub.getPersonalInformationManager();
                event.putBoolean("canCollectPersonalInformation", mPersonalInfoManager.canCollectPersonalInformation());
                if (mPersonalInfoManager.gdprApplies() == null) {
                    event.putBoolean("gdprApplies", false); // This happens if the SDK is not properly initialized. We don't want it to crash the app though.
                } else {
                    event.putBoolean("gdprApplies", mPersonalInfoManager.gdprApplies());
                }
                event.putBoolean("shouldShowConsent", mPersonalInfoManager.shouldShowConsentDialog());
                event.putString("adUnitId", adUnitID);
                sendEvent(EVENT_INIT, event);
                MoPubRewardedVideos.setRewardedVideoListener(listener);


            }
        };
    }


    @ReactMethod
    public void loadRewardedVideo(String adUnitID) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.loadRewardedVideo(adUnitID);
            }
        });
    }

    @ReactMethod
    public void addTestDevice(String testDeviceID) {

    }

    @ReactMethod
    public void userClickedToWatchAd(String adUnitID) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.showRewardedVideo(adUnitID);
            }
        });

    }


    private void sendEvent(String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

    @Override
    public void onRewardedVideoLoadSuccess(@NonNull String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(EVENT_LOADED, event);
    }

    @Override
    public void onRewardedVideoLoadFailure(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(EVENT_FAILED, event);
    }

    @Override
    public void onRewardedVideoStarted(@NonNull String adUnitId) {

    }

    @Override
    public void onRewardedVideoPlaybackError(@NonNull String adUnitId, @NonNull MoPubErrorCode errorCode) {

    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {

    }

    @Override
    public void onRewardedVideoClosed(@NonNull String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(EVENT_DISAPEARED, event);

    }

    @Override
    public void onRewardedVideoCompleted(@NonNull Set<String> adUnitIds, @NonNull MoPubReward reward) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", String.valueOf(adUnitIds.toArray()[0]));
        sendEvent(EVENT_SHOULD_REWARD, event);
    }


}