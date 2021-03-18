import { NativeModules, NativeEventEmitter } from 'react-native';
const { RNMoPubInterstitial } = NativeModules;

const emitter = new NativeEventEmitter(RNMoPubInterstitial);

module.exports = {
    initialize: (adUnitId:string) => RNMoPubInterstitial.initialize(adUnitId),
    loadInterstitial: (adUnitId: string) => RNMoPubInterstitial.loadInterstitial(adUnitId),
    presentInterstitial: (adUnitId: string) => RNMoPubInterstitial.presentInterstitial(adUnitId),
    addEventListener: (eventType: string, listener: Function)  => emitter.addListener(eventType, listener),
    removeAllListeners: (eventType: string) => emitter.removeAllListeners(eventType)
};