package com.hamidzar2002.untangle.ads

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdsManagerTest {
    @Test
    fun interstitialIsLimitedToEveryThirdCompletedLevelAfterLevelOne() {
        assertFalse(shouldShowLevelCompletionInterstitial(1))
        assertFalse(shouldShowLevelCompletionInterstitial(2))
        assertTrue(shouldShowLevelCompletionInterstitial(3))
        assertFalse(shouldShowLevelCompletionInterstitial(4))
        assertFalse(shouldShowLevelCompletionInterstitial(5))
        assertTrue(shouldShowLevelCompletionInterstitial(6))
    }
}
