package com.example.pomodoro.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.example.pomodoro.data.model.SessionType

class MusicPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private val audioGenerator = AudioGenerator(context)
    private var currentSessionType: SessionType? = null

    fun playMusicForSession(sessionType: SessionType, isEnabled: Boolean) {
        if (!isEnabled) {
            stop()
            return
        }

        // Si ya está sonando la música correcta, no hacer nada
        if (currentSessionType == sessionType && mediaPlayer?.isPlaying == true) {
            return
        }

        stop()
        currentSessionType = sessionType

        val uri = when (sessionType) {
            SessionType.WORK -> audioGenerator.generateFocusMusic()
            SessionType.SHORT_BREAK, SessionType.LONG_BREAK -> audioGenerator.generateBreakMusic()
        }

        uri?.let {
            try {
                mediaPlayer = MediaPlayer().apply {
                    setDataSource(context, it)
                    isLooping = true
                    setVolume(0.3f, 0.3f) // Volumen bajo
                    prepare()
                    start()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun stop() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        currentSessionType = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }
}