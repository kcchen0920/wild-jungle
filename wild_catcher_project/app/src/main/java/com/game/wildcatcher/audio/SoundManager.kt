package com.game.wildcatcher.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.SoundPool
import android.util.Log
import kotlin.concurrent.thread
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

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

    @Volatile
    private var isMuted = false

    @Volatile
    private var isPaused = false

    @Volatile
    private var isReleased = false

    private var bgmThread: Thread? = null

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
        startBgm()
    }

    private fun loadSounds() {
        Log.d("SoundManager", "SoundPool initialized for tablet audio landscape")
    }

    fun startBgm() {
        if (bgmThread != null || isReleased) return
        bgmThread = thread(start = true, isDaemon = true, name = "WildCatcherBGM") {
            val sampleRate = 22050
            val stepDurationSec = 0.38
            val stepSamples = (sampleRate * stepDurationSec).toInt()
            val bufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            ).coerceAtLeast(stepSamples * 2)

            val track = try {
                AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STREAM)
                    .build()
            } catch (e: Exception) {
                Log.w("SoundManager", "AudioTrack init failed: ${e.message}")
                return@thread
            }

            // Cheerful, gentle C-major pentatonic jungle marimba melody (Hz)
            val melody = doubleArrayOf(
                523.25, 659.25, 783.99, 659.25,
                880.00, 783.99, 659.25, 587.33,
                523.25, 587.33, 659.25, 783.99,
                659.25, 587.33, 523.25, 0.0,
                783.99, 880.00, 1046.50, 880.00,
                783.99, 659.25, 587.33, 659.25,
                523.25, 659.25, 587.33, 440.00,
                523.25, 0.0, 392.00, 0.0
            )
            val bass = doubleArrayOf(
                261.63, 0.0, 392.00, 0.0,
                220.00, 0.0, 293.66, 0.0,
                261.63, 0.0, 329.63, 0.0,
                196.00, 0.0, 261.63, 0.0,
                261.63, 0.0, 392.00, 0.0,
                220.00, 0.0, 293.66, 0.0,
                174.61, 0.0, 196.00, 0.0,
                261.63, 0.0, 196.00, 0.0
            )

            val pcm = ShortArray(stepSamples)
            var step = 0
            try {
                track.play()
                while (!isReleased) {
                    if (isMuted || isPaused) {
                        pcm.fill(0)
                        track.write(pcm, 0, pcm.size)
                        Thread.sleep(120)
                        continue
                    }

                    val fNote = melody[step % melody.size]
                    val fBass = bass[step % bass.size]
                    for (i in 0 until stepSamples) {
                        val t = i.toDouble() / sampleRate
                        val attack = (t / 0.015).coerceAtMost(1.0)
                        var sample = 0.0
                        if (fNote > 0.0) {
                            val env = attack * exp(-t * 6.5)
                            sample += (sin(2.0 * PI * fNote * t) * 0.72 +
                                sin(2.0 * PI * (fNote * 2.0) * t) * 0.18) * env
                        }
                        if (fBass > 0.0) {
                            val bassEnv = attack * exp(-t * 4.5)
                            sample += sin(2.0 * PI * fBass * t) * 0.35 * bassEnv
                        }
                        // Soft, child-friendly volume
                        val clamped = (sample * 3400.0).toInt().coerceIn(-32767, 32767)
                        pcm[i] = clamped.toShort()
                    }
                    track.write(pcm, 0, pcm.size)
                    step = (step + 1) % melody.size
                }
            } catch (e: Exception) {
                Log.w("SoundManager", "BGM loop stopped: ${e.message}")
            } finally {
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        }
    }

    fun pauseBgm() {
        isPaused = true
    }

    fun resumeBgm() {
        isPaused = false
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun playSound(effect: SoundEffect, volume: Float = 1.0f) {
        if (isMuted) return
        val soundId = soundMap[effect]
        if (soundId != null && soundPool != null) {
            soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
        } else {
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
        isReleased = true
        bgmThread = null
        soundPool?.release()
        soundPool = null
    }
}
