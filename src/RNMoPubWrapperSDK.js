import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNMoPubWrapperSDK } = NativeModules;

const emitter = new NativeEventEmitter(RNMoPubWrapperSDK);

module.exports = {
    initialize: (interstitialAdUnitId: string, rewardedVideoAdUnitId: string) => RNMoPubWrapperSDK.initialize(interstitialAdUnitId, rewardedVideoAdUnitId),
    loadRewardedVideo: (adUnitId: string) => RNMoPubWrapperSDK.loadRewardedVideo(adUnitId),
    presentRewardedVideo: (adUnitId: string) => RNMoPubWrapperSDK.presentRewardedVideo(adUnitId),
    loadInterstitial: (adUnitId: string) => RNMoPubWrapperSDK.loadInterstitial(adUnitId),
    presentInterstitial: (adUnitId: string) => RNMoPubWrapperSDK.presentInterstitial(adUnitId),
    addEventListener: (eventType: string, listener: Function)  => emitter.addListener(eventType, listener),
    removeListener: (eventType: string) => emitter.removeAllListeners(eventType)
};