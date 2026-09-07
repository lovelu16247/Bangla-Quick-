package com.example.ads

import android.content.Context
import com.example.data.model.AdSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AdsManager(private val context: Context) {

  val startIoAds = StartIoAds(context)
  val adverticaAds = AdverticaAds(context)

  private val _adSettings = MutableStateFlow(
    AdSettings(
      startIoEnabled = true,
      adverticaEnabled = true,
      bannerEnabled = true,
      interstitialEnabled = true,
      rewardedEnabled = true,
      nativeEnabled = true,
      adFrequency = 3,
      minIntervalSeconds = 45
    )
  )
  val adSettings: StateFlow<AdSettings> = _adSettings.asStateFlow()

  private var lastInterstitialShownTimestamp: Long = 0L
  private var quizCompletionCount: Int = 0

  private val _totalImpressions = MutableStateFlow(142)
  val totalImpressions: StateFlow<Int> = _totalImpressions.asStateFlow()

  private val _estimatedRevenueUsd = MutableStateFlow(18.45f)
  val estimatedRevenueUsd: StateFlow<Float> = _estimatedRevenueUsd.asStateFlow()

  // Track claimed reward callbacks to prevent multiple rewards from single callback
  private val processedRewardTokens = mutableSetOf<String>()

  init {
    startIoAds.initialize(AdConfiguration.START_IO_APP_ID, AdConfiguration.isTestMode)
    adverticaAds.initialize(AdConfiguration.ADVERTICA_APP_ID, AdConfiguration.isTestMode)
  }

  fun updateSettings(settings: AdSettings) {
    _adSettings.value = settings
  }

  /**
   * Evaluates smart interstitial ad placement after a quiz/exam completes.
   * Checks frequency and minimum time interval.
   */
  fun shouldShowInterstitial(): Boolean {
    val settings = _adSettings.value
    if (!settings.interstitialEnabled) return false
    if (!settings.startIoEnabled && !settings.adverticaEnabled) return false

    quizCompletionCount++
    if (quizCompletionCount % settings.adFrequency != 0) {
      return false
    }

    val now = System.currentTimeMillis()
    val elapsedSeconds = (now - lastInterstitialShownTimestamp) / 1000
    if (elapsedSeconds < settings.minIntervalSeconds) {
      return false
    }

    return true
  }

  fun showSmartInterstitial(onDismiss: () -> Unit) {
    if (!shouldShowInterstitial()) {
      onDismiss()
      return
    }

    lastInterstitialShownTimestamp = System.currentTimeMillis()
    _totalImpressions.value += 1
    _estimatedRevenueUsd.value += 0.025f

    // Pick active network based on admin settings
    val settings = _adSettings.value
    if (settings.startIoEnabled) {
      startIoAds.showInterstitial(onAdClosed = onDismiss, onAdFailed = { onDismiss() })
    } else if (settings.adverticaEnabled) {
      adverticaAds.showInterstitial(onAdClosed = onDismiss, onAdFailed = { onDismiss() })
    } else {
      onDismiss()
    }
  }

  /**
   * Shows a voluntary rewarded ad to give the user bonus points.
   * Requires anti-double-claim callback token.
   */
  fun showRewardedAd(
    callbackToken: String,
    onRewardGranted: (points: Int) -> Unit,
    onFailed: (String) -> Unit
  ) {
    val settings = _adSettings.value
    if (!settings.rewardedEnabled) {
      onFailed("রিওয়ার্ড বিজ্ঞাপন সাময়িকভাবে বন্ধ আছে।")
      return
    }

    if (processedRewardTokens.contains(callbackToken)) {
      onFailed("এই বিজ্ঞাপনটি ইতোমধ্যে গ্রহণ করা হয়েছে।")
      return
    }

    processedRewardTokens.add(callbackToken)
    _totalImpressions.value += 1
    _estimatedRevenueUsd.value += 0.05f

    val onCompleted: (Int) -> Unit = { pts ->
      onRewardGranted(pts)
    }

    if (settings.startIoEnabled) {
      startIoAds.showRewardedVideo(
        onRewardGranted = onCompleted,
        onAdClosed = {},
        onAdFailed = onFailed
      )
    } else if (settings.adverticaEnabled) {
      adverticaAds.showRewardedVideo(
        onRewardGranted = onCompleted,
        onAdClosed = {},
        onAdFailed = onFailed
      )
    } else {
      onFailed("কোন বিজ্ঞাপন নেটওয়ার্ক সক্রিয় নেই।")
    }
  }
}
