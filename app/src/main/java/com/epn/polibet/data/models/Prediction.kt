package com.epn.polibet.data.models

data class Prediction(
    val id: String,
    val userId: String,
    val eventId: String,
    val predictionType: PredictionType,
    val selectedOption: String,
    val odds: Double,
    val amount: Double,
    val potentialWin: Double,
    val status: PredictionStatus = PredictionStatus.PENDING,
    val createdAt: Long = System.currentTimeMillis()
)

enum class PredictionType {
    MATCH_RESULT, // 1X2
    OVER_UNDER,
    HANDICAP,
    BOTH_TEAMS_SCORE
}

enum class PredictionStatus {
    PENDING, WON, LOST, CANCELLED, VOID
}

data class PredictionSummary(
    val totalPredictions: Int,
    val wonPredictions: Int,
    val lostPredictions: Int,
    val pendingPredictions: Int,
    val totalStaked: Double,
    val totalWon: Double,
    val winRate: Double
)
