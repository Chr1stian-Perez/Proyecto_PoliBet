package com.epn.polibet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.polibet.data.models.Event
import com.epn.polibet.data.models.Sport
import com.epn.polibet.data.repository.AuthRepository
import com.epn.polibet.data.repository.PredictionsRepository
import com.epn.polibet.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val sportsRepository: SportsRepository = SportsRepository(),
    private val predictionsRepository: PredictionsRepository = PredictionsRepository.getInstance(),
    private val authRepository: AuthRepository = AuthRepository.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
        // Refrescar estadísticas cada vez que se inicializa
        viewModelScope.launch {
            kotlinx.coroutines.delay(1000) // Esperar un poco para que se carguen los datos base
            loadPredictionsSummary()
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                val sports = sportsRepository.getSports()
                val featuredEvents = getFeaturedEvents()

                // Actualizar conteo de apuestas pendientes
                updatePredictionsCount()

                // Cargar resumen de predicciones
                loadPredictionsSummary()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    sports = sports,
                    featuredEvents = featuredEvents
                )

                // Cargar eventos en vivo
                sportsRepository.getLiveEvents().collect { liveEvents ->
                    _uiState.value = _uiState.value.copy(liveEvents = liveEvents)
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    private suspend fun updatePredictionsCount() {
        try {
            val currentUser = authRepository.currentUser.value
            if (currentUser != null) {
                val pendingCount = predictionsRepository.getPendingPredictionsCount(currentUser.id)
                _uiState.value = _uiState.value.copy(pendingPredictionsCount = pendingCount)
            }
        } catch (e: Exception) {
            // Ignorar errores en el conteo para no afectar el dashboard
        }
    }

    private suspend fun getFeaturedEvents(): List<Event> {
        // Obtener algunos eventos destacados de diferentes deportes
        val footballEvents = sportsRepository.getEventsBySport("football").take(2)
        val basketballEvents = sportsRepository.getEventsBySport("basketball").take(1)
        return footballEvents + basketballEvents
    }

    private suspend fun loadPredictionsSummary() {
        try {
            val currentUser = authRepository.currentUser.value
            if (currentUser != null) {
                val summary = predictionsRepository.getPredictionSummary(currentUser.id)
                _uiState.value = _uiState.value.copy(predictionsSummary = summary)

                // Debug: imprimir el resumen para verificar
                println("DEBUG Dashboard: Loaded summary - Total: ${summary.totalPredictions}, Won: ${summary.wonPredictions}, Rate: ${summary.winRate}%")
            } else {
                println("DEBUG Dashboard: No current user found")
            }
        } catch (e: Exception) {
            println("DEBUG Dashboard: Error loading predictions summary: ${e.message}")
            // No afectar el dashboard si hay error, pero mantener el estado actual
        }
    }

    fun refreshData() {
        loadDashboardData()
    }

    fun refreshPredictionsCount() {
        viewModelScope.launch {
            updatePredictionsCount()
        }
    }

    fun refreshPredictionsSummary() {
        viewModelScope.launch {
            loadPredictionsSummary()
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
}

data class DashboardUiState(
    val isLoading: Boolean = false,
    val sports: List<Sport> = emptyList(),
    val featuredEvents: List<Event> = emptyList(),
    val liveEvents: List<Event> = emptyList(),
    val pendingPredictionsCount: Int = 0,
    val predictionsSummary: com.epn.polibet.data.models.PredictionSummary? = null,
    val error: String? = null
)
