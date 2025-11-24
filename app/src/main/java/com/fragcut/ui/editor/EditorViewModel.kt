package com.fragcut.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fragcut.data.VideoEditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class EditorUiState(
    val selectedVideoPath: String? = null,
    val isProcessing: Boolean = false,
    val aspectRatio: AspectRatio = AspectRatio.RATIO_16_9,
    val error: String? = null,
    val successMessage: String? = null
)

enum class AspectRatio(val label: String, val width: Int, val height: Int) {
    RATIO_16_9("16:9 (YouTube)", 1920, 1080),
    RATIO_9_16("9:16 (Shorts)", 1080, 1920),
    RATIO_1_1("1:1 (Insta)", 1080, 1080)
}

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: VideoEditorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    fun onVideoSelected(path: String) {
        _uiState.update { it.copy(selectedVideoPath = path) }
    }

    fun onAspectRatioChanged(ratio: AspectRatio) {
        _uiState.update { it.copy(aspectRatio = ratio) }
    }

    fun trimVideo(startTime: String, endTime: String) {
        val inputPath = _uiState.value.selectedVideoPath ?: return
        val outputPath = inputPath.replace(".mp4", "_trimmed.mp4") // Simple output naming

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            val success = repository.trimVideo(inputPath, outputPath, startTime, endTime)
            _uiState.update { 
                it.copy(
                    isProcessing = false, 
                    successMessage = if (success) "Trim Complete!" else null,
                    error = if (!success) "Trim Failed" else null
                ) 
            }
        }
    }

    fun exportVideo() {
        val inputPath = _uiState.value.selectedVideoPath ?: return
        val outputPath = inputPath.replace(".mp4", "_export.mp4")
        val ratio = _uiState.value.aspectRatio

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, error = null) }
            val success = repository.exportVideo(inputPath, outputPath, ratio.width, ratio.height, 60)
            _uiState.update {
                it.copy(
                    isProcessing = false,
                    successMessage = if (success) "Export Complete!" else null,
                    error = if (!success) "Export Failed" else null
                )
            }
        }
    }
    
    fun clearMessages() {
        _uiState.update { it.copy(error = null, successMessage = null) }
    }
}
