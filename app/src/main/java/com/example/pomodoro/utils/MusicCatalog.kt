package com.example.pomodoro.utils

import com.example.pomodoro.R
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.SessionType

object MusicCatalog {

    // TRABAJO - 5 canciones (1 gratis, 4 de pago)
    private val workTrack1 = MusicTrack(1, "Focus 1", "Concentración profunda", R.raw.work_focus_1, SessionType.WORK, "🎯", 0)
    private val workTrack2 = MusicTrack(2, "Focus 2", "Concentración profunda", R.raw.work_focus_2, SessionType.WORK, "🎯", 75)
    private val workTrack3 = MusicTrack(3, "Focus 3", "Concentración profunda", R.raw.work_focus_3, SessionType.WORK, "🎯", 75)
    private val workTrack4 = MusicTrack(4, "Focus 4", "Concentración profunda", R.raw.work_focus_4, SessionType.WORK, "🎯", 75)
    private val workTrack5 = MusicTrack(5, "Focus 5", "Concentración profunda", R.raw.work_focus_5, SessionType.WORK, "🎯", 75)

    // DESCANSO CORTO - 5 canciones (1 gratis, 4 de pago)
    private val shortBreak1 = MusicTrack(11, "Chill 1", "Relax suave", R.raw.break_short_chill_1, SessionType.SHORT_BREAK, "☁️", 0)
    private val shortBreak2 = MusicTrack(12, "Chill 2", "Relax suave", R.raw.break_short_chill_2, SessionType.SHORT_BREAK, "☁️", 75)
    private val shortBreak3 = MusicTrack(13, "Chill 3", "Relax suave", R.raw.break_short_chill_3, SessionType.SHORT_BREAK, "☁️", 75)
    private val shortBreak4 = MusicTrack(14, "Chill 4", "Relax suave", R.raw.break_short_chill_4, SessionType.SHORT_BREAK, "☁️", 75)
    private val shortBreak5 = MusicTrack(15, "Chill 5", "Relax suave", R.raw.break_short_chill_5, SessionType.SHORT_BREAK, "☁️", 75)

    // DESCANSO LARGO - 5 canciones (1 gratis, 4 de pago)
    private val longBreak1 = MusicTrack(21, "Deep 1", "Descanso profundo", R.raw.break_long_deep_1, SessionType.LONG_BREAK, "😌", 0)
    private val longBreak2 = MusicTrack(22, "Deep 2", "Descanso profundo", R.raw.break_long_deep_2, SessionType.LONG_BREAK, "😌", 75)
    private val longBreak3 = MusicTrack(23, "Deep 3", "Descanso profundo", R.raw.break_long_deep_3, SessionType.LONG_BREAK, "😌", 75)
    private val longBreak4 = MusicTrack(24, "Deep 4", "Descanso profundo", R.raw.break_long_deep_4, SessionType.LONG_BREAK, "😌", 75)
    private val longBreak5 = MusicTrack(25, "Deep 5", "Descanso profundo", R.raw.break_long_deep_5, SessionType.LONG_BREAK, "😌", 75)

    val workTracks = listOf(workTrack1, workTrack2, workTrack3, workTrack4, workTrack5)
    val shortBreakTracks = listOf(shortBreak1, shortBreak2, shortBreak3, shortBreak4, shortBreak5)
    val longBreakTracks = listOf(longBreak1, longBreak2, longBreak3, longBreak4, longBreak5)

    val allTracks = workTracks + shortBreakTracks + longBreakTracks

    // IDs de canciones gratuitas (primera de cada categoría)
    val freeTracks = listOf(1, 11, 21)

    fun getTrackById(id: Int): MusicTrack? {
        return allTracks.find { it.id == id }
    }

    // Versión legacy con String (para compatibilidad temporal)
    fun getTrackById(id: String): MusicTrack? {
        return allTracks.find { it.id.toString() == id }
    }

    fun getTracksByType(sessionType: SessionType): List<MusicTrack> {
        return when (sessionType) {
            SessionType.WORK -> workTracks
            SessionType.SHORT_BREAK -> shortBreakTracks
            SessionType.LONG_BREAK -> longBreakTracks
        }
    }

    // ← NUEVO: Método para obtener track desde música importada
    fun getImportedTrackById(id: Int): MusicTrack? {
        // Este método será usado por el selector de música
        // Retorna un MusicTrack adaptado desde ImportedMusic
        return null // Implementación en el selector
    }
}