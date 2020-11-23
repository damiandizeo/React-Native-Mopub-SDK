package com.reactlibrary;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

public class RNMoPubRewardedVideo extends ReactContextBaseJavaModule implements MoPubRewardedVideoListener {

    public static final String ON_REWARDED_VIDEO_INIT_SUCCESS = "onRewardedVideoInitSuccess";
    public static final String ON_REWARDED_VIDEO_LOAD_SUCCESS = "onRewardedVideoLoadSuccess";
    public static final String ON_REWARDED_VIDEO_LOAD_FAILURE = "onRewardedVideoLoadFailure";
    public static final String ON_REWARDED_VIDEO_APPEAR = "onRewardedVideoAppear";
    public static final String ON_REWARDED_VIDEO_PLAYBACK_ERROR = "onRewardedVideoPlaybackError";
    public static final String ON_REWARDED_VIDEO_SHOULD_REWARD = "onRewardedVideoShouldReward";
    public static final String ON_REWARDED_VIDEO_CLICKED = "onRewardedVideoClicked";
    public static final String ON_REWARDED_VIDEO_DISAPPEAR = "onRewardedVideoDisappear";

    private final ReactApplicationContext reactContext;

    public RNMoPubRewardedVideo(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNMoPubRewardedVideo";
    }

    @ReactMethod
    public void initialize(final String adUnitID) {
        final Context context = this.getCurrentActivity();
        final MoPubRewardedVideoListener listener = this;
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

    private SdkInitializationListener initSdkListener(final String adUnitID, final MoPubRewardedVideoListener listener) {
        return new SdkInitializationListener() {
            @Override
            public void onInitializationFinished() {
                /* MoPub SDK initialized.*/
                WritableMap event = Arguments.createMap();
                PersonalInfoManager mPersonalInfoManager = MoPub.getPersonalInformationManager();
                event.putBoolean("canCollectPersonalInformation", mPersonalInfoManager.canCollectPersonalInformation());
                if (mPersonalInfoManager.gdprApplies() == null) {
                    event.putBoolean("gdprApplies", false);
                } else {
                    event.putBoolean("gdprApplies", mPersonalInfoManager.gdprApplies());
                }
                event.putBoolean("shouldShowConsent", mPersonalInfoManager.shouldShowConsentDialog());
                event.putString("adUnitId", adUnitID);
                sendEvent(ON_REWARDED_VIDEO_INIT_SUCCESS, event);
                MoPubRewardedVideos.setRewardedVideoListener(listener);
            }
        };
    }


    @ReactMethod
    public void loadRewardedVideo(final String adUnitID) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.loadRewardedVideo(adUnitID);
            }
        });
    }

    @ReactMethod
    public void presentRewardedVideo(final String adUnitID) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.showRewardedVideo(adUnitID);
            }
        });

    }

    @Override
    public void onRewardedVideoLoadSuccess(String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_LOAD_SUCCESS, event);
    }

    @Override
    public void onRewardedVideoLoadFailure(String adUnitId, MoPubErrorCode errorCode) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_LOAD_FAILURE, event);
    }

    @Override
    public void onRewardedVideoStarted(String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_APPEAR, event);
    }

    @Override
    public void onRewardedVideoPlaybackError(String adUnitId, MoPubErrorCode errorCode) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_PLAYBACK_ERROR, event);
    }

    @Override
    public void onRewardedVideoClicked(@NonNull String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_CLICKED, event);
    }

    @Override
    public void onRewardedVideoCompleted(Set<String> adUnitIds, MoPubReward reward) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", String.valueOf(adUnitIds.toArray()[0]));
        sendEvent(ON_REWARDED_VIDEO_SHOULD_REWARD, event);
    }

    @Override
    public void onRewardedVideoClosed(String adUnitId) {
        WritableMap event = Arguments.createMap();
        event.putString("adUnitId", adUnitId);
        sendEvent(ON_REWARDED_VIDEO_DISAPPEAR, event);
    }

    private void sendEvent(final String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}
