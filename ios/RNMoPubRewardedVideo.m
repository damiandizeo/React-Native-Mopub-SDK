//
//  RNMoPubRewardedVideo.m
//  react-native-ad-lib
//
//  Created by Usama Azam on 28/03/2019.
//

#import "RNMoPubRewardedVideo.h"
#import "MPRewardedVideo.h"
@implementation RNMoPubRewardedVideo

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"onRewardedVideoInitSuccess",
        @"onRewardedVideoLoadSuccess",
        @"onRewardedVideoLoadFailure",
        @"onRewardedVideoDidExpire",
        @"onRewardedVideoAppear",
        @"onRewardedVideoPlaybackError",
        @"onRewardedVideoShouldReward",
        @"onRewardedVideoClicked",
        @"onRewardedVideoDisappear"
    ];
}

RCT_EXPORT_METHOD(initialize: (nonnull NSString*) adUnitId) {
    NSDictionary * ironSourceConfig = @{@"applicationKey": @"dd3058a9"};
    
    MPMoPubConfiguration *sdkConfig = [[MPMoPubConfiguration alloc] initWithAdUnitIdForAppInitialization:adUnitId];
    sdkConfig.globalMediationSettings = @[];
    
    NSMutableDictionary * config = [@{@"IronSourceAdapterConfiguration" : ironSourceConfig} mutableCopy];

    sdkConfig.mediatedNetworkConfigurations = config;
    
    [[MoPub sharedInstance] initializeSdkWithConfiguration:sdkConfig completion:^{
        [MPRewardedVideo setDelegate:self forAdUnitId:adUnitId];
        NSDictionary* body = @{
            @"canCollectPersonalInformation":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"shouldShowConsentDialog":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"isGDPRApplicable":@([MoPub sharedInstance].isGDPRApplicable),
            @"adUnitId": adUnitId
        };
        [self sendEventWithName:@"onRewardedVideoInitSuccess" body:body];
    }];
}

RCT_EXPORT_METHOD(loadRewardedVideo:(NSString *)adUnitID) {
    [MPRewardedVideo loadRewardedVideoAdWithAdUnitID:adUnitID withMediationSettings:nil];
}

RCT_EXPORT_METHOD(presentRewardedVideo:(NSString *)adUnitID) {
    UIViewController *vc = UIApplication.sharedApplication.delegate.window.rootViewController;
    while (vc.presentedViewController) {
        vc = vc.presentedViewController;
    }
    dispatch_async(dispatch_get_main_queue(), ^{
        [MPRewardedVideo presentRewardedVideoAdForAdUnitID:adUnitID fromViewController:vc withReward:nil];
    });
}

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
    [self sendEventWithName:@"onRewardedVideoPlaybackError" body:@{@"adUnitID": adUnitID}];
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

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

@end
