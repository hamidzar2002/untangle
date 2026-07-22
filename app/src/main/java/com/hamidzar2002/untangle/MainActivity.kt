package com.hamidzar2002.untangle

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.hamidzar2002.untangle.ads.AdsManager
import com.hamidzar2002.untangle.audio.GameSoundManager
import com.hamidzar2002.untangle.ui.theme.UntangleTheme
import com.hamidzar2002.untangle.view.UntangleRoute

class MainActivity : ComponentActivity() {
    private lateinit var adsManager: AdsManager
    private lateinit var gameSoundManager: GameSoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        adsManager = AdsManager(this)
        gameSoundManager = GameSoundManager(this)

        setContent {
            UntangleTheme {
                UntangleRoute(
                    activity = this,
                    adsManager = adsManager,
                    gameSoundManager = gameSoundManager
                )
            }
        }

        adsManager.gatherConsent(this)
    }

    override fun onDestroy() {
        gameSoundManager.release()
        super.onDestroy()
    }
}
