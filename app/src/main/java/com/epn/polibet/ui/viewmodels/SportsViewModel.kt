package com.epn.polibet.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epn.polibet.data.models.Event
import com.epn.polibet.data.repository.SportsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SportsViewModel(
    private val sportsRepository: SportsRepository = SportsRepository()
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(SportsUiState())
    val uiState: StateFlow<SportsUiState> = _uiState.asStateFlow()
    
    fun loadSportEvents(sportId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val events = sportsRepository.getEventsBySport(sportId)
                val sports = sportsRepository.getSports()
                val sportName = sports.find { it.id == sportId }?.name ?: "Deporte"
                
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    events = events,
                    sportName = sportName
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
}

data class SportsUiState(
    val isLoading: Boolean = false,
    val events: List<Event> = emptyList(),
    val sportName: String = "",
    val error: String? = null
)
