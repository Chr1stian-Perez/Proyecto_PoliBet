package com.epn.polibet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.polibet.data.models.Prediction
import com.epn.polibet.data.models.PredictionSummary
import com.epn.polibet.data.repository.AuthRepository
import com.epn.polibet.data.repository.PredictionsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PredictionsViewModel(
    private val predictionsRepository: PredictionsRepository = PredictionsRepository.getInstance(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(PredictionsUiState())
    val uiState: StateFlow<PredictionsUiState> = _uiState.asStateFlow()

    fun loadPredictions() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, error = null)

                val currentUser = authRepository.currentUser.value
                if (currentUser == null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        predictions = emptyList(),
                        summary = null,
                        error = null
                    )
                    return@launch
                }

                // Debug: verificar qué usuario está logueado
                println("DEBUG: Loading predictions for user: ${currentUser.username} (ID: ${currentUser.id})")

                val predictions = predictionsRepository.getUserPredictions(currentUser.id)
                println("DEBUG: Found ${predictions.size} predictions for user ${currentUser.username}")

                val summary = if (predictions.isNotEmpty()) {
                    predictionsRepository.getPredictionSummary(currentUser.id)
                } else {
                    null
                }

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    predictions = predictions,
                    summary = summary,
                    error = null
                )

            } catch (e: Exception) {
                println("DEBUG: Error loading predictions: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar pronósticos: ${e.message}"
                )
            }
        }
    }

    fun cancelPrediction(predictionId: String) {
        viewModelScope.launch {
            try {
                val result = predictionsRepository.cancelPrediction(predictionId, authRepository)
                result.fold(
                    onSuccess = {
                        // Recargar automáticamente después de cancelar
                        loadPredictions()
                    },
                    onFailure = { exception ->
                        _uiState.value = _uiState.value.copy(
                            error = "Error al cancelar: ${exception.message}"
                        )
                    }
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    error = "Error inesperado: ${e.message}"
                )
            }
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class PredictionsUiState(
    val isLoading: Boolean = false,
    val predictions: List<Prediction> = emptyList(),
    val summary: PredictionSummary? = null,
    val error: String? = null
)
