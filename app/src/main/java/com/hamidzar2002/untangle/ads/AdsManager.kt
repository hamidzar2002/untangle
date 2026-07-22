package com.hamidzar2002.untangle.ads

import android.app.Activity
import android.util.Log
import com.google.android.libraries.ads.mobile.sdk.MobileAds
import com.google.android.libraries.ads.mobile.sdk.common.AdLoadCallback
import com.google.android.libraries.ads.mobile.sdk.common.AdRequest
import com.google.android.libraries.ads.mobile.sdk.common.FullScreenContentError
import com.google.android.libraries.ads.mobile.sdk.common.LoadAdError
import com.google.android.libraries.ads.mobile.sdk.initialization.InitializationConfig
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAd
import com.google.android.libraries.ads.mobile.sdk.interstitial.InterstitialAdEventCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.hamidzar2002.untangle.BuildConfig
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class AdsUiState(
    val privacyOptionsRequired: Boolean = false
)

/**
 * Coordinates consent and SDK initialization outside the game controller.
 *
 * Ads are never initialized or requested until UMP reports that ads may be
 * requested. This keeps advertising concerns separate from the puzzle model.
 */
class AdsManager(activity: Activity) {
    private val consentInformation =
        UserMessagingPlatform.getConsentInformation(activity.applicationContext)
    private val initializationStarted = AtomicBoolean(false)
    private val _uiState = MutableStateFlow(AdsUiState())
    private var interstitialAd: InterstitialAd? = null
    private var interstitialLoadInProgress = false

    val uiState: StateFlow<AdsUiState> = _uiState.asStateFlow()

    fun gatherConsent(activity: Activity) {
        val parameters = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            activity,
            parameters,
            {
                refreshPrivacyOptionsState()
                initializeAdsIfAllowed(activity)

                UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity) { error ->
                    if (error != null) {
                        Log.w(TAG, "Consent form was not shown: ${error.message}")
                    }
                    refreshPrivacyOptionsState()
                    initializeAdsIfAllowed(activity)
                }
            },
            { error ->
                Log.w(TAG, "Consent information update failed: ${error.message}")
                refreshPrivacyOptionsState()
                // UMP can retain a valid decision from an earlier app session.
                initializeAdsIfAllowed(activity)
            }
        )
    }

    fun showPrivacyOptions(activity: Activity) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
            if (error != null) {
                Log.w(TAG, "Privacy options form was not shown: ${error.message}")
            }
            refreshPrivacyOptionsState()
            initializeAdsIfAllowed(activity)
        }
    }

    /**
     * Shows a preloaded ad only at a conservative level-completion interval.
     * The completion callback always runs, including when an ad is unavailable.
     */
    fun showLevelCompletionInterstitial(
        activity: Activity,
        completedLevel: Int,
        onComplete: () -> Unit
    ) {
        if (!shouldShowLevelCompletionInterstitial(completedLevel)) {
            onComplete()
            return
        }

        val ad = interstitialAd
        if (ad == null) {
            loadInterstitial()
            onComplete()
            return
        }

        interstitialAd = null
        var completionDelivered = false
        fun finish() {
            if (completionDelivered) return
            completionDelivered = true
            loadInterstitial()
            onComplete()
        }

        ad.adEventCallback = object : InterstitialAdEventCallback {
            override fun onAdDismissedFullScreenContent() = finish()

            override fun onAdFailedToShowFullScreenContent(
                fullScreenContentError: FullScreenContentError
            ) {
                Log.w(TAG, "Interstitial failed to show: $fullScreenContentError")
                finish()
            }
        }
        ad.show(activity)
    }

    private fun refreshPrivacyOptionsState() {
        val required =
            consentInformation.privacyOptionsRequirementStatus ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
        _uiState.update { it.copy(privacyOptionsRequired = required) }
    }

    private fun initializeAdsIfAllowed(activity: Activity) {
        if (!consentInformation.canRequestAds()) return
        if (!initializationStarted.compareAndSet(false, true)) return

        Thread {
            MobileAds.initialize(
                activity.applicationContext,
                InitializationConfig.Builder(BuildConfig.ADMOB_APP_ID).build()
            ) {
                activity.runOnUiThread(::loadInterstitial)
            }
        }.start()
    }

    private fun loadInterstitial() {
        if (interstitialAd != null || interstitialLoadInProgress) return
        if (!consentInformation.canRequestAds()) return

        interstitialLoadInProgress = true
        val request = AdRequest.Builder(BuildConfig.ADMOB_INTERSTITIAL_ID).build()
        InterstitialAd.load(
            request,
            object : AdLoadCallback<InterstitialAd> {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialLoadInProgress = false
                    interstitialAd = ad
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialLoadInProgress = false
                    Log.w(TAG, "Interstitial failed to load: $adError")
                }
            }
        )
    }

    private companion object {
        const val TAG = "UntangleAds"
    }
}

internal fun shouldShowLevelCompletionInterstitial(completedLevel: Int): Boolean =
    completedLevel > 1 && completedLevel % INTERSTITIAL_LEVEL_INTERVAL == 0

private const val INTERSTITIAL_LEVEL_INTERVAL = 3
