#import "RNEMoPubRewardedAds.h"

@implementation RNEMoPubRewardedAds {
}

@synthesize bridge = _bridge;

- (dispatch_queue_t)methodQueue {
    return dispatch_get_main_queue();
}

RCT_EXPORT_MODULE()

- (NSArray<NSString *> *)supportedEvents {
  return @[
           @"onInit",
           @"onLoaded",
           @"onFailed",
           @"onDidDisappear",
           @"onDidExpire",
           @"onShouldReward"
           ];
}
- (id)init {
  if (self = [super init]) {}
  return self;
}

+ (BOOL)requiresMainQueueSetup {
    return YES;
}

RCT_EXPORT_METHOD(initialize: (nonnull NSString*) adUnitId) {
  
  
  MPMoPubConfiguration *sdkConfig = [[MPMoPubConfiguration alloc] initWithAdUnitIdForAppInitialization:adUnitId];
  sdkConfig.globalMediationSettings = @[];
  
  [[MoPub sharedInstance] initializeSdkWithConfiguration:sdkConfig completion:^{
    
    [MPRewardedVideo setDelegate:self forAdUnitId:adUnitId];
    
    NSDictionary* body = @{
                           @"canCollectPersonalInformation":@([MoPub sharedInstance].shouldShowConsentDialog),
                           @"shouldShowConsentDialog":@([MoPub sharedInstance].shouldShowConsentDialog),
                           @"isGDPRApplicable":@([MoPub sharedInstance].isGDPRApplicable),
                           @"adUnitId": adUnitId
                           };
    [self sendEventWithName:@"onInit" body:body];
    NSLog(@"onInit %@", body);
  }];
}

RCT_EXPORT_METHOD(addTestDevice: (nonnull NSString *) testDeviceId) {}


RCT_EXPORT_METHOD(loadRewardedVideo: (nonnull NSString *) adUnitId) {
    NSLog(@"[MOPUB] loadRewardedVideo %@", adUnitId);
    [MPRewardedVideo loadRewardedVideoAdWithAdUnitID:adUnitId withMediationSettings:nil ];
}

RCT_EXPORT_METHOD(userClickedToWatchAd: (nonnull NSString *) adUnitId) {
    UIViewController *vc = [UIApplication sharedApplication].delegate.window.rootViewController;
  // we need to display the ad in the main thread
    dispatch_async(dispatch_get_main_queue(), ^{
        [MPRewardedVideo presentRewardedVideoAdForAdUnitID:adUnitId fromViewController:vc withReward:nil];
    });
}

#pragma mark - MPRewardedVideoDelegate

- (void)rewardedVideoAdDidLoadForAdUnitID:(NSString *)adUnitID {
    [self sendEventWithName:@"onLoaded" body:@{@"adUnitId" : adUnitID}];
}

- (void)rewardedVideoAdDidFailToLoadForAdUnitID:(NSString *)adUnitID error:(NSError *)error {
    [self sendEventWithName:@"onFailed" body:@{@"adUnitId": adUnitID}];
}

- (void)rewardedVideoAdShouldRewardForAdUnitID:(NSString *)adUnitID reward:(MPRewardedVideoReward *)reward {
    [self sendEventWithName:@"onShouldReward" body:@{@"adUnitId" : adUnitID}];
}

- (void)rewardedVideoAdDidExpireForAdUnitID:(NSString *)adUnitID {
  [self sendEventWithName:@"onDidExpire" body:@{@"adUnitId" : adUnitID}];
}

- (void)rewardedVideoAdDidDisappearForAdUnitID:(NSString *)adUnitID {
  [self sendEventWithName:@"onDidDisappear" body:@{@"adUnitId" : adUnitID}];
}

@end
  
