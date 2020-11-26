package com.reactlibrary;

import android.content.Context;
import android.os.Handler;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.common.SdkInitializationListener;
import com.mopub.common.privacy.PersonalInfoManager;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

public class RNMoPubInterstitial extends ReactContextBaseJavaModule implements MoPubInterstitial.InterstitialAdListener {

    public static final String ON_INTERSTITIAL_INIT_SUCCESS = "onInterstitialInitSuccess";
    public static final String ON_INTERSTITIAL_LOAD_SUCCESS = "onInterstitialLoadSuccess";
    public static final String ON_INTERSTITIAL_LOAD_FAILURE = "onInterstitialLoadFailure";
    public static final String ON_INTERSTITIAL_APPEAR = "onInterstitialLoadAppear";
    public static final String ON_INTERSTITIAL_CLICKED = "onInterstitialLoadClicked";
    public static final String ON_INTERSTITIAL_DISAPPEAR = "onInterstitialLoadDisappear";

    private final ReactApplicationContext reactContext;
    private MoPubInterstitial mInterstitial;

    public RNMoPubInterstitial(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "RNMoPubInterstitial";
    }

    @ReactMethod
    public void initialize(final String adUnitID) {
        final Context context = this.getCurrentActivity();
        mInterstitial = new MoPubInterstitial(getCurrentActivity(), adUnitID);
        final MoPubInterstitial.InterstitialAdListener listener = this;
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

    private SdkInitializationListener initSdkListener(final String adUnitID, final MoPubInterstitial.InterstitialAdListener listener) {
        final SdkInitializationListener sdkInitializationListener = new SdkInitializationListener() {
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
                sendEvent(ON_INTERSTITIAL_INIT_SUCCESS, event);
                mInterstitial.setInterstitialAdListener(listener);
            }
        };
        return sdkInitializationListener;
    }


    @ReactMethod
    public void loadInterstitial(final String adUnitID) {
        UiThreadUtil.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mInterstitial != null) {
                    mInterstitial.load();
                }
            }
        });
    }

    @ReactMethod
    public void presentInterstitial(final String adUnitID) {
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
