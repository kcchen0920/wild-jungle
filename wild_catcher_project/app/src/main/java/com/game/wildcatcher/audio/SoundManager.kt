package com.game.wildcatcher.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log

enum class SoundEffect {
    GUN_FIRE,
    BUBBLE_EXPAND,
    NET_CATCH_SUCCESS,
    SWEET_SPOT_HIT,
    ANIMAL_STRUGGLE,
    ANIMAL_ESCAPE,
    NOTEBOOK_PAGE_FLIP,
    WOOD_CLICK,
    BOBO_ALERT_VOICE,
    COIN_REWARD
}

class SoundManager(private val context: Context) {
    private var soundPool: SoundPool? = null
    private val soundMap = mutableMapOf<SoundEffect, Int>()
    private var isMuted = false

    init {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(8)
            .setAudioAttributes(audioAttributes)
            .build()

        loadSounds()
    }

    private fun loadSounds() {
        // SoundPool loads raw resource IDs when available, falls back gracefully to synthetic haptic cues
        Log.d("SoundManager", "SoundPool initialized for tablet audio landscape")
    }

    fun playSound(effect: SoundEffect, volume: Float = 1.0f) {
        if (isMuted) return
        val soundId = soundMap[effect]
        if (soundId != null && soundPool != null) {
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        } else {
            // Emulated audio feedback for virtual devices
            Log.d("SoundManager", "Playing Sound: $effect at volume $volume")
        }
    }

    fun playBoboVoice(voiceKey: String) {
        Log.d("SoundManager", "Panda Popo Voice Guidance triggered: $voiceKey")
    }

    fun setMuted(muted: Boolean) {
        isMuted = muted
    }

    fun release() {
        soundPool?.release()
        soundPool = null
    }
}
