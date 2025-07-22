package com.epn.polibet.data.models

data class Sport(
    val id: String,
    val name: String,
    val icon: String,
    val description: String,
    val activeEvents: Int = 0
)

data class Event(
    val id: String,
    val sportId: String,
    val homeTeam: String,
    val awayTeam: String,
    val homeTeamLogo: String = "",
    val awayTeamLogo: String = "",
    val startTime: Long,
    val league: String,
    val status: EventStatus = EventStatus.UPCOMING,
    val odds: EventOdds
)

data class EventOdds(
    val homeWin: Double,
    val draw: Double? = null, // Null para deportes sin empate
    val awayWin: Double,
    val overUnder: Map<String, Double> = emptyMap(),
    val handicap: Map<String, Double> = emptyMap()
)

enum class EventStatus {
    UPCOMING, LIVE, FINISHED, CANCELLED
}
