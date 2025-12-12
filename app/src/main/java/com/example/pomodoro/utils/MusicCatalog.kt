package com.example.pomodoro.utils

import com.example.pomodoro.R
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.SessionType

object MusicCatalog {

    // IDs de canciones gratuitas por defecto
    val freeTracks = listOf(1, 11, 21)

    // TRABAJO - IDs 1-10
    private val workTracks = listOf(
        MusicTrack(
            id = 1,
            name = "Cosmos",
            description = "Música espacial y contemplativa",
            emoji = "🌌",
            resourceId = R.raw.cosmos,
            sessionType = SessionType.WORK,
            price = 0
        ),
        MusicTrack(
            id = 2,
            name = "Electro Lofi",
            description = "Beats electrónicos relajantes",
            emoji = "🎹",
            resourceId = R.raw.electro_lofi,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 3,
            name = "Nostalgia",
            description = "Sonidos que evocan recuerdos",
            emoji = "🌅",
            resourceId = R.raw.nostalgia,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 4,
            name = "Piano Focus",
            description = "Piano para concentración",
            emoji = "🎹",
            resourceId = R.raw.piano_focus,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 5,
            name = "Jazz Suave",
            description = "Jazz relajado y productivo",
            emoji = "🎷",
            resourceId = R.raw.jazz_suave,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 6,
            name = "NYC Nocturno",
            description = "Ambiente urbano nocturno",
            emoji = "🌃",
            resourceId = R.raw.nyc_nocturno,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 7,
            name = "Campo Tranquilo",
            description = "Serenidad rural",
            emoji = "🌾",
            resourceId = R.raw.campo_tranquilo,
            sessionType = SessionType.WORK,
            price = 75
        ),
        MusicTrack(
            id = 8,
            name = "Con Amor",
            description = "Ritmo cálido y motivador",
            emoji = "💚",
            resourceId = R.raw.con_amor,
            sessionType = SessionType.WORK,
            price = 75
        )
    )

    // DESCANSO CORTO - IDs 11-20
    private val shortBreakTracks = listOf(
        MusicTrack(
            id = 11,
            name = "Lluvia Exterior",
            description = "Sonido de lluvia calmante",
            emoji = "🌧️",
            resourceId = R.raw.lluvia_exterior,
            sessionType = SessionType.SHORT_BREAK,
            price = 0
        ),
        MusicTrack(
            id = 12,
            name = "Tu Descanso",
            description = "Pausa merecida",
            emoji = "☁️",
            resourceId = R.raw.tu_descanso,
            sessionType = SessionType.SHORT_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 13,
            name = "Progreso",
            description = "Celebra tus logros",
            emoji = "📈",
            resourceId = R.raw.progreso,
            sessionType = SessionType.SHORT_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 14,
            name = "Guitarra Suave",
            description = "Melodías de guitarra",
            emoji = "🎸",
            resourceId = R.raw.guitarra_suave,
            sessionType = SessionType.SHORT_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 15,
            name = "Cumbre Fría",
            description = "Frescura de montaña",
            emoji = "🏔️",
            resourceId = R.raw.cumbre_fria,
            sessionType = SessionType.SHORT_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 16,
            name = "Primavera",
            description = "Renovación y energía",
            emoji = "🌸",
            resourceId = R.raw.primavera,
            sessionType = SessionType.SHORT_BREAK,
            price = 75
        )
    )

    // DESCANSO LARGO - IDs 21-30
    private val longBreakTracks = listOf(
        MusicTrack(
            id = 21,
            name = "Zen Japonés",
            description = "Paz oriental profunda",
            emoji = "🎋",
            resourceId = R.raw.zen_japones,
            sessionType = SessionType.LONG_BREAK,
            price = 0
        ),
        MusicTrack(
            id = 22,
            name = "Celebración",
            description = "Disfruta tu logro",
            emoji = "🎉",
            resourceId = R.raw.celebracion,
            sessionType = SessionType.LONG_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 23,
            name = "Fiesta",
            description = "Momento de alegría",
            emoji = "🎊",
            resourceId = R.raw.fiesta,
            sessionType = SessionType.LONG_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 24,
            name = "Paz Interior",
            description = "Tranquilidad absoluta",
            emoji = "🕊️",
            resourceId = R.raw.paz_interior,
            sessionType = SessionType.LONG_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 25,
            name = "Hielo Sereno",
            description = "Calma invernal",
            emoji = "❄️",
            resourceId = R.raw.hielo_sereno,
            sessionType = SessionType.LONG_BREAK,
            price = 75
        ),
        MusicTrack(
            id = 26,
            name = "Gran Final",
            description = "Cierre épico",
            emoji = "🎵",
            resourceId = R.raw.gran_final,
            sessionType = SessionType.LONG_BREAK,
            price = 75
        )
    )

    val allTracks = workTracks + shortBreakTracks + longBreakTracks

    fun getTrackById(id: Int): MusicTrack? = allTracks.find { it.id == id }

    fun getTracksByType(sessionType: SessionType): List<MusicTrack> {
        return when (sessionType) {
            SessionType.WORK -> workTracks
            SessionType.SHORT_BREAK -> shortBreakTracks
            SessionType.LONG_BREAK -> longBreakTracks
        }
    }
}