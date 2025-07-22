package com.epn.polibet.data.repository

import com.epn.polibet.data.models.Event
import com.epn.polibet.data.models.EventOdds
import com.epn.polibet.data.models.EventStatus
import com.epn.polibet.data.models.Sport
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SportsRepository {

    suspend fun getSports(): List<Sport> {
        delay(500) // Simular llamada de red
        return listOf(
            Sport("football", "Fútbol", "⚽", "El deporte más popular del mundo", 7),
            Sport("basketball", "Baloncesto", "🏀", "Deporte de canasta", 7),
            Sport("tennis", "Tenis", "🎾", "Deporte de raqueta", 7),
            Sport("volleyball", "Voleibol", "🏐", "Deporte de red", 7),
            Sport("baseball", "Béisbol", "⚾", "Deporte americano", 7),
            Sport("boxing", "Boxeo", "🥊", "Deporte de combate", 7)
        )
    }

    suspend fun getEventsBySport(sportId: String): List<Event> {
        delay(500) // Simular llamada de red

        return when (sportId) {
            "football" -> getFootballEvents()
            "basketball" -> getBasketballEvents()
            "tennis" -> getTennisEvents()
            "volleyball" -> getVolleyballEvents()
            "baseball" -> getBaseballEvents()
            "boxing" -> getBoxingEvents()
            else -> emptyList()
        }
    }

    suspend fun getEventById(eventId: String): Event? {
        delay(300)
        return getAllEvents().find { it.id == eventId }
    }

    fun getLiveEvents(): Flow<List<Event>> = flow {
        while (true) {
            delay(5000) // Actualizar cada 5 segundos
            val liveEvents = getAllEvents().filter { it.status == EventStatus.LIVE }
            emit(liveEvents)
        }
    }

    private fun getFootballEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "fb_001",
                sportId = "football",
                homeTeam = "Barcelona SC",
                awayTeam = "Emelec",
                league = "Liga Pro Ecuador",
                startTime = currentTime + 3600000, // 1 hora
                odds = EventOdds(homeWin = 2.1, draw = 3.2, awayWin = 3.8)
            ),
            Event(
                id = "fb_002",
                sportId = "football",
                homeTeam = "Real Madrid",
                awayTeam = "FC Barcelona",
                league = "La Liga",
                startTime = currentTime + 7200000, // 2 horas
                odds = EventOdds(homeWin = 2.5, draw = 3.1, awayWin = 2.9)
            ),
            Event(
                id = "fb_003",
                sportId = "football",
                homeTeam = "Manchester United",
                awayTeam = "Liverpool",
                league = "Premier League",
                startTime = currentTime + 10800000, // 3 horas
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 3.2, draw = 3.0, awayWin = 2.3)
            ),
            Event(
                id = "fb_004",
                sportId = "football",
                homeTeam = "PSG",
                awayTeam = "Bayern Munich",
                league = "Champions League",
                startTime = currentTime + 14400000, // 4 horas
                odds = EventOdds(homeWin = 2.8, draw = 3.4, awayWin = 2.6)
            ),
            Event(
                id = "fb_005",
                sportId = "football",
                homeTeam = "Aucas",
                awayTeam = "Liga de Quito",
                league = "Liga Pro Ecuador",
                startTime = currentTime + 18000000, // 5 horas
                odds = EventOdds(homeWin = 2.2, draw = 3.1, awayWin = 3.5)
            ),
            Event(
                id = "fb_006",
                sportId = "football",
                homeTeam = "Chelsea",
                awayTeam = "Arsenal",
                league = "Premier League",
                startTime = currentTime + 21600000, // 6 horas
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 2.4, draw = 3.3, awayWin = 3.0)
            ),
            Event(
                id = "fb_007",
                sportId = "football",
                homeTeam = "Juventus",
                awayTeam = "AC Milan",
                league = "Serie A",
                startTime = currentTime + 25200000, // 7 horas
                odds = EventOdds(homeWin = 2.7, draw = 3.2, awayWin = 2.8)
            )
        )
    }

    private fun getBasketballEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "bb_001",
                sportId = "basketball",
                homeTeam = "Lakers",
                awayTeam = "Warriors",
                league = "NBA",
                startTime = currentTime + 5400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "bb_002",
                sportId = "basketball",
                homeTeam = "Celtics",
                awayTeam = "Heat",
                league = "NBA",
                startTime = currentTime + 9000000,
                odds = EventOdds(homeWin = 2.1, draw = null, awayWin = 1.7)
            ),
            Event(
                id = "bb_003",
                sportId = "basketball",
                homeTeam = "Nets",
                awayTeam = "76ers",
                league = "NBA",
                startTime = currentTime + 12600000,
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            ),
            Event(
                id = "bb_004",
                sportId = "basketball",
                homeTeam = "Bucks",
                awayTeam = "Bulls",
                league = "NBA",
                startTime = currentTime + 16200000,
                odds = EventOdds(homeWin = 1.6, draw = null, awayWin = 2.3)
            ),
            Event(
                id = "bb_005",
                sportId = "basketball",
                homeTeam = "Suns",
                awayTeam = "Clippers",
                league = "NBA",
                startTime = currentTime + 19800000,
                odds = EventOdds(homeWin = 2.2, draw = null, awayWin = 1.6)
            ),
            Event(
                id = "bb_006",
                sportId = "basketball",
                homeTeam = "Nuggets",
                awayTeam = "Mavericks",
                league = "NBA",
                startTime = currentTime + 23400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "bb_007",
                sportId = "basketball",
                homeTeam = "Thunder",
                awayTeam = "Rockets",
                league = "NBA",
                startTime = currentTime + 27000000,
                odds = EventOdds(homeWin = 1.7, draw = null, awayWin = 2.1)
            )
        )
    }

    private fun getTennisEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "tn_001",
                sportId = "tennis",
                homeTeam = "Novak Djokovic",
                awayTeam = "Rafael Nadal",
                league = "ATP Masters",
                startTime = currentTime + 1800000,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            ),
            Event(
                id = "tn_002",
                sportId = "tennis",
                homeTeam = "Carlos Alcaraz",
                awayTeam = "Daniil Medvedev",
                league = "ATP Masters",
                startTime = currentTime + 5400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "tn_003",
                sportId = "tennis",
                homeTeam = "Stefanos Tsitsipas",
                awayTeam = "Alexander Zverev",
                league = "ATP 500",
                startTime = currentTime + 9000000,
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 2.1, draw = null, awayWin = 1.7)
            ),
            Event(
                id = "tn_004",
                sportId = "tennis",
                homeTeam = "Iga Swiatek",
                awayTeam = "Aryna Sabalenka",
                league = "WTA 1000",
                startTime = currentTime + 12600000,
                odds = EventOdds(homeWin = 1.6, draw = null, awayWin = 2.3)
            ),
            Event(
                id = "tn_005",
                sportId = "tennis",
                homeTeam = "Coco Gauff",
                awayTeam = "Jessica Pegula",
                league = "WTA 500",
                startTime = currentTime + 16200000,
                odds = EventOdds(homeWin = 2.0, draw = null, awayWin = 1.8)
            ),
            Event(
                id = "tn_006",
                sportId = "tennis",
                homeTeam = "Jannik Sinner",
                awayTeam = "Holger Rune",
                league = "ATP Masters",
                startTime = currentTime + 19800000,
                odds = EventOdds(homeWin = 1.7, draw = null, awayWin = 2.1)
            ),
            Event(
                id = "tn_007",
                sportId = "tennis",
                homeTeam = "Elena Rybakina",
                awayTeam = "Ons Jabeur",
                league = "WTA 1000",
                startTime = currentTime + 23400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            )
        )
    }

    private fun getVolleyballEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "vb_001",
                sportId = "volleyball",
                homeTeam = "Equipo VLANS",
                awayTeam = "Equipo David",
                league = "Liga de Naciones",
                startTime = currentTime + 3600000,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            ),
            Event(
                id = "vb_002",
                sportId = "volleyball",
                homeTeam = "Polonia",
                awayTeam = "Francia",
                league = "Liga de Naciones",
                startTime = currentTime + 7200000,
                odds = EventOdds(homeWin = 2.1, draw = null, awayWin = 1.7)
            ),
            Event(
                id = "vb_003",
                sportId = "volleyball",
                homeTeam = "Estados Unidos",
                awayTeam = "Japón",
                league = "Liga de Naciones",
                startTime = currentTime + 10800000,
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 1.6, draw = null, awayWin = 2.3)
            ),
            Event(
                id = "vb_004",
                sportId = "volleyball",
                homeTeam = "Serbia",
                awayTeam = "Argentina",
                league = "Liga de Naciones",
                startTime = currentTime + 14400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "vb_005",
                sportId = "volleyball",
                homeTeam = "Turquía",
                awayTeam = "Holanda",
                league = "Liga de Naciones",
                startTime = currentTime + 18000000,
                odds = EventOdds(homeWin = 2.2, draw = null, awayWin = 1.6)
            ),
            Event(
                id = "vb_006",
                sportId = "volleyball",
                homeTeam = "Rusia",
                awayTeam = "Alemania",
                league = "Liga de Naciones",
                startTime = currentTime + 21600000,
                odds = EventOdds(homeWin = 1.7, draw = null, awayWin = 2.1)
            ),
            Event(
                id = "vb_007",
                sportId = "volleyball",
                homeTeam = "China",
                awayTeam = "Canadá",
                league = "Liga de Naciones",
                startTime = currentTime + 25200000,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            )
        )
    }

    private fun getBaseballEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "bs_001",
                sportId = "baseball",
                homeTeam = "Yankees",
                awayTeam = "Red Sox",
                league = "MLB",
                startTime = currentTime + 5400000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "bs_002",
                sportId = "baseball",
                homeTeam = "Dodgers",
                awayTeam = "Giants",
                league = "MLB",
                startTime = currentTime + 9000000,
                odds = EventOdds(homeWin = 1.7, draw = null, awayWin = 2.1)
            ),
            Event(
                id = "bs_003",
                sportId = "baseball",
                homeTeam = "Astros",
                awayTeam = "Rangers",
                league = "MLB",
                startTime = currentTime + 12600000,
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 2.0, draw = null, awayWin = 1.8)
            ),
            Event(
                id = "bs_004",
                sportId = "baseball",
                homeTeam = "Braves",
                awayTeam = "Mets",
                league = "MLB",
                startTime = currentTime + 16200000,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            ),
            Event(
                id = "bs_005",
                sportId = "baseball",
                homeTeam = "Cubs",
                awayTeam = "Cardinals",
                league = "MLB",
                startTime = currentTime + 19800000,
                odds = EventOdds(homeWin = 2.1, draw = null, awayWin = 1.7)
            ),
            Event(
                id = "bs_006",
                sportId = "baseball",
                homeTeam = "Padres",
                awayTeam = "Rockies",
                league = "MLB",
                startTime = currentTime + 23400000,
                odds = EventOdds(homeWin = 1.6, draw = null, awayWin = 2.3)
            ),
            Event(
                id = "bs_007",
                sportId = "baseball",
                homeTeam = "Mariners",
                awayTeam = "Angels",
                league = "MLB",
                startTime = currentTime + 27000000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            )
        )
    }

    private fun getBoxingEvents(): List<Event> {
        val currentTime = System.currentTimeMillis()
        return listOf(
            Event(
                id = "bx_001",
                sportId = "boxing",
                homeTeam = "Rocky Balboa",
                awayTeam = "Ivan Drago",
                league = "Peso Mediano",
                startTime = currentTime + 7200000,
                odds = EventOdds(homeWin = 1.6, draw = null, awayWin = 2.3)
            ),
            Event(
                id = "bx_002",
                sportId = "boxing",
                homeTeam = "Apollo Creed",
                awayTeam = "Fricxon Xavier",
                league = "Peso Pesado",
                startTime = currentTime + 14400000,
                odds = EventOdds(homeWin = 1.8, draw = null, awayWin = 2.0)
            ),
            Event(
                id = "bx_003",
                sportId = "boxing",
                homeTeam = "Spyder Rico",
                awayTeam = "Thunder",
                league = "Peso Ligero",
                startTime = currentTime + 21600000,
                status = EventStatus.LIVE,
                odds = EventOdds(homeWin = 2.1, draw = null, awayWin = 1.7)
            ),
            Event(
                id = "bx_004",
                sportId = "boxing",
                homeTeam = "Auron Vonhendrik",
                awayTeam = "Bandan",
                league = "Peso Welter",
                startTime = currentTime + 28800000,
                odds = EventOdds(homeWin = 1.9, draw = null, awayWin = 1.9)
            ),
            Event(
                id = "bx_005",
                sportId = "boxing",
                homeTeam = "Mauricio Lopez",
                awayTeam = "Richard Gallegos",
                league = "Peso Bantam",
                startTime = currentTime + 36000000,
                odds = EventOdds(homeWin = 1.5, draw = null, awayWin = 2.5)
            ),
            Event(
                id = "bx_006",
                sportId = "boxing",
                homeTeam = "Maria Taylor",
                awayTeam = "Jenny Sanchez",
                league = "Peso Ligero Femenino",
                startTime = currentTime + 43200000,
                odds = EventOdds(homeWin = 1.7, draw = null, awayWin = 2.1)
            ),
            Event(
                id = "bx_007",
                sportId = "boxing",
                homeTeam = "German Hernandez",
                awayTeam = "Julian Johnson",
                league = "Peso Semipesado",
                startTime = currentTime + 50400000,
                odds = EventOdds(homeWin = 2.0, draw = null, awayWin = 1.8)
            )
        )
    }

    private fun getAllEvents(): List<Event> {
        return getFootballEvents() + getBasketballEvents() + getTennisEvents() +
                getVolleyballEvents() + getBaseballEvents() + getBoxingEvents()
    }
}
