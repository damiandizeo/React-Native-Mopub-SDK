import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNMoPubRewardedVideo } = NativeModules;

const emitter = new NativeEventEmitter(RNMoPubRewardedVideo);

module.exports = {
    initialize: (interstitialAdUnitId, rewardedVideoAdUnitId) => RNMoPubRewardedVideo.initialize(interstitialAdUnitId, rewardedVideoAdUnitId),
    loadRewardedVideo: (adUnitId: string) => RNMoPubRewardedVideo.loadRewardedVideo(adUnitId),
    presentRewardedVideo: (adUnitId: string) => RNMoPubRewardedVideo.presentRewardedVideo(adUnitId),
    addEventListener: (eventType: string, listener: Function)  => emitter.addListener(eventType, listener),
    removeAllListeners: (eventType: string) => emitter.removeAllListeners(eventType)
};