package com.hamidzar2002.untangle.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Lightweight, offline sound effects with a persistent mute preference. */
class GameSoundManager(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE
    )
    private val toneGenerator = runCatching {
        ToneGenerator(AudioManager.STREAM_MUSIC, TONE_VOLUME_PERCENT)
    }.getOrNull()
    private val _soundEnabled = MutableStateFlow(
        preferences.getBoolean(SOUND_ENABLED_KEY, true)
    )

    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    fun toggleSound() = setSoundEnabled(!_soundEnabled.value)

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        preferences.edit().putBoolean(SOUND_ENABLED_KEY, enabled).apply()
    }

    fun playMove() = play(ToneGenerator.TONE_PROP_BEEP, MOVE_DURATION_MS)

    fun playNodeFreed() = play(ToneGenerator.TONE_PROP_ACK, FREED_DURATION_MS)

    fun playSolved() = play(ToneGenerator.TONE_PROP_PROMPT, SOLVED_DURATION_MS)

    fun release() {
        toneGenerator?.release()
    }

    private fun play(tone: Int, durationMs: Int) {
        if (!_soundEnabled.value) return
        toneGenerator?.startTone(tone, durationMs)
    }

    private companion object {
        const val PREFERENCES_NAME = "untangle_audio"
        const val SOUND_ENABLED_KEY = "sound_enabled"
        const val TONE_VOLUME_PERCENT = 45
        const val MOVE_DURATION_MS = 35
        const val FREED_DURATION_MS = 90
        const val SOLVED_DURATION_MS = 220
    }
}
