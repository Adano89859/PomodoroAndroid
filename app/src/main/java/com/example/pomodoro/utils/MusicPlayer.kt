package com.example.pomodoro.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.pomodoro.data.model.SessionType

class MusicPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackId: String? = null

    fun playTrack(trackId: String, isEnabled: Boolean) {
        if (!isEnabled) {
            stop()
            return
        }

        // Si ya está sonando la misma pista, no hacer nada
        if (currentTrackId == trackId && mediaPlayer?.isPlaying == true) {
            return
        }

        stop()

        val track = MusicCatalog.getTrackById(trackId)

        track?.let {
            try {
                mediaPlayer = MediaPlayer.create(context, it.resourceId)?.apply {
                    isLooping = true
                    setVolume(0.4f, 0.4f) // Volumen moderado
                    start()
                    currentTrackId = trackId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun playMusicForSession(
        sessionType: SessionType,
        isEnabled: Boolean,
        workTrackId: String,
        shortBreakTrackId: String,
        longBreakTrackId: String
    ) {
        if (!isEnabled) {
            stop()
            return
        }

        val trackId = when (sessionType) {
            SessionType.WORK -> workTrackId
            SessionType.SHORT_BREAK -> shortBreakTrackId
            SessionType.LONG_BREAK -> longBreakTrackId
        }

        playTrack(trackId, true)
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
        currentTrackId = null
    }

    fun isPlaying(): Boolean {
        return mediaPlayer?.isPlaying ?: false
    }

    fun setVolume(volume: Float) {
        mediaPlayer?.setVolume(volume, volume)
    }
}