package com.example.designyourt_shirt.uiScreen.Tshirt_Design

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.designyourt_shirt.Model.DesignState
import com.example.designyourt_shirt.R
import java.io.File
import kotlin.math.max
import kotlin.math.min

@SuppressLint("UnrememberedMutableState")
@Composable
fun TShirtDesigner(
    modifier: Modifier = Modifier,
    onExportToCheckout: (TShirtDesignData) -> Unit = {} // Added callback for export
) {
    val context = LocalContext.current
    var selectedColor by remember { mutableStateOf(Color.White) }
    var showingFront by remember { mutableStateOf(true) }
    var customDesignDialogVisible by remember { mutableStateOf(false) }

    // Separate design states for front and back
    var frontDesignState by remember { mutableStateOf<DesignState?>(null) }
    var backDesignState by remember { mutableStateOf<DesignState?>(null) }

    // Track the currently active design state based on view
    val currentDesignState by derivedStateOf {
        if (showingFront) frontDesignState else backDesignState
    }

    // Track if the design is being edited
    var isEditingDesign by remember { mutableStateOf(false) }

    // T-shirt boundaries for constraining design movement
    var tShirtBounds by remember { mutableStateOf(Offset.Zero to Offset.Zero) }

    // Define color options
    val colorOptions = listOf(
        Color.White,
        Color.Red,
        Color.Blue,
        Color.Green,
        Color.Yellow,
        Color.Cyan,
        Color.LightGray,
        Color.Black,
        Color(0xFF800020), // Burgundy
        Color(0xFF8B4513), // SaddleBrown
        Color(0xFFFF69B4), // Hot Pink
        Color(0xFF4B0082)  // Indigo
    )

    // Predefined designs (would be replaced with actual drawable resources)
    // Using placeholder IDs - these would be replaced with your actual resource IDs
    val predefinedDesigns = listOf(
        R.drawable.ic_design_1,
        R.drawable.ic_design_2,
        R.drawable.ic_design_3,
        R.drawable.ic_design_4,
        R.drawable.ic_design_5,
        R.drawable.ic_design_6
    )

    if (customDesignDialogVisible) {
        CustomDesignDialog(
            onDismiss = { customDesignDialogVisible = false },
            onDesignSelected = { designState ->
                // Apply the selected design to the current side
                if (showingFront) {
                    frontDesignState = designState
                } else {
                    backDesignState = designState
                }
                // Start in editing mode for the new design
                isEditingDesign = true
                customDesignDialogVisible = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp, 40.dp, 16.dp, 16.dp)
    ) {
        // Top bar with view toggle and indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "T-Shirt Designer",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Show which side has designs
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (frontDesignState != null) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Front Design", color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }

                    if (backDesignState != null) {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            Text("Back Design", color = MaterialTheme.colorScheme.onSecondary)
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically  // Added this line to ensure vertical centering
            ) {
                // View toggle button - same size as checkout button
                Button(
                    onClick = {
                        showingFront = !showingFront
                        // Reset editing mode when switching sides
                        isEditingDesign = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)  // Set fixed height to ensure both buttons are the same size
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically  // Align content vertically within the button
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = "Toggle Front/Back View"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (showingFront) "Back" else "Front")
                    }
                }

                // Checkout button - same size as toggle button
                Button(
                    onClick = {
                        val imageUri = getImageUri(frontDesignState, backDesignState)
                        // Create data object with all design information
                        val designData = TShirtDesignData(
                            color = selectedColor,
                            frontDesign = frontDesignState,
                            backDesign = backDesignState,
                            imageUri = imageUri
                        )
                        // Call the export function
                        onExportToCheckout(designData)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    modifier = Modifier.height(40.dp)  // Set fixed height to ensure both buttons are the same size
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically  // Align content vertically within the button
                    ) {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Export to Checkout"
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Checkout")
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Color options on the left
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
            ) {
                Text(
                    text = "Colors",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                colorOptions.forEach { color ->
                    ColorOption(
                        color = color,
                        isSelected = color == selectedColor,
                        onClick = { selectedColor = color }
                    )
                }
            }

            // T-shirt in the middle
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                RealisticTShirt(
                    color = selectedColor,
                    showingFront = showingFront,
                    designState = currentDesignState,
                    isEditingDesign = isEditingDesign,
                    onDesignStateChange = { newState ->
                        if (showingFront) {
                            frontDesignState = newState
                        } else {
                            backDesignState = newState
                        }
                    },
                    onTShirtBoundsUpdated = { topLeft, bottomRight ->
                        tShirtBounds = topLeft to bottomRight
                    }
                )

                // Design editing controls - now visible for both front and back designs
                this@Row.AnimatedVisibility(
                    visible = currentDesignState != null,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .shadow(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Toggle edit mode
                            IconButton(onClick = { isEditingDesign = !isEditingDesign }) {
                                Icon(
                                    imageVector = if (isEditingDesign) Icons.Default.Refresh else Icons.Default.SwapHoriz,
                                    contentDescription = "Toggle Edit Mode"
                                )
                            }

                            // Zoom in
                            IconButton(
                                onClick = {
                                    if (showingFront) {
                                        frontDesignState?.let {
                                            frontDesignState = it.copy(scale = min(it.scale * 1.2f, 3.0f))
                                        }
                                    } else {
                                        backDesignState?.let {
                                            backDesignState = it.copy(scale = min(it.scale * 1.2f, 3.0f))
                                        }
                                    }
                                },
                                enabled = isEditingDesign
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomIn,
                                    contentDescription = "Zoom In",
                                    tint = if (isEditingDesign)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }

                            // Zoom out
                            IconButton(
                                onClick = {
                                    if (showingFront) {
                                        frontDesignState?.let {
                                            frontDesignState = it.copy(scale = max(it.scale * 0.8f, 0.5f))
                                        }
                                    } else {
                                        backDesignState?.let {
                                            backDesignState = it.copy(scale = max(it.scale * 0.8f, 0.5f))
                                        }
                                    }
                                },
                                enabled = isEditingDesign
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ZoomOut,
                                    contentDescription = "Zoom Out",
                                    tint = if (isEditingDesign)
                                        MaterialTheme.colorScheme.primary
                                    else
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }

                            // Remove design button
                            IconButton(
                                onClick = {
                                    if (showingFront) {
                                        frontDesignState = null
                                    } else {
                                        backDesignState = null
                                    }
                                    isEditingDesign = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Remove Design",
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }

                        }
                    }
                }
            }
        }

        // Bottom section with design options
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp, 16.dp, 16.dp, 40.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Show which side we're currently designing
                Text(
                    text = if (showingFront) "Front Designs" else "Back Designs",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Button(
                    onClick = { customDesignDialogVisible = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Custom Design"
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Custom Design")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Predefined designs
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                // For the "None" option
                item {  // Add this
                    DesignOption(
                        resourceId = null,
                        isSelected = currentDesignState == null,
                        onClick = {
                            if (showingFront) {
                                frontDesignState = null
                            } else {
                                backDesignState = null
                            }
                        }
                    )
                }

                // For the predefined designs
                items(predefinedDesigns) { designId ->
                    DesignOption(
                        resourceId = designId,
                        isSelected = currentDesignState?.resourceId == designId,
                        onClick = {
                            if (showingFront) {
                                frontDesignState = DesignState(resourceId = designId)
                            } else {
                                backDesignState = DesignState(resourceId = designId)
                            }
                        }
                    )
                }
            }
        }
    }
}

fun getImageUri(frontDesign: DesignState?, backDesign: DesignState?): String? {
    return frontDesign?.customImageUri?.toString() ?: backDesign?.customImageUri?.toString()
}

// Data class to hold all design information for export
data class TShirtDesignData(
    val color: Color,
    val frontDesign: DesignState?,
    val backDesign: DesignState?,
    val imageUri: String?
)

@Composable
fun CustomDesignDialog(
    onDismiss: () -> Unit,
    onDesignSelected: (DesignState) -> Unit
) {
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri

            try {
                // Load the selected image and convert it to a bitmap
                val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = ImageDecoder.createSource(context.contentResolver, uri)
                    ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                }

                // Create a design state with the URI directly
                val designState = DesignState(customImageUri = uri)

                // Pass it back to the parent
                onDesignSelected(designState)

                Toast.makeText(context, "Design added successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Camera launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            try {
                // Save the bitmap to a temporary file
                val fileName = "camera_design_${System.currentTimeMillis()}.png"
                val file = File(context.filesDir, fileName)

                file.outputStream().use { out ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                }

                // Create a URI for the saved file
                val fileUri = Uri.fromFile(file)

                // Create a design state with the camera image
                val designState = DesignState(customImageUri = fileUri)

                // Pass it back to the parent
                onDesignSelected(designState)

                Toast.makeText(context, "Photo added successfully!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to save photo: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Custom Design") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = {
                        // Launch gallery to select an image
                        galleryLauncher.launch("image/*")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Gallery"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Choose from Gallery")
                }

                Button(
                    onClick = {
                        // Launch camera to take a photo
                        cameraLauncher.launch(null)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Camera"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Take a Photo")
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun CustomDesignOption(
    designState: DesignState?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        when {
            designState == null -> {
                Text(
                    text = "None",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            designState.resourceId != null -> {
                Image(
                    painter = painterResource(id = designState.resourceId),
                    contentDescription = "Design Option",
                    modifier = Modifier.size(60.dp)
                )
            }
            designState.customImageUri != null -> {
                // Custom image code here
                val context = LocalContext.current
                val bitmap = remember(designState.customImageUri) {
                    try {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(context.contentResolver, designState.customImageUri)
                            ImageDecoder.decodeBitmap(source)
                        } else {
                            @Suppress("DEPRECATION")
                            MediaStore.Images.Media.getBitmap(context.contentResolver, designState.customImageUri)
                        }
                    } catch (e: Exception) {
                        null
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Custom Design",
                        modifier = Modifier.size(60.dp)
                    )
                } else {
                    Text(
                        text = "Custom",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Composable
fun ColorOption(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = CircleShape
            )
            .clickable(onClick = onClick)
    )
}

@Composable
fun DesignOption(
    resourceId: Int?, // Keep the original signature
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray.copy(alpha = 0.3f))
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.LightGray,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (resourceId != null) {
            Image(
                painter = painterResource(id = resourceId),
                contentDescription = "Design Option",
                modifier = Modifier.size(60.dp)
            )
        } else {
            Text(
                text = "None",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}


private fun saveImageAndGetResourceId(context: android.content.Context, bitmap: Bitmap, uri: Uri): DesignState {
    try {
        // Generate a unique filename
        val fileName = "custom_design_${System.currentTimeMillis()}.png"
        val file = File(context.filesDir, fileName)

        // Save the bitmap to the file
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Create a URI for the saved file
        val fileUri = Uri.fromFile(file)

        // Return a DesignState with the custom image URI
        return DesignState(customImageUri = fileUri)
    } catch (e: Exception) {
        // Log the error
        e.printStackTrace()

        // Return a fallback design
        return DesignState(resourceId = R.drawable.ic_design_1)
    }
}


