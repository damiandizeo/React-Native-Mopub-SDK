import {NativeModules, NativeEventEmitter} from 'react-native';
const { RNMoPubRewardedVideo } = NativeModules;


const emitter = new NativeEventEmitter(RNMoPubRewardedVideo);

module.exports = {
    initializeSdkForRewardedVideoAd: (adUnitId:string) => RNMoPubRewardedVideo.initialize(adUnitId),
    loadRewardedVideoAdWithAdUnitID: (adUnitId: string) => RNMoPubRewardedVideo.loadRewardedVideo(adUnitId),
    presentRewardedVideoAdForAdUnitID: (adUnitId: string) => RNMoPubRewardedVideo.userClickedToWatchAd(adUnitId),
    availableRewardsForAdUnitID: (adUnitId:string, promise:()=>{}) => RNMoPubRewardedVideo.availableRewardsForAdUnitID(adUnitId,promise),
    hasAdAvailableForAdUnitID:(adUnitId:string, promise:()=>{}) =>RNMoPubRewardedVideo.hasAdAvailableForAdUnitID(adUnitId,promise),
    addEventListener: (eventType: string, listener: Function)  => emitter.addListener(eventType, listener),
    removeAllListeners: (eventType: string) => emitter.removeAllListeners(eventType)
};