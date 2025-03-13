package com.example.designyourt_shirt.uiScreen.CheckOutPage

import TShirtCanvasDesign
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.designyourt_shirt.uiScreen.Tshirt_Design.TShirtDesignData

@Composable
fun ProductSummaryCard(designData: TShirtDesignData?) {
    if (designData == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // T-Shirt Preview using Canvas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Front View
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Front",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Canvas T-shirt design for front
                        TShirtCanvasDesign(
                            color = designData.color,
                            isFront = true
                        )

                        // Design overlay if available
                        designData.frontDesign?.let { design ->
                            RenderDesign(design = design)
                        }
                    }
                }

                // Back View
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Back",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Canvas T-shirt design for back
                        TShirtCanvasDesign(
                            color = designData.color,
                            isFront = false
                        )

                        // Design overlay if available
                        designData.backDesign?.let { design ->
                            RenderDesign(design = design)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Product Details",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Divider()

                // Product Type
                DetailRow(label = "Product", value = "Custom T-Shirt")

                // Color
                val colorName = when(designData.color) {
                    Color.White -> "White"
                    Color.Black -> "Black"
                    Color.Red -> "Red"
                    Color.Blue -> "Blue"
                    Color.Green -> "Green"
                    Color.Yellow -> "Yellow"
                    Color.Cyan -> "Cyan"
                    Color.LightGray -> "Light Gray"
                    Color(0xFF800020) -> "Burgundy"
                    Color(0xFF8B4513) -> "Saddle Brown"
                    Color(0xFFFF69B4) -> "Hot Pink"
                    Color(0xFF4B0082) -> "Indigo"
                    else -> "Custom Color"
                }
                DetailRow(label = "Color", value = colorName)

                // Size (assuming a default size for now)
                DetailRow(label = "Size", value = "Medium (Default)")

                // Design details
                val frontDesignType = getDesignTypeText(designData.frontDesign)
                val backDesignType = getDesignTypeText(designData.backDesign)

                if (frontDesignType.isNotEmpty()) {
                    DetailRow(label = "Front Design", value = frontDesignType)
                }

                if (backDesignType.isNotEmpty()) {
                    DetailRow(label = "Back Design", value = backDesignType)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                // Price Information
                DetailRow(
                    label = "Base Price",
                    value = "$19.99",
                    isPrice = true
                )

                if (designData.frontDesign != null || designData.backDesign != null) {
                    DetailRow(
                        label = "Design Fee",
                        value = "$5.99",
                        isPrice = true
                    )
                }

                DetailRow(
                    label = "Total",
                    value = if (designData.frontDesign != null || designData.backDesign != null) "$25.98" else "$19.99",
                    isPrice = true,
                    isTotal = true
                )
            }
        }
    }
}



@Composable
fun RenderDesign(design: com.example.designyourt_shirt.Model.DesignState) {
    val context = LocalContext.current

    if (design.resourceId != null) {
        // For predefined designs, use the resource
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(design.resourceId)
                .crossfade(true)
                .build(),
            contentDescription = "Design",
            modifier = Modifier
                .width(100.dp * design.scale)
                .aspectRatio(1f)
        )
    } else if (design.customImageUri != null) {
        // For custom uploaded designs
        AsyncImage(
            model = ImageRequest.Builder(context)
                .data(design.customImageUri)
                .crossfade(true)
                .build(),
            contentDescription = "Custom Design",
            modifier = Modifier
                .width(100.dp * design.scale)
                .aspectRatio(1f)
        )
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    isPrice: Boolean = false,
    isTotal: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isTotal) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isPrice || isTotal) FontWeight.Bold else FontWeight.Normal,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Helper function to get design type text
private fun getDesignTypeText(design: com.example.designyourt_shirt.Model.DesignState?): String {
    return when {
        design == null -> ""
        design.resourceId != null -> "Predefined Design"
        design.customImageUri != null -> "Custom Uploaded Design"
        else -> "No Design"
    }
}