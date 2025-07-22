package com.epn.polibet.data.repository

import com.epn.polibet.data.models.Prediction
import com.epn.polibet.data.models.PredictionStatus
import com.epn.polibet.data.models.PredictionSummary
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PredictionsRepository {
    // Estado compartido para todas las apuestas de la sesión
    private val _predictions = MutableStateFlow<List<Prediction>>(emptyList())
    val predictions: StateFlow<List<Prediction>> = _predictions.asStateFlow()

    // Mapa persistente para organizar apuestas por usuario
    private val userPredictionsMap = mutableMapOf<String, MutableList<Prediction>>()

    init {
        // Cargar datos de prueba para demostración
        loadSampleData()
    }

    private fun loadSampleData() {
        // Datos de muestra para el usuario demo
        val samplePredictions = listOf(
            Prediction(
                id = "pred_sample_1",
                userId = "user_demo1",
                eventId = "fb_001",
                predictionType = com.epn.polibet.data.models.PredictionType.MATCH_RESULT,
                selectedOption = "Victoria Barcelona SC",
                odds = 2.1,
                amount = 50.0,
                potentialWin = 105.0,
                status = PredictionStatus.PENDING,
                createdAt = System.currentTimeMillis() - 3600000 // 1 hora atrás
            ),
            Prediction(
                id = "pred_sample_2",
                userId = "user_demo1",
                eventId = "fb_002",
                predictionType = com.epn.polibet.data.models.PredictionType.MATCH_RESULT,
                selectedOption = "Victoria Real Madrid",
                odds = 2.5,
                amount = 25.0,
                potentialWin = 62.5,
                status = PredictionStatus.WON,
                createdAt = System.currentTimeMillis() - 7200000 // 2 horas atrás
            )
        )

        // Agregar datos de muestra al usuario demo
        userPredictionsMap["user_demo1"] = samplePredictions.toMutableList()

        // Actualizar el estado global
        updateGlobalState()
    }

    private fun updateGlobalState() {
        val allPredictions = userPredictionsMap.values.flatten()
        _predictions.value = allPredictions
    }

    suspend fun createPrediction(
        prediction: Prediction,
        authRepository: AuthRepository
    ): Result<Prediction> {
        delay(500) // Simular llamada de red

        try {
            // Verificar fondos suficientes
            if (!authRepository.hasEnoughBalance(prediction.userId, prediction.amount)) {
                val currentBalance = authRepository.getCurrentBalance(prediction.userId)
                return Result.failure(Exception("Fondos insuficientes. Balance actual: $${String.format("%.2f", currentBalance)}"))
            }

            // Debitar el dinero de la apuesta
            val debitResult = authRepository.debitBalance(prediction.userId, prediction.amount)
            if (debitResult.isFailure) {
                return Result.failure(debitResult.exceptionOrNull() ?: Exception("Error al debitar fondos"))
            }

            val newPrediction = prediction.copy(
                id = "pred_${System.currentTimeMillis()}",
                potentialWin = prediction.amount * prediction.odds,
                createdAt = System.currentTimeMillis()
            )

            // Agregar a la lista del usuario específico
            val userPredictions = userPredictionsMap.getOrPut(prediction.userId) { mutableListOf() }
            userPredictions.add(newPrediction)

            // Actualizar el estado global
            updateGlobalState()

            // Simular resolución de apuesta después de un tiempo (para demo)
            simulateBetResolution(newPrediction, authRepository)

            return Result.success(newPrediction)
        } catch (e: Exception) {
            return Result.failure(e)
        }
    }

    // Simular resolución de apuestas para hacer la demo más realista
    private suspend fun simulateBetResolution(prediction: Prediction, authRepository: AuthRepository) {
        kotlinx.coroutines.GlobalScope.launch {
            // Esperar entre 30 segundos y 2 minutos para resolver la apuesta
            val delayTime = (30000..120000).random().toLong()
            delay(delayTime)

            // 60% de probabilidad de ganar
            val isWinner = (1..100).random() <= 60

            val userPredictions = userPredictionsMap[prediction.userId]
            val predictionIndex = userPredictions?.indexOfFirst { it.id == prediction.id }

            if (predictionIndex != null && predictionIndex >= 0 && userPredictions != null) {
                val updatedPrediction = if (isWinner) {
                    // Acreditar la ganancia
                    authRepository.creditBalance(prediction.userId, prediction.potentialWin)
                    prediction.copy(status = PredictionStatus.WON)
                } else {
                    prediction.copy(status = PredictionStatus.LOST)
                }

                userPredictions[predictionIndex] = updatedPrediction
                updateGlobalState()
            }
        }
    }

    suspend fun getUserPredictions(userId: String): List<Prediction> {
        delay(300)
        return userPredictionsMap[userId]?.toList() ?: emptyList()
    }

    suspend fun getPredictionSummary(userId: String): PredictionSummary {
        delay(300)
        val userPreds = userPredictionsMap[userId] ?: emptyList()

        val total = userPreds.size
        val won = userPreds.count { it.status == PredictionStatus.WON }
        val lost = userPreds.count { it.status == PredictionStatus.LOST }
        val pending = userPreds.count { it.status == PredictionStatus.PENDING }

        val totalStaked = userPreds.sumOf { it.amount }
        val totalWon = userPreds.filter { it.status == PredictionStatus.WON }
            .sumOf { it.potentialWin }

        val winRate = if (total > 0) (won.toDouble() / total) * 100 else 0.0

        return PredictionSummary(
            totalPredictions = total,
            wonPredictions = won,
            lostPredictions = lost,
            pendingPredictions = pending,
            totalStaked = totalStaked,
            totalWon = totalWon,
            winRate = winRate
        )
    }

    suspend fun cancelPrediction(predictionId: String, authRepository: AuthRepository): Result<Unit> {
        delay(300)

        // Buscar en todas las listas de usuarios
        for ((userId, predictions) in userPredictionsMap) {
            val index = predictions.indexOfFirst { it.id == predictionId }
            if (index != -1) {
                val prediction = predictions[index]

                // Solo se puede cancelar si está pendiente
                if (prediction.status != PredictionStatus.PENDING) {
                    return Result.failure(Exception("Solo se pueden cancelar apuestas pendientes"))
                }

                // Reembolsar el dinero
                authRepository.creditBalance(userId, prediction.amount)

                predictions[index] = prediction.copy(status = PredictionStatus.CANCELLED)

                // Actualizar el estado global
                updateGlobalState()

                return Result.success(Unit)
            }
        }
        return Result.failure(Exception("Pronóstico no encontrado"))
    }

    // Método para obtener el conteo de apuestas pendientes de un usuario
    fun getPendingPredictionsCount(userId: String): Int {
        return userPredictionsMap[userId]?.count { it.status == PredictionStatus.PENDING } ?: 0
    }

    // Método para obtener todas las apuestas de un usuario (sin delay para uso interno)
    fun getUserPredictionsSync(userId: String): List<Prediction> {
        return userPredictionsMap[userId]?.toList() ?: emptyList()
    }

    // Método para limpiar datos de sesión (opcional)
    fun clearSession() {
        userPredictionsMap.clear()
        _predictions.value = emptyList()
        // Recargar datos de muestra
        loadSampleData()
    }

    // Método para debug - ver todos los datos
    fun getAllPredictionsMap(): Map<String, List<Prediction>> {
        return userPredictionsMap.mapValues { it.value.toList() }
    }

    companion object {
        @Volatile
        private var INSTANCE: PredictionsRepository? = null

        fun getInstance(): PredictionsRepository {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PredictionsRepository().also { INSTANCE = it }
            }
        }
    }
}
