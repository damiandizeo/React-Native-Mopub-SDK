#if __has_include(<React/RCTBridgeModule.h>)
#import <React/RCTBridgeModule.h>
#else
#import "RCTBridgeModule.h"
#endif

#import <React/RCTEventEmitter.h>

#import <Foundation/Foundation.h>
#import <mopub-ios-sdk/MoPub.h>

@interface RNMoPubWrapperSDK : RCTEventEmitter <RCTBridgeModule, MPRewardedVideoDelegate, MPInterstitialAdControllerDelegate>
@property (nonatomic, retain) MPInterstitialAdController *interstitial;
@end


