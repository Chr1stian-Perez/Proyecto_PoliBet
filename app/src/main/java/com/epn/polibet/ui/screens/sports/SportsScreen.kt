package com.epn.polibet.ui.screens.sports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.epn.polibet.ui.components.FeaturedEventCard
import com.epn.polibet.ui.components.SectionHeader
import com.epn.polibet.ui.viewmodels.SportsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SportsScreen(
    sportId: String,
    onNavigateToEvent: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: SportsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(sportId) {
        viewModel.loadSportEvents(sportId)
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = {
                Text(
                    text = uiState.sportName,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }
        )
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.events.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.displayMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay eventos disponibles",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Vuelve más tarde para ver nuevos eventos",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    SectionHeader(
                        title = "Eventos Disponibles",
                        subtitle = "${uiState.events.size} eventos encontrados"
                    )
                }
                
                items(uiState.events) { event ->
                    FeaturedEventCard(
                        event = event,
                        onClick = { onNavigateToEvent(event.id) }
                    )
                }
            }
        }
    }
}
