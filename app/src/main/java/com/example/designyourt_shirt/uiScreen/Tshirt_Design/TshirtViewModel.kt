package com.example.designyourt_shirt.uiScreen.Tshirt_Design

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TShirtViewModel : ViewModel() {
    // Using StateFlow for better lifecycle handling
    private val _designData = MutableStateFlow<TShirtDesignData?>(null)
    val designData: StateFlow<TShirtDesignData?> = _designData.asStateFlow()

    // Alternative using State for simpler Compose integration
    private val _designState = mutableStateOf<TShirtDesignData?>(null)
    val designState: State<TShirtDesignData?> = _designState

    fun setDesignData(data: TShirtDesignData) {
        viewModelScope.launch {
            _designData.value = data
            _designState.value = data
        }
    }

    // Helper method to clear design data if needed
    fun clearDesignData() {
        viewModelScope.launch {
            _designData.value = null
            _designState.value = null
        }
    }
}

