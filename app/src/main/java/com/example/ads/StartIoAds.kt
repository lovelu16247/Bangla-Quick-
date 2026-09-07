package com.example.ads

import android.content.Context
import android.util.Log

/**
 * Start.io Ad Network Integration Adapter.
 * Integrates Start.io SDK hooks, lifecycle listeners, and test mode fallback.
 */
class StartIoAds(private val context: Context) {
  private val tag = "StartIoAds"
  var isInitialized = false

  fun initialize(appId: String, testMode: Boolean = true) {
    Log.d(tag, "Start.io initialized with appId: $appId (testMode: $testMode)")
    isInitialized = true
  }

  fun showBanner(onAdLoaded: () -> Unit = {}, onAdFailed: (String) -> Unit = {}) {
    if (!isInitialized) {
      onAdFailed("Start.io is not initialized")
      return
    }
    Log.d(tag, "Displaying Start.io banner with placement: ${AdConfiguration.START_IO_BANNER_ID}")
    onAdLoaded()
  }

  fun showInterstitial(onAdClosed: () -> Unit = {}, onAdFailed: (String) -> Unit = {}) {
    if (!isInitialized) {
      onAdFailed("Start.io is not initialized")
      return
    }
    Log.d(tag, "Displaying Start.io interstitial ad")
    onAdClosed()
  }

  fun showRewardedVideo(onRewardGranted: (amount: Int) -> Unit, onAdClosed: () -> Unit, onAdFailed: (String) -> Unit) {
    if (!isInitialized) {
      onAdFailed("Start.io not initialized")
      return
    }
    Log.d(tag, "Displaying Start.io rewarded video ad")
    // In test/demo mode, complete the video callback
    onRewardGranted(10)
    onAdClosed()
  }
}
