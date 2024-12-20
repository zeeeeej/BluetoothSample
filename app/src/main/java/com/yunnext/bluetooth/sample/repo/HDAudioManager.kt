package com.yunnext.bluetooth.sample.repo

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import randomZhongGuoSe
import kotlin.random.Random

class HDAudioManager(context: Context, private val setting: Setting = Setting()) {
    private val context = context.applicationContext

    companion object {
        private const val SAMPLE_RATE_IN_HZ: Int = 44100
        private const val CHANNEL_CONFIG: Int = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT: Int = AudioFormat.ENCODING_PCM_16BIT
    }

    data class Setting(
        val sampleRateInHz: Int = SAMPLE_RATE_IN_HZ,
        val channelConfig: Int = CHANNEL_CONFIG,
        val audioFormat: Int = AUDIO_FORMAT,
    )

    private var audioRecord: AudioRecord? = null
    private var audioTrack: AudioTrack? = null
    private var start: Boolean = false
    private val bufferSize by lazy {
        AudioRecord.getMinBufferSize(
            setting.sampleRateInHz,
            setting.channelConfig,
            setting.audioFormat
        )
    }

    @SuppressLint("MissingPermission")
    fun start(callback: (Boolean) -> Unit) {
        try {
            val ar = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                setting.sampleRateInHz,
                setting.channelConfig,
                setting.audioFormat,
                bufferSize
            )

            val at = AudioTrack(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build(),
                AudioFormat.Builder()
                    .setSampleRate(setting.sampleRateInHz)
                    .setEncoding(setting.audioFormat)
                    //.setChannelMask(setting.channelConfig)
                    .build(),
                bufferSize,
                AudioTrack.MODE_STREAM,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            audioRecord = ar
            audioTrack = at
            ar.startRecording()
            at.play()
            start = true
            callback(true)
            val buff = ByteArray(bufferSize)
            while (start) {
                val read = ar.read(buff, 0, bufferSize)
                if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                    at.write(buff, 0, read)
                }
            }
            start = false
            callback(false)
        } catch (e: Exception) {
            e.printStackTrace()
            audioRecord?.stop()
            audioTrack?.stop()
            audioRecord?.release()
            audioTrack?.release()
            audioRecord = null
            audioTrack = null
            start = false
            callback(false)
        } finally {

        }
    }

    fun stop() {
        try {
            audioRecord?.stop()
            audioTrack?.stop()
            start = false
            audioRecord?.release()
            audioTrack?.release()
            audioRecord = null
            audioTrack = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun hDAudioManagerFlow(context: Context) = callbackFlow<Boolean> {
    val manager = HDAudioManager(context = context)
    Thread {
        manager.start {
            trySend(it)
        }
    }.start()

    awaitClose {
        manager.stop()
    }
}