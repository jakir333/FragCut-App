package com.fragcut.ui.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.fragcut.ui.theme.NeonGreen
import com.fragcut.ui.theme.NeonPurple
import com.fragcut.ui.theme.SurfaceDark

@Composable
fun EditingScreen(
    viewModel: EditorViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            EditorToolbar(
                onTrimClick = { viewModel.trimVideo("00:00:00", "00:00:10") }, // Dummy values for now
                onExportClick = { viewModel.exportVideo() },
                onRatioChange = viewModel::onAspectRatioChanged,
                currentRatio = uiState.aspectRatio
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Sticky Preview Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black)
                    .border(2.dp, NeonPurple, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (uiState.isProcessing) {
                    CircularProgressIndicator(color = NeonGreen)
                } else {
                    Text(
                        text = "VIDEO PREVIEW\n${uiState.aspectRatio.label}",
                        color = Color.White,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }

            // Timeline Area
            TimelineView(modifier = Modifier.weight(0.6f))
        }
    }
    
    // Status Snackbar/Dialog could go here
    if (uiState.successMessage != null) {
        // Simple overlay for success
        AlertDialog(
            onDismissRequest = viewModel::clearMessages,
            confirmButton = { TextButton(onClick = viewModel::clearMessages) { Text("OK", color = NeonGreen) } },
            title = { Text("Success") },
            text = { Text(uiState.successMessage!!) }
        )
    }
}

@Composable
fun TimelineView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(8.dp)
    ) {
        Text("TIMELINE", color = NeonGreen, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(bottom = 8.dp))
        
        // Tracks
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(10) { index ->
                Box(
                    modifier = Modifier
                        .width(100.dp)
                        .height(60.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (index % 2 == 0) Color.DarkGray else Color.Gray)
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        // Audio Track
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(NeonPurple.copy(alpha = 0.3f))
        ) {
            Text("Audio Track", color = NeonPurple, modifier = Modifier.padding(4.dp), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun EditorToolbar(
    onTrimClick: () -> Unit,
    onExportClick: () -> Unit,
    onRatioChange: (AspectRatio) -> Unit,
    currentRatio: AspectRatio
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceDark)
            .padding(16.dp)
    ) {
        // Aspect Ratio Switcher
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            AspectRatio.values().forEach { ratio ->
                FilterChip(
                    selected = ratio == currentRatio,
                    onClick = { onRatioChange(ratio) },
                    label = { Text(ratio.label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = NeonGreen,
                        selectedLabelColor = Color.Black,
                        labelColor = Color.White
                    )
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = onTrimClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonPurple)
            ) {
                Icon(Icons.Default.ContentCut, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("TRIM")
            }

            Button(
                onClick = onExportClick,
                colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black)
                Spacer(Modifier.width(8.dp))
                Text("EXPORT", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }
}
