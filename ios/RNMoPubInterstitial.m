
#import "RNMoPubInterstitial.h"

@implementation RNMoPubInterstitial

RCT_EXPORT_MODULE();

- (NSArray<NSString *> *)supportedEvents {
    return @[
        @"onInterstitialInitSuccess",
        @"onInterstitialLoadSuccess",
        @"onInterstitialLoadFailure",
        @"onInterstitialAppear",
        @"onInterstitialClicked",
        @"onInterstitialDisappear"
    ];
}

RCT_EXPORT_METHOD(initialize: (nonnull NSString*)adUnitId) {
    MPMoPubConfiguration *sdkConfig = [[MPMoPubConfiguration alloc] initWithAdUnitIdForAppInitialization:adUnitId];
    sdkConfig.globalMediationSettings = @[];
    [[MoPub sharedInstance] initializeSdkWithConfiguration:sdkConfig completion:^{
        self.interstitial = [MPInterstitialAdController interstitialAdControllerForAdUnitId: adUnitId];
        self.interstitial.delegate = self;
        NSDictionary* body = @{
            @"canCollectPersonalInformation":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"shouldShowConsentDialog":@([MoPub sharedInstance].shouldShowConsentDialog),
            @"isGDPRApplicable":@([MoPub sharedInstance].isGDPRApplicable),
            @"adUnitId": adUnitId
        };
        [self sendEventWithName:@"onInterstitialInitSuccess" body:body];
    }];
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
