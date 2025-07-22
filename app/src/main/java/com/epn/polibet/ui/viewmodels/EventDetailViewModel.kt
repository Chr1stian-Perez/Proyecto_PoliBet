package com.epn.polibet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.polibet.data.models.Event
import com.epn.polibet.data.repository.AuthRepository
import com.epn.polibet.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.epn.polibet.data.models.Prediction
import com.epn.polibet.data.repository.PredictionsRepository

class EventDetailViewModel(
    private val sportsRepository: SportsRepository = SportsRepository(),
    private val predictionsRepository: PredictionsRepository = PredictionsRepository.getInstance(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventDetailUiState())
    val uiState: StateFlow<EventDetailUiState> = _uiState.asStateFlow()

    fun loadEvent(eventId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)

            try {
                val event = sportsRepository.getEventById(eventId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    event = event,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Error al cargar evento"
                )
            }
        }
    }

    fun createPrediction(prediction: Prediction, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            try {
                // Obtener el usuario actual
                val currentUser = authRepository.currentUser.value
                if (currentUser == null) {
                    onResult(false, "Usuario no autenticado")
                    return@launch
                }

                // Verificar fondos suficientes antes de proceder
                if (!authRepository.hasEnoughBalance(currentUser.id, prediction.amount)) {
                    val currentBalance = authRepository.getCurrentBalance(currentUser.id)
                    onResult(false, "Fondos insuficientes. Balance actual: $${String.format("%.2f", currentBalance)}")
                    return@launch
                }

                // Crear la apuesta con el ID del usuario actual
                val predictionWithUser = prediction.copy(userId = currentUser.id)
                val result = predictionsRepository.createPrediction(predictionWithUser, authRepository)

                result.fold(
                    onSuccess = { createdPrediction ->
                        _uiState.value = _uiState.value.copy(
                            lastCreatedPrediction = createdPrediction,
                            error = null
                        )
                        val newBalance = authRepository.getCurrentBalance(currentUser.id)
                        onResult(true, "¡Pronóstico creado! Nuevo balance: $${String.format("%.2f", newBalance)}")
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = exception.message,
                            lastCreatedPrediction = null
                        )
                        onResult(false, exception.message ?: "Error al crear pronóstico")
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = e.message,
                    lastCreatedPrediction = null
                )
                onResult(false, e.message ?: "Error inesperado")
            }
        }
    }

    fun validateBetAmount(amount: Double): String? {
        val currentUser = authRepository.currentUser.value ?: return "Usuario no autenticado"
        val currentBalance = authRepository.getCurrentBalance(currentUser.id)

        return when {
            amount <= 0 -> "La cantidad debe ser mayor a 0"
            amount > currentBalance -> "Fondos insuficientes. Balance: $${String.format("%.2f", currentBalance)}"
            amount > 1000 -> "Cantidad máxima por apuesta: $1000"
            else -> null
        }
    }

    fun getCurrentUserBalance(): Double {
        val currentUser = authRepository.currentUser.value ?: return 0.0
        return authRepository.getCurrentBalance(currentUser.id)
    }

    fun clearLastCreatedPrediction() {
        _uiState.value = _uiState.value.copy(lastCreatedPrediction = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class EventDetailUiState(
    val isLoading: Boolean = false,
    val event: Event? = null,
    val lastCreatedPrediction: Prediction? = null,
    val error: String? = null
)
