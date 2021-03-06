package com.reactlibrary;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import com.mopub.mobileads.MoPubInterstitial;
import com.mopub.mobileads.MoPubRewardedVideoListener;
import com.mopub.mobileads.MoPubRewardedVideos;
import com.mopub.network.ImpressionListener;
import com.mopub.network.ImpressionsEmitter;
import com.mopub.network.ImpressionData;


import java.util.Set;

public class RNMoPubWrapperSDK extends ReactContextBaseJavaModule implements MoPubRewardedVideoListener, MoPubInterstitial.InterstitialAdListener {

    public static final String ON_SDK_INIT_SUCCESS = "onSDKInitSuccess";

    public static final String ON_REWARDED_VIDEO_LOAD_SUCCESS = "onRewardedVideoLoadSuccess";
    public static final String ON_REWARDED_VIDEO_LOAD_FAILURE = "onRewardedVideoLoadFailure";
    public static final String ON_REWARDED_VIDEO_APPEAR = "onRewardedVideoAppear";
    public static final String ON_REWARDED_VIDEO_PLAYBACK_ERROR = "onRewardedVideoPlaybackError";
    public static final String ON_REWARDED_VIDEO_SHOULD_REWARD = "onRewardedVideoShouldReward";
    public static final String ON_REWARDED_VIDEO_CLICKED = "onRewardedVideoClicked";
    public static final String ON_REWARDED_VIDEO_DISAPPEAR = "onRewardedVideoDisappear";

    public static final String ON_INTERSTITIAL_LOAD_SUCCESS = "onInterstitialLoadSuccess";
    public static final String ON_INTERSTITIAL_LOAD_FAILURE = "onInterstitialLoadFailure";
    public static final String ON_INTERSTITIAL_APPEAR = "onInterstitialAppear";
    public static final String ON_INTERSTITIAL_CLICKED = "onInterstitialClicked";
    public static final String ON_INTERSTITIAL_DISAPPEAR = "onInterstitialDisappear";

    public static final String ON_IMPRESSION_TRACKED = "onImpressionTracked";

    private final ReactApplicationContext reactContext;
    private MoPubInterstitial mInterstitial;
    private final MoPubInterstitial.InterstitialAdListener listenerInterstitial = this;
    private ImpressionListener mImpressionListener;

    public RNMoPubWrapperSDK(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNMoPubWrapperSDK";
    }

    @ReactMethod
    public void initialize(final String interstitialAdUnitId, final String rewardedVideoAdUnitId) {
        final Context context = this.getCurrentActivity();
        final MoPubRewardedVideoListener listenerRewardedVideo = this;
        Handler mainHandler = new Handler(context.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                SdkConfiguration sdkConfiguration = new SdkConfiguration.Builder(interstitialAdUnitId).build();
                MoPub.initializeSdk(context, sdkConfiguration, initSdkListener(interstitialAdUnitId, rewardedVideoAdUnitId, listenerRewardedVideo, listenerInterstitial));

                mImpressionListener = createImpressionsListener();
                ImpressionsEmitter.addListener(mImpressionListener);
            }
        };
        mainHandler.post(myRunnable);
    }

    private SdkInitializationListener initSdkListener(final String interstitialAdUnitId, final String rewardedVideoAdUnitId, final MoPubRewardedVideoListener listenerRewardedVideo, final MoPubInterstitial.InterstitialAdListener listenerInterstitial) {
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
                event.putString("interstitialAdUnitId", interstitialAdUnitId);
                event.putString("rewardedVideoAdUnitId", rewardedVideoAdUnitId);
                event.putString("version", "1.0.2");
                sendEvent(ON_SDK_INIT_SUCCESS, event);
                MoPubRewardedVideos.setRewardedVideoListener(listenerRewardedVideo);
            }
        };
    }

    private ImpressionListener createImpressionsListener() {
        return new ImpressionListener() {
            @Override
            public void onImpression(@NonNull final String adUnitId, @Nullable final ImpressionData impressionData) {
                WritableMap event = Arguments.createMap();
                event.putString("adUnitID", adUnitId);
                if( impressionData != null ) {
                    event.putString("network", impressionData.getNetworkName());
                }
                sendEvent(ON_IMPRESSION_TRACKED, event);
            }
        };
    }

    // Rewarded videos methods
    @ReactMethod
    public void loadRewardedVideo(final String rewardedVideoAdUnitId) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.loadRewardedVideo(rewardedVideoAdUnitId);
            }
        });
    }

    @ReactMethod
    public void presentRewardedVideo(final String rewardedVideoAdUnitId) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MoPubRewardedVideos.showRewardedVideo(rewardedVideoAdUnitId);
            }
        });

    }

    // Interstitials methods
    @ReactMethod
    public void loadInterstitial(final String interstitialAdUnitId) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mInterstitial = new MoPubInterstitial(getCurrentActivity(), interstitialAdUnitId);
                mInterstitial.setInterstitialAdListener(listenerInterstitial);
                mInterstitial.load();
            }
        });
    }

    @ReactMethod
    public void presentInterstitial(final String interstitialAdUnitId) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInterstitial != null && mInterstitial.isReady()) {
                    mInterstitial.show();
                }
            }
        });
    }

    @ReactMethod
    public void forceRefreshInterstitial() {
        if (mInterstitial != null) {
            mInterstitial.forceRefresh();
        }
    }

    // Rewarded videos delegates
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

    // Interstitials delegates
    @Override
    public void onInterstitialLoaded(MoPubInterstitial interstitial) {
        WritableMap event = Arguments.createMap();
        event.putString("error", null);
        sendEvent(ON_INTERSTITIAL_LOAD_SUCCESS, event);
    }

    @Override
    public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
        WritableMap event = Arguments.createMap();
        event.putString("error", errorCode.toString());
        sendEvent(ON_INTERSTITIAL_LOAD_FAILURE, event);
    }

    @Override
    public void onInterstitialShown(MoPubInterstitial interstitial) {
        WritableMap event = Arguments.createMap();
        event.putString("error", null);
        sendEvent(ON_INTERSTITIAL_APPEAR, event);
    }

    @Override
    public void onInterstitialClicked(MoPubInterstitial interstitial) {
        WritableMap event = Arguments.createMap();
        event.putString("error", null);
        sendEvent(ON_INTERSTITIAL_CLICKED, event);
    }

    @Override
    public void onInterstitialDismissed(MoPubInterstitial interstitial) {
        WritableMap event = Arguments.createMap();
        event.putString("error", null);
        sendEvent(ON_INTERSTITIAL_DISAPPEAR, event);
    }

    private void sendEvent(final String eventName, @Nullable WritableMap params) {
        getReactApplicationContext().getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
    }

}
