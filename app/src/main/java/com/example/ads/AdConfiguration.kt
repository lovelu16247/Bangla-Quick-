package com.example.ads

/**
 * Centralized ad configuration containing official placeholders for
 * Start.io and Advertica SDK credentials and IDs.
 */
object AdConfiguration {
  // Start.io credentials
  var START_IO_APP_ID: String = "START_IO_APP_ID_PLACEHOLDER"
  var START_IO_BANNER_ID: String = "START_IO_BANNER_ID_PLACEHOLDER"
  var START_IO_INTERSTITIAL_ID: String = "START_IO_INTERSTITIAL_ID_PLACEHOLDER"
  var START_IO_REWARDED_ID: String = "START_IO_REWARDED_ID_PLACEHOLDER"
  var START_IO_NATIVE_ID: String = "START_IO_NATIVE_ID_PLACEHOLDER"

  // Advertica credentials
  var ADVERTICA_APP_ID: String = "ADVERTICA_APP_ID_PLACEHOLDER"
  var ADVERTICA_BANNER_ID: String = "ADVERTICA_BANNER_ID_PLACEHOLDER"
  var ADVERTICA_INTERSTITIAL_ID: String = "ADVERTICA_INTERSTITIAL_ID_PLACEHOLDER"
  var ADVERTICA_REWARDED_ID: String = "ADVERTICA_REWARDED_ID_PLACEHOLDER"
  var ADVERTICA_NATIVE_ID: String = "ADVERTICA_NATIVE_ID_PLACEHOLDER"

  // Test Mode (Safe for development and review)
  var isTestMode: Boolean = true
}
