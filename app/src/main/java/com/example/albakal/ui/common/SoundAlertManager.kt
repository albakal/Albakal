package com.example.albakal.ui.common

import android.media.AudioManager
import android.media.ToneGenerator
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

object SoundAlertManager {
    private var toneGenerator: ToneGenerator? = null
    private var alertJob: Job? = null
    private var isRinging = false

    @Synchronized
    fun startRepeatingOrderAlert(scope: CoroutineScope) {
        if (isRinging) return
        isRinging = true

        alertJob = scope.launch(Dispatchers.IO) {
            try {
                if (toneGenerator == null) {
                    toneGenerator = ToneGenerator(AudioManager.STREAM_ALARM, 90)
                }
                while (isActive && isRinging) {
                    toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
                    delay(800)
                }
            } catch (e: Exception) {
                Log.e("SoundAlertManager", "Error playing sound: ${e.message}")
            }
        }
    }

    @Synchronized
    fun stopOrderAlert() {
        isRinging = false
        alertJob?.cancel()
        alertJob = null
        try {
            toneGenerator?.stopTone()
            toneGenerator?.release()
            toneGenerator = null
        } catch (e: Exception) {
            Log.e("SoundAlertManager", "Error stopping sound: ${e.message}")
        }
    }
}
