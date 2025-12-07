package com.example.pomodoro.utils

import com.example.pomodoro.R
import com.example.pomodoro.data.model.MusicTrack
import com.example.pomodoro.data.model.SessionType

object MusicCatalog {

    private val allTracks = listOf(
        // Música para TRABAJO (8 tracks)
        MusicTrack(
            id = "work_focus_1",
            name = "Focus 1",
            description = "Música de concentración #1",
            resourceId = R.raw.work_focus_1,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_2",
            name = "Focus 2",
            description = "Música de concentración #2",
            resourceId = R.raw.work_focus_2,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_3",
            name = "Focus 3",
            description = "Música de concentración #3",
            resourceId = R.raw.work_focus_3,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_4",
            name = "Focus 4",
            description = "Música de concentración #4",
            resourceId = R.raw.work_focus_4,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_5",
            name = "Focus 5",
            description = "Música de concentración #5",
            resourceId = R.raw.work_focus_5,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_6",
            name = "Focus 6",
            description = "Música de concentración #6",
            resourceId = R.raw.work_focus_6,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_7",
            name = "Focus 7",
            description = "Música de concentración #7",
            resourceId = R.raw.work_focus_7,
            sessionType = SessionType.WORK
        ),
        MusicTrack(
            id = "work_focus_8",
            name = "Focus 8",
            description = "Música de concentración #8",
            resourceId = R.raw.work_focus_8,
            sessionType = SessionType.WORK
        ),

        // Música para DESCANSO CORTO (6 tracks)
        MusicTrack(
            id = "break_short_chill_1",
            name = "Chill 1",
            description = "Música relajante #1",
            resourceId = R.raw.break_short_chill_1,
            sessionType = SessionType.SHORT_BREAK
        ),
        MusicTrack(
            id = "break_short_chill_2",
            name = "Chill 2",
            description = "Música relajante #2",
            resourceId = R.raw.break_short_chill_2,
            sessionType = SessionType.SHORT_BREAK
        ),
        MusicTrack(
            id = "break_short_chill_3",
            name = "Chill 3",
            description = "Música relajante #3",
            resourceId = R.raw.break_short_chill_3,
            sessionType = SessionType.SHORT_BREAK
        ),
        MusicTrack(
            id = "break_short_chill_4",
            name = "Chill 4",
            description = "Música relajante #4",
            resourceId = R.raw.break_short_chill_4,
            sessionType = SessionType.SHORT_BREAK
        ),
        MusicTrack(
            id = "break_short_chill_5",
            name = "Chill 5",
            description = "Música relajante #5",
            resourceId = R.raw.break_short_chill_5,
            sessionType = SessionType.SHORT_BREAK
        ),
        MusicTrack(
            id = "break_short_chill_6",
            name = "Chill 6",
            description = "Música relajante #6",
            resourceId = R.raw.break_short_chill_6,
            sessionType = SessionType.SHORT_BREAK
        ),

        // Música para DESCANSO LARGO (6 tracks)
        MusicTrack(
            id = "break_long_deep_1",
            name = "Deep Relax 1",
            description = "Relajación profunda #1",
            resourceId = R.raw.break_long_deep_1,
            sessionType = SessionType.LONG_BREAK
        ),
        MusicTrack(
            id = "break_long_deep_2",
            name = "Deep Relax 2",
            description = "Relajación profunda #2",
            resourceId = R.raw.break_long_deep_2,
            sessionType = SessionType.LONG_BREAK
        ),
        MusicTrack(
            id = "break_long_deep_3",
            name = "Deep Relax 3",
            description = "Relajación profunda #3",
            resourceId = R.raw.break_long_deep_3,
            sessionType = SessionType.LONG_BREAK
        ),
        MusicTrack(
            id = "break_long_deep_4",
            name = "Deep Relax 4",
            description = "Relajación profunda #4",
            resourceId = R.raw.break_long_deep_4,
            sessionType = SessionType.LONG_BREAK
        ),
        MusicTrack(
            id = "break_long_deep_5",
            name = "Deep Relax 5",
            description = "Relajación profunda #5",
            resourceId = R.raw.break_long_deep_5,
            sessionType = SessionType.LONG_BREAK
        ),
        MusicTrack(
            id = "break_long_deep_6",
            name = "Deep Relax 6",
            description = "Relajación profunda #6",
            resourceId = R.raw.break_long_deep_6,
            sessionType = SessionType.LONG_BREAK
        )
    )

    fun getTracksForSession(sessionType: SessionType): List<MusicTrack> {
        return allTracks.filter { it.sessionType == sessionType }
    }

    fun getTrackById(id: String): MusicTrack? {
        return allTracks.find { it.id == id }
    }

    fun getAllTracks(): List<MusicTrack> = allTracks
}