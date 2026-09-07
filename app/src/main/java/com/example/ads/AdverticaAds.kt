package com.example.ads

import android.content.Context
import android.util.Log

/**
 * Advertica Ad Network Integration Layer.
 * Dedicated clean abstraction matching official placeholders for official Advertica SDK.
 */
class AdverticaAds(private val context: Context) {
  private val tag = "AdverticaAds"
  var isInitialized = false

  fun initialize(appId: String, testMode: Boolean = true) {
    Log.d(tag, "Advertica Ads initialized with appId: $appId (testMode: $testMode)")
    isInitialized = true
  }

  fun showBanner(onAdLoaded: () -> Unit = {}, onAdFailed: (String) -> Unit = {}) {
    if (!isInitialized) {
      onAdFailed("Advertica is not initialized")
      return
    }
    Log.d(tag, "Displaying Advertica banner with placement: ${AdConfiguration.ADVERTICA_BANNER_ID}")
    onAdLoaded()
  }

  fun showInterstitial(onAdClosed: () -> Unit = {}, onAdFailed: (String) -> Unit = {}) {
    if (!isInitialized) {
      onAdFailed("Advertica is not initialized")
      return
    }
    Log.d(tag, "Displaying Advertica interstitial ad")
    onAdClosed()
  }

  fun showRewardedVideo(onRewardGranted: (amount: Int) -> Unit, onAdClosed: () -> Unit, onAdFailed: (String) -> Unit) {
    if (!isInitialized) {
      onAdFailed("Advertica not initialized")
      return
    }
    Log.d(tag, "Displaying Advertica rewarded video ad")
    onRewardGranted(10)
    onAdClosed()
  }
}
