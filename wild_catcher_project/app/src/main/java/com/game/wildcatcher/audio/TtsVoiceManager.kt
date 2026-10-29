package com.game.wildcatcher.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import java.util.Locale

class TtsVoiceManager(context: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.TAIWAN)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to traditional Chinese / default
                tts?.setLanguage(Locale.CHINESE)
            }
            tts?.setPitch(1.15f) // Slightly higher friendly pitch for Bobo assistant
            tts?.setSpeechRate(0.90f) // Slower pacing tailored for young children
            isInitialized = true
            Log.d("TtsVoiceManager", "TextToSpeech successfully initialized for Toddler Audio")
        } else {
            Log.w("TtsVoiceManager", "TextToSpeech initialization failed: status $status")
        }
    }

    fun speakAnimalGuide(animalName: String, bopomofo: String, funFact: String) {
        if (!isInitialized || tts == null) return
        val speechText = "這是 $animalName。$funFact"
        tts?.speak(speechText, TextToSpeech.QUEUE_FLUSH, null, "ANIMAL_GUIDE_${System.currentTimeMillis()}")
    }

    fun speakCustomText(text: String) {
        if (!isInitialized || tts == null) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "CUSTOM_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun release() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
