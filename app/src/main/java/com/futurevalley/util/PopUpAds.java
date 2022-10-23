package com.futurevalley.util;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.CacheFlag;
import com.facebook.ads.InterstitialAdListener;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.ixidev.gdpr.GDPRChecker;

public class PopUpAds {
    public static void showInterstitialAds(Context context, int adapterPosition, RvOnClickListener clickListener) {
        if (Constant.isInterstitial) {
            Constant.AD_COUNT += 1;
            if (Constant.AD_COUNT == Constant.AD_COUNT_SHOW) {
                if (Constant.isAdMobInterstitial) {
                    GDPRChecker.Request request = GDPRChecker.getRequest();
                    AdRequest.Builder builder = new AdRequest.Builder();
                    if (request == GDPRChecker.Request.NON_PERSONALIZED) {
                        Bundle extras = new Bundle();
                        extras.putString("npa", "1");
                        builder.addNetworkExtrasBundle(AdMobAdapter.class, extras);
                    }
                    Constant.AD_COUNT = 0;
                    InterstitialAd.load(context, Constant.interstitialId, builder.build(), new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                            super.onAdLoaded(interstitialAd);
                            interstitialAd.show((Activity) context);
                            interstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override
                                public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                    super.onAdFailedToShowFullScreenContent(adError);
                                    clickListener.onItemClick(adapterPosition);
                                }

                                @Override
                                public void onAdDismissedFullScreenContent() {
                                    super.onAdDismissedFullScreenContent();
                                    clickListener.onItemClick(adapterPosition);
                                }
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                            super.onAdFailedToLoad(loadAdError);
                            clickListener.onItemClick(adapterPosition);
                        }
                    });
                } else {
                    Constant.AD_COUNT = 0;
                    com.facebook.ads.InterstitialAd interstitialAd = new com.facebook.ads.InterstitialAd(context, Constant.interstitialId);
                    InterstitialAdListener interstitialAdListener = new InterstitialAdListener() {
                        @Override
                        public void onInterstitialDisplayed(Ad ad) {

                        }

                        @Override
                        public void onInterstitialDismissed(Ad ad) {
                            clickListener.onItemClick(adapterPosition);
                        }

                        @Override
                        public void onError(Ad ad, AdError adError) {
                            clickListener.onItemClick(adapterPosition);
                        }

                        @Override
                        public void onAdLoaded(Ad ad) {
                            interstitialAd.show();
                        }

                        @Override
                        public void onAdClicked(Ad ad) {

                        }

                        @Override
                        public void onLoggingImpression(Ad ad) {

                        }
                    };
                    com.facebook.ads.InterstitialAd.InterstitialLoadAdConfig loadAdConfig = interstitialAd.buildLoadAdConfig().withAdListener(interstitialAdListener).withCacheFlags(CacheFlag.ALL).build();
                    interstitialAd.loadAd(loadAdConfig);
                }
            } else {
                clickListener.onItemClick(adapterPosition);
            }
        } else {
            clickListener.onItemClick(adapterPosition);
        }
    }
}
