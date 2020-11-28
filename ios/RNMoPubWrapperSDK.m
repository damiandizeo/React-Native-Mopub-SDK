//
//  RNMoPubRewardedVideo.m
//  react-native-ad-lib
//
//  Created by Usama Azam on 28/03/2019.
//

#import "RNMoPubWrapperSDK.h"
#import "MPRewardedVideo.h"

@implementation RNMoPubWrapperSDK

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"onSDKInitSuccess",
        @"onRewardedVideoLoadSuccess",
        @"onRewardedVideoLoadFailure",
        @"onRewardedVideoDidExpire",
        @"onRewardedVideoAppear",
        @"onRewardedVideoPlaybackError",
        @"onRewardedVideoShouldReward",
        @"onRewardedVideoClicked",
        @"onRewardedVideoDisappear",
        
        @"onInterstitialInitSuccess",
        @"onInterstitialLoadSuccess",
        @"onInterstitialLoadFailure",
        @"onInterstitialAppear",
        @"onInterstitialClicked",
        @"onInterstitialDisappear"
    ];
}

// Rewarded videos methods
RCT_EXPORT_METHOD(initialize:(NSString *)interstitialAdUnitId andRewardedVideoAdUnitId:(NSString *)rewardedVideoAdUnitId) {
    MPMoPubConfiguration *sdkConfig = [[MPMoPubConfiguration alloc] initWithAdUnitIdForAppInitialization:interstitialAdUnitId];
    sdkConfig.globalMediationSettings = @[];
    [[MoPub sharedInstance] initializeSdkWithConfiguration:sdkConfig completion:^{
        [MPRewardedVideo setDelegate:self forAdUnitId:rewardedVideoAdUnitId];
        self.interstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId: interstitialAdUnitId];
        self.interstitial.delegate = self;
        NSDictionary* body = @{
            @"canCollectPersonalInformation":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"shouldShowConsentDialog":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"isGDPRApplicable":@([MoPub sharedInstance].isGDPRApplicable),
            @"interstitialAdUnitId": interstitialAdUnitId,
            @"rewardedVideoAdUnitId": rewardedVideoAdUnitId
        };
        [self sendEventWithName:@"onSDKInitSuccess" body:body];
    }];
}

RCT_EXPORT_METHOD(loadRewardedVideo:(NSString *)adUnitID) {
    [MPRewardedVideo loadRewardedVideoAdWithAdUnitID:adUnitID withMediationSettings:nil];
}

// Interstitials methods
RCT_EXPORT_METHOD(presentRewardedVideo:(NSString *)adUnitID) {
    UIViewController *vc = UIApplication.sharedApplication.delegate.window.rootViewController;
    while (vc.presentedViewController) {
        vc = vc.presentedViewController;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [MPRewardedVideo presentRewardedVideoAdForAdUnitID:adUnitID fromViewController:vc withReward:nil];
    });
}

RCT_EXPORT_METHOD(loadInterstitial:(NSString *)adUnitID) {
    [self.interstitial loadAd];
}

RCT_EXPORT_METHOD(presentInterstitial:(NSString *)adUnitID) {
    if( self.interstitial != nil && [self.interstitial ready] ) {
        UIViewController *vc = UIApplication.sharedApplication.delegate.window.rootViewController;
        while (vc.presentedViewController) {
            vc = vc.presentedViewController;
        }
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.interstitial showFromViewController:vc];
        });
    }
}

// Rewarded videos delegates
- (void)rewardedVideoAdDidLoadForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onRewardedVideoLoadSuccess" body:@{@"adUnitID": adUnitID}];
}

- (void)rewardedVideoAdDidFailToLoadForAdUnitID:(NSString *)adUnitID error:(NSError *)error {
    [self sendEventWithName:@"onRewardedVideoLoadFailure" body:@{@"adUnitID": adUnitID, @"error":error}];
}

- (void)rewardedVideoAdDidExpireForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onRewardedVideoDidExpire" body:@{@"adUnitID": adUnitID}];
}

- (void)rewardedVideoAdDidFailToPlayForAdUnitID:(NSString *)adUnitID error:(NSError *)error {
    [self sendEventWithName:@"onRewardedVideoPlaybackError" body:@{@"adUnitID": adUnitID, @"error": error.localizedDescription}];
}

- (void)rewardedVideoAdWillAppearForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onRewardedVideoAppear" body:@{@"adUnitID": adUnitID}];
}

- (void)rewardedVideoAdDidReceiveTapEventForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onRewardedVideoClicked" body:@{@"adUnitID": adUnitID}];
}

- (void)rewardedVideoAdShouldRewardForAdUnitID:(NSString *)adUnitID reward:(MPRewardedVideoReward *)reward {
    [self sendEventWithName:@"onRewardedVideoShouldReward" body:@{@"adUnitID": adUnitID}];
}

-(void)rewardedVideoAdDidDisappearForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onRewardedVideoDisappear" body:@{@"adUnitID": adUnitID}];
}

// Interstitials delegates
- (void)interstitialDidLoadAd:(MPInterstitialAdController *)interstitial {
    [self sendEventWithName:@"onInterstitialLoadSuccess" body:nil];
}

- (void)interstitialDidFailToLoadAd:(MPInterstitialAdController *)interstitial {
    [self sendEventWithName:@"onInterstitialLoadFailure" body:@{@"message": @"MoPub interstital failed to load"}];
}

- (void)interstitialWillAppear:(MPInterstitialAdController *)interstitial {
    [self sendEventWithName:@"onInterstitialAppear" body:nil];
}

- (void)interstitialDidReceiveTapEvent:(MPInterstitialAdController *)interstitial {
    [self sendEventWithName:@"onInterstitialClicked" body:nil];
}

- (void)interstitialDidDisappear:(MPInterstitialAdController *)interstitial {
    [self sendEventWithName:@"onInterstitialDisappear" body:nil];
}

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

@end
