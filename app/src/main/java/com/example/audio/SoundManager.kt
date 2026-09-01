package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.exp
import kotlin.math.sin

class SoundManager {
    var isSoundEnabled: Boolean = true
    var isHapticEnabled: Boolean = true

    private val audioScope = CoroutineScope(Dispatchers.Default)
    private val sampleRate = 22050

    // Pentatonic scale frequencies for combo tones
    private val comboFrequencies = doubleArrayOf(
        261.63, // C4
        293.66, // D4
        329.63, // E4
        392.00, // G4
        440.00, // A4
        523.25, // C5
        587.33, // D5
        659.25, // E5
        783.99, // G5
        880.00, // A5
        1046.50 // C6
    )

    fun playTapSuccess(comboIndex: Int = 0) {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val freqIdx = (comboIndex).coerceIn(0, comboFrequencies.size - 1)
                val baseFreq = comboFrequencies[freqIdx]
                val durationMs = 180
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Fast attack, exponential decay bell curve
                    val env = exp(-8.0 * (t / (durationMs / 1000.0)))
                    // Tone with harmonic overtone
                    val wave = sin(2.0 * PI * baseFreq * t) + 0.3 * sin(4.0 * PI * baseFreq * t)
                    buffer[i] = (wave * env * 22000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playWhoosh() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val durationMs = 200
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)
                    // Sweeping pitch upward
                    val currentFreq = 300.0 + 800.0 * progress
                    val env = sin(PI * progress) // smooth arc
                    val wave = sin(2.0 * PI * currentFreq * t)
                    buffer[i] = (wave * env * 16000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playBlockedThud() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val durationMs = 120
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Descending low thud
                    val currentFreq = (140.0 - 60.0 * (t / 0.12)).coerceAtLeast(40.0)
                    val env = exp(-20.0 * t)
                    val wave = sin(2.0 * PI * currentFreq * t)
                    buffer[i] = (wave * env * 28000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playVictory() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                // Arpeggio: C5 -> E5 -> G5 -> C6
                val notes = doubleArrayOf(523.25, 659.25, 783.99, 1046.50)
                val noteDurationMs = 100
                val totalDurationMs = noteDurationMs * notes.size + 200
                val numSamples = (sampleRate * totalDurationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (n in notes.indices) {
                    val freq = notes[n]
                    val startSample = (n * noteDurationMs * sampleRate / 1000)
                    val endSample = ((n + 1) * noteDurationMs * sampleRate / 1000) + (if (n == notes.lastIndex) sampleRate * 200 / 1000 else 0)

                    for (i in startSample until endSample.coerceAtMost(numSamples)) {
                        val localT = (i - startSample).toDouble() / sampleRate
                        val env = exp(-4.0 * localT)
                        val wave = sin(2.0 * PI * freq * localT) + 0.4 * sin(4.0 * PI * freq * localT)
                        buffer[i] = (buffer[i] + (wave * env * 18000).toInt()).coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
                    }
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playPowerUp() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val durationMs = 250
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)
                    val currentFreq = 400.0 + 900.0 * progress
                    val shimmer = sin(2.0 * PI * 35.0 * t) // 35Hz vibrato
                    val env = exp(-2.5 * progress)
                    val wave = sin(2.0 * PI * (currentFreq + shimmer * 50.0) * t)
                    buffer[i] = (wave * env * 20000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playClick() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val durationMs = 40
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val env = exp(-50.0 * t)
                    val wave = sin(2.0 * PI * 880.0 * t)
                    buffer[i] = (wave * env * 18000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    fun playBombExplode() {
        if (!isSoundEnabled) return
        audioScope.launch {
            try {
                val durationMs = 300
                val numSamples = (sampleRate * durationMs / 1000)
                val buffer = ShortArray(numSamples)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val progress = t / (durationMs / 1000.0)
                    val currentFreq = (200.0 - 150.0 * progress).coerceAtLeast(30.0)
                    // Add noise
                    val noise = (Math.random() * 2.0 - 1.0) * 0.4
                    val env = exp(-7.0 * progress)
                    val wave = sin(2.0 * PI * currentFreq * t) + noise
                    buffer[i] = (wave * env * 24000).toInt().toShort()
                }

                playPcm(buffer)
            } catch (_: Exception) {}
        }
    }

    private fun playPcm(buffer: ShortArray) {
        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(buffer.size * 2)
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        audioTrack.write(buffer, 0, buffer.size)
        audioTrack.play()
        audioTrack.setNotificationMarkerPosition(buffer.size)
        audioTrack.setPlaybackPositionUpdateListener(object : AudioTrack.OnPlaybackPositionUpdateListener {
            override fun onMarkerReached(track: AudioTrack?) {
                track?.release()
            }
            override fun onPeriodicNotification(track: AudioTrack?) {}
        })
    }
}
