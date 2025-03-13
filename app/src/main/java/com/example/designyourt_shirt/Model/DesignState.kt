package com.example.designyourt_shirt.Model

import android.net.Uri
import androidx.compose.ui.geometry.Offset

data class DesignState(
    val resourceId: Int? = null,
    val customImageUri: Uri? = null,
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f

)