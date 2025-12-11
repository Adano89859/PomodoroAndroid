package com.example.pomodoro.utils

import android.content.Context
import android.media.MediaPlayer
import com.example.pomodoro.data.model.SessionType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MusicPlayer(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackId: Int? = null
    private var previewJob: Job? = null  // ← NUEVO

    fun playTrack(trackId: Int, isEnabled: Boolean) {
        if (!isEnabled) {
            stop()
            return
        }

        if (currentTrackId == trackId && mediaPlayer?.isPlaying == true) {
            return
        }

        stop()

        val track = MusicCatalog.getTrackById(trackId)

        track?.let {
            try {
                mediaPlayer = MediaPlayer.create(context, it.resourceId)?.apply {
                    isLooping = true
                    setVolume(0.4f, 0.4f)
                    start()
                    currentTrackId = trackId
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ← NUEVO: Reproducir preview de 5 segundos
    fun playPreview(trackId: Int, onComplete: () -> Unit = {}) {
        stop()  // Detener cualquier reproducción actual

        val track = MusicCatalog.getTrackById(trackId)

        track?.let {
            try {
                mediaPlayer = MediaPlayer.create(context, it.resourceId)?.apply {
                    isLooping = false  // No loop para preview
                    setVolume(0.6f, 0.6f)  // Volumen un poco más alto para preview
                    start()
                    currentTrackId = trackId
                }

                // Auto-detener después de 5 segundos
                previewJob?.cancel()
                previewJob = CoroutineScope(Dispatchers.Main).launch {
                    delay(5000)
                    stop()
                    onComplete()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // ← NUEVO: Detener preview
    fun stopPreview() {
        previewJob?.cancel()
        stop()
    }

    fun playMusicForSession(
        sessionType: SessionType,
        isEnabled: Boolean,
        workTrackId: Int,
        shortBreakTrackId: Int,
        longBreakTrackId: Int
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
        previewJob?.cancel()  // ← NUEVO: Cancelar job de preview
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