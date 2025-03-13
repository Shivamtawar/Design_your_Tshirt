package com.example.designyourt_shirt.uiScreen.Tshirt_Design

import android.graphics.Bitmap
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.designyourt_shirt.Model.DesignState
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore

@Composable
fun RealisticTShirt(
    color: Color,
    showingFront: Boolean,
    designState: DesignState?,
    isEditingDesign: Boolean,
    onDesignStateChange: (DesignState?) -> Unit,
    onTShirtBoundsUpdated: (Offset, Offset) -> Unit
)  {
    val density = LocalDensity.current
    val context = LocalContext.current

    // For tracking the T-shirt's printable area
    var tShirtAreaTopLeft by remember { mutableStateOf(Offset.Zero) }
    var tShirtAreaBottomRight by remember { mutableStateOf(Offset.Zero) }
    var tShirtCenter by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .width(280.dp)
            .height(350.dp)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInWindow()
                val width = bounds.width
                val height = bounds.height

                tShirtAreaTopLeft = Offset(
                    width * 0.25f,
                    height * 0.2f
                )
                tShirtAreaBottomRight = Offset(
                    width * 0.75f,
                    height * 0.7f
                )
                tShirtCenter = Offset(
                    (tShirtAreaTopLeft.x + tShirtAreaBottomRight.x) / 2f,
                    (tShirtAreaTopLeft.y + tShirtAreaBottomRight.y) / 2f
                )

                onTShirtBoundsUpdated(tShirtAreaTopLeft, tShirtAreaBottomRight)
            },
        contentAlignment = Alignment.Center
    ) {
        // Draw T-shirt base shape
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val bodyPath = if (showingFront) {
                Path().apply {
                    moveTo(width * 0.3f, height * 0.18f)
                    lineTo(width * 0.08f, height * 0.25f)
                    lineTo(width * 0.05f, height * 0.38f)
                    lineTo(width * 0.15f, height * 0.42f)
                    lineTo(width * 0.18f, height * 0.9f)
                    lineTo(width * 0.82f, height * 0.9f)
                    lineTo(width * 0.85f, height * 0.42f)
                    lineTo(width * 0.95f, height * 0.38f)
                    lineTo(width * 0.92f, height * 0.25f)
                    lineTo(width * 0.7f, height * 0.18f)
                    close()
                }
            } else {
                Path().apply {
                    moveTo(width * 0.3f, height * 0.15f)
                    lineTo(width * 0.08f, height * 0.25f)
                    lineTo(width * 0.05f, height * 0.38f)
                    lineTo(width * 0.15f, height * 0.42f)
                    lineTo(width * 0.18f, height * 0.9f)
                    lineTo(width * 0.82f, height * 0.9f)
                    lineTo(width * 0.85f, height * 0.42f)
                    lineTo(width * 0.95f, height * 0.38f)
                    lineTo(width * 0.92f, height * 0.25f)
                    lineTo(width * 0.7f, height * 0.15f)
                    close()
                }
            }

            drawPath(path = bodyPath, color = color)
            drawPath(
                path = bodyPath,
                color = Color.Black.copy(alpha = 0.5f),
                style = Stroke(width = 1.5f)
            )

            // Show printable area on both front and back when editing
            if (isEditingDesign) {
                drawRect(
                    color = Color.Red.copy(alpha = 0.1f),
                    topLeft = tShirtAreaTopLeft,
                    size = androidx.compose.ui.geometry.Size(
                        tShirtAreaBottomRight.x - tShirtAreaTopLeft.x,
                        tShirtAreaBottomRight.y - tShirtAreaTopLeft.y
                    ),
                    style = Stroke(width = 1.5f)
                )
            }
        }

        // Design rendering and interactions
        if (designState != null) {
            // Ensure design starts centered
            LaunchedEffect(designState, tShirtCenter) {
                if (designState.offset == Offset.Zero && tShirtCenter != Offset.Zero) {
                    onDesignStateChange(
                        designState.copy(
                            offset = tShirtCenter,
                            scale = 1f,
                            rotation = 0f
                        )
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(isEditingDesign, tShirtAreaTopLeft, tShirtAreaBottomRight) {
                        if (isEditingDesign) {
                            detectTransformGestures { centroid, pan, zoom, rotation ->
                                try {
                                    val currentOffset = designState.offset
                                    val currentScale = designState.scale
                                    val currentRotation = designState.rotation

                                    // Calculate new scale first, as it affects boundary constraints
                                    val newScale = (currentScale * zoom).coerceIn(0.5f, 3.0f)

                                    // Calculate new offset based on pan
                                    val newOffset = currentOffset + pan

                                    // Safety check to avoid potential NaN or infinity values
                                    if (newOffset.x.isFinite() && newOffset.y.isFinite() &&
                                        tShirtAreaTopLeft.x.isFinite() && tShirtAreaTopLeft.y.isFinite() &&
                                        tShirtAreaBottomRight.x.isFinite() && tShirtAreaBottomRight.y.isFinite()
                                    ) {
                                        // Calculate design dimensions based on new scale
                                        val designWidth = 120.dp.toPx() * newScale / 2
                                        val designHeight = 120.dp.toPx() * newScale / 2

                                        // Check if the T-shirt area is valid
                                        if (tShirtAreaTopLeft.x < tShirtAreaBottomRight.x &&
                                            tShirtAreaTopLeft.y < tShirtAreaBottomRight.y &&
                                            designWidth > 0 && designHeight > 0
                                        ) {
                                            // Add padding to prevent going exactly to the edge
                                            val padding = 2f
                                            val minX = tShirtAreaTopLeft.x + padding
                                            val maxX = tShirtAreaBottomRight.x - padding
                                            val minY = tShirtAreaTopLeft.y + padding
                                            val maxY = tShirtAreaBottomRight.y - padding

                                            // Check if the constraints are valid before applying them
                                            val constrainedX = if (minX < maxX) {
                                                newOffset.x.coerceIn(minX, maxX)
                                            } else {
                                                currentOffset.x // Fallback to current if constraints are invalid
                                            }

                                            val constrainedY = if (minY < maxY) {
                                                newOffset.y.coerceIn(minY, maxY)
                                            } else {
                                                currentOffset.y // Fallback to current if constraints are invalid
                                            }

                                            onDesignStateChange(
                                                designState.copy(
                                                    offset = Offset(constrainedX, constrainedY),
                                                    scale = newScale,
                                                    rotation = currentRotation + rotation
                                                )
                                            )
                                        } else {
                                            // If bounds are invalid, just update rotation and scale but keep position
                                            onDesignStateChange(
                                                designState.copy(
                                                    scale = newScale,
                                                    rotation = currentRotation + rotation
                                                )
                                            )
                                        }
                                    }
                                } catch (e: Exception) {
                                    // In case of any exception, just ignore this gesture update
                                    // This prevents crashes when unexpected conditions occur
                                }
                            }
                        }
                    }
            ) {
                // Render the design based on whether it's a resource ID or custom image URI
                if (designState.resourceId != null) {
                    // Resource-based design
                    Image(
                        painter = painterResource(id = designState.resourceId),
                        contentDescription = "T-Shirt Design",
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.Center)
                            .graphicsLayer {
                                translationX = designState.offset.x - tShirtCenter.x
                                translationY = designState.offset.y - tShirtCenter.y
                                scaleX = designState.scale
                                scaleY = designState.scale
                                rotationZ = designState.rotation
                                alpha = if (isEditingDesign) 0.9f else 1f
                            },
                        colorFilter = if (color.calculateLuminance() < 0.2f) {
                            ColorFilter.tint(Color.White.copy(alpha = 0.9f))
                        } else {
                            null
                        }
                    )
                } else if (designState.customImageUri != null) {
                    // Custom image from URI
                    val bitmap = remember(designState.customImageUri) {
                        try {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                val source = ImageDecoder.createSource(
                                    context.contentResolver,
                                    designState.customImageUri
                                )
                                ImageDecoder.decodeBitmap(source)
                            } else {
                                @Suppress("DEPRECATION")
                                MediaStore.Images.Media.getBitmap(
                                    context.contentResolver,
                                    designState.customImageUri
                                )
                            }
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Custom T-Shirt Design",
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.Center)
                                .graphicsLayer {
                                    translationX = designState.offset.x - tShirtCenter.x
                                    translationY = designState.offset.y - tShirtCenter.y
                                    scaleX = designState.scale
                                    scaleY = designState.scale
                                    rotationZ = designState.rotation
                                    alpha = if (isEditingDesign) 0.9f else 1f
                                },
                            colorFilter = if (color.calculateLuminance() < 0.2f) {
                                ColorFilter.tint(Color.White.copy(alpha = 0.9f))
                            } else {
                                null
                            }
                        )
                    }
                }
            }
        }
    }
}

// Updated to match the DesignState model that includes customImageUri
data class DesignState(
    val resourceId: Int? = null,
    val customImageUri: Uri? = null,
    val offset: Offset = Offset.Zero,
    val scale: Float = 1f,
    val rotation: Float = 0f
)

fun Color.calculateLuminance(): Float {
    return (0.2126f * red + 0.7152f * green + 0.0722f * blue)
}