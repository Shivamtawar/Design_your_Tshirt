import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import android.graphics.Paint
import android.graphics.PathEffect
import androidx.compose.ui.graphics.asAndroidPath
import kotlin.random.Random

@Composable
fun TShirtCanvasDesign(color: Color, isFront: Boolean) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        val width = size.width
        val height = size.height

        // Define constants for collar
        val collarWidth = width * 0.3f
        val collarDepth = height * 0.1f
        val shoulderHeight = height * 0.15f
        val sleeveLength = width * 0.25f

        val tshirtPath = Path().apply {
            // Start from the middle top of the collar
            moveTo(width / 2f, 0f)

            if (isFront) {
                // Draw a more natural U shape for the collar
                cubicTo(
                    width / 2f - collarWidth / 3f, collarDepth / 3f,
                    width / 2f - collarWidth / 2f, collarDepth,
                    width / 2f - collarWidth / 2f, collarDepth
                )

                // Bottom of the collar
                lineTo(width / 2f + collarWidth / 2f, collarDepth)

                // Right curve of the collar back to the top
                cubicTo(
                    width / 2f + collarWidth / 2f, collarDepth,
                    width / 2f + collarWidth / 3f, collarDepth / 3f,
                    width / 2f, 0f
                )
            } else {
                // Back collar is higher and more straight
                cubicTo(
                    width / 2f - collarWidth / 4f, 0f,
                    width / 2f - collarWidth / 4f, collarDepth / 3f,
                    width / 2f - collarWidth / 4f, collarDepth / 3f
                )

                // Bottom of the collar
                lineTo(width / 2f + collarWidth / 4f, collarDepth / 3f)

                // Right curve of the collar back to the top
                cubicTo(
                    width / 2f + collarWidth / 4f, collarDepth / 3f,
                    width / 2f + collarWidth / 4f, 0f,
                    width / 2f, 0f
                )
            }

            close()
        }

        // Body of the T-shirt with more natural curves
        val bodyPath = Path().apply {
            // Start from top left of the body (left shoulder)
            moveTo(width * 0.05f, shoulderHeight)

            // Left sleeve with natural drape
            quadraticTo(
                width * 0.02f, shoulderHeight + sleeveLength * 0.6f,
                0f, shoulderHeight + sleeveLength
            )

            // Back to armpit with a curve
            quadraticTo(
                width * 0.1f, shoulderHeight + sleeveLength * 0.9f,
                width * 0.2f, shoulderHeight + sleeveLength
            )

            // Down to the waist on the left side with a slight curve
            quadraticTo(
                width * 0.15f, height * 0.7f,
                width * 0.1f, height
            )

            // Bottom of the shirt with a slight curve
            quadraticTo(
                width * 0.5f, height * 1.02f,
                width * 0.9f, height
            )

            // Up to the right armpit with a curve
            quadraticTo(
                width * 0.85f, height * 0.7f,
                width * 0.8f, shoulderHeight + sleeveLength
            )

            // Right sleeve with natural drape
            quadraticTo(
                width * 0.9f, shoulderHeight + sleeveLength * 0.9f,
                width, shoulderHeight + sleeveLength
            )

            // Right shoulder with a curve
            quadraticTo(
                width * 0.98f, shoulderHeight + sleeveLength * 0.6f,
                width * 0.95f, shoulderHeight
            )

            close()
        }

        // Draw base shirt color
        drawPath(
            path = bodyPath,
            color = color,
            style = Fill
        )

        // Create and draw subtle fabric texture
        val fabricPaint = android.graphics.Paint().apply {
            this.color = if (getLuminance(color) > 0.5f)
                0x08000000 // Semi-transparent black for light shirts
            else
                0x08FFFFFF // Semi-transparent white for dark shirts
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 0.5f
            isAntiAlias = true
        }

        // Use native canvas for more complex drawing
        drawContext.canvas.nativeCanvas.apply {
            val random = Random(0)  // Fixed seed for consistent pattern
            val lineCount = 150

            save()
            // Create clipping path to keep texture inside shirt
            val clipPath = android.graphics.Path()
            bodyPath.asAndroidPath()
            clipPath(clipPath)

            // Draw random short lines for fabric texture
            for (i in 0 until lineCount) {
                val startX = random.nextFloat() * width
                val startY = random.nextFloat() * height
                val endX = startX + random.nextFloat() * width * 0.05f - width * 0.025f
                val endY = startY + random.nextFloat() * height * 0.05f - height * 0.025f

                drawLine(startX, startY, endX, endY, fabricPaint)
            }
            restore()
        }

        // Draw collar with slight color difference for realism
        drawPath(
            path = tshirtPath,
            color = if (color == Color.White)
                Color.LightGray
            else
                Color(
                    red = (color.red * 0.95f).coerceIn(0f, 1f),
                    green = (color.green * 0.95f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.95f).coerceIn(0f, 1f),
                    alpha = color.alpha
                ),
            style = Fill
        )

        // Draw ribbed collar texture
        if (isFront) {
            val ribCount = 8
            val ribWidth = collarWidth / ribCount

            for (i in 0 until ribCount) {
                val startX = width / 2f - collarWidth / 2f + i * ribWidth
                drawLine(
                    color = if (getLuminance(color) > 0.5f)
                        Color.Gray.copy(alpha = 0.1f)
                    else
                        Color.White.copy(alpha = 0.1f),
                    start = Offset(startX, collarDepth * 0.3f),
                    end = Offset(startX, collarDepth * 0.9f),
                    strokeWidth = 1f
                )
            }
        }

        // Draw highlights and shadows for 3D effect
        val highlightPath = Path().apply {
            moveTo(width * 0.2f, shoulderHeight * 1.2f)
            quadraticTo(
                width * 0.3f, height * 0.4f,
                width * 0.25f, height * 0.7f
            )
        }

        drawPath(
            path = highlightPath,
            color = Color.White.copy(alpha = 0.1f),
            style = Stroke(width = width * 0.1f, cap = StrokeCap.Round)
        )

        val shadowPath = Path().apply {
            moveTo(width * 0.8f, shoulderHeight * 1.2f)
            quadraticTo(
                width * 0.7f, height * 0.4f,
                width * 0.75f, height * 0.7f
            )
        }

        drawPath(
            path = shadowPath,
            color = Color.Black.copy(alpha = 0.07f),
            style = Stroke(width = width * 0.1f, cap = StrokeCap.Round)
        )

        // Add fold lines that follow the contour of the shirt
        if (isFront) {
            // Center fold
            val centerFold = Path().apply {
                moveTo(width / 2f, height * 0.25f)
                cubicTo(
                    width / 2f - width * 0.03f, height * 0.4f,
                    width / 2f + width * 0.03f, height * 0.6f,
                    width / 2f, height * 0.8f
                )
            }

            drawPath(
                path = centerFold,
                color = if (getLuminance(color) > 0.5f)
                    Color.Black.copy(alpha = 0.04f)
                else
                    Color.White.copy(alpha = 0.04f),
                style = Stroke(width = width * 0.01f)
            )

            // Natural wrinkles near the bottom
            for (i in 0 until 4) {
                val startX = width * (0.3f + i * 0.15f)
                val startY = height * 0.7f
                val controlX = startX + width * 0.05f * (if (i % 2 == 0) 1 else -1)
                val endY = height * 0.9f

                val wrinklePath = Path().apply {
                    moveTo(startX, startY)
                    quadraticTo(
                        controlX, (startY + endY) / 2,
                        startX, endY
                    )
                }

                drawPath(
                    path = wrinklePath,
                    color = if (getLuminance(color) > 0.5f)
                        Color.Black.copy(alpha = 0.03f)
                    else
                        Color.White.copy(alpha = 0.03f),
                    style = Stroke(width = 1f)
                )
            }
        } else {
            // Back side has fewer wrinkles
            val backFolds = Path().apply {
                moveTo(width * 0.3f, height * 0.3f)
                lineTo(width * 0.3f, height * 0.7f)
                moveTo(width * 0.7f, height * 0.3f)
                lineTo(width * 0.7f, height * 0.7f)
            }

            drawPath(
                path = backFolds,
                color = if (getLuminance(color) > 0.5f)
                    Color.Black.copy(alpha = 0.02f)
                else
                    Color.White.copy(alpha = 0.02f),
                style = Stroke(width = 0.8f)
            )
        }

        // Draw subtle outline using native canvas for better control
        val outlinePaint = android.graphics.Paint().apply {
            this.color = if (getLuminance(color) > 0.5f)
                0x66808080 // Semi-transparent gray for light shirts
            else
                0x4D444444 // Semi-transparent dark gray for dark shirts
            style = android.graphics.Paint.Style.STROKE
            strokeWidth = 0.8f
            isAntiAlias = true
        }

        val bodyPathAndroid = android.graphics.Path()
        bodyPath.asAndroidPath()
        drawContext.canvas.nativeCanvas.drawPath(bodyPathAndroid, outlinePaint)

        val collarPathAndroid = android.graphics.Path()
        tshirtPath.asAndroidPath()
        drawContext.canvas.nativeCanvas.drawPath(collarPathAndroid, outlinePaint)

        // Add sleeve hem details
        val leftSleevePath = Path().apply {
            moveTo(0f, shoulderHeight + sleeveLength)
            lineTo(width * 0.2f, shoulderHeight + sleeveLength)
        }

        val rightSleevePath = Path().apply {
            moveTo(width, shoulderHeight + sleeveLength)
            lineTo(width * 0.8f, shoulderHeight + sleeveLength)
        }

        // Draw sleeve hems
        val hemStyle = Stroke(width = 2f)
        drawPath(
            path = leftSleevePath,
            color = if (getLuminance(color) > 0.5f)
                Color.Gray.copy(alpha = 0.3f)
            else
                Color(
                    red = (color.red * 0.8f).coerceIn(0f, 1f),
                    green = (color.green * 0.8f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.8f).coerceIn(0f, 1f),
                    alpha = 0.5f
                ),
            style = hemStyle
        )

        drawPath(
            path = rightSleevePath,
            color = if (getLuminance(color) > 0.5f)
                Color.Gray.copy(alpha = 0.3f)
            else
                Color(
                    red = (color.red * 0.8f).coerceIn(0f, 1f),
                    green = (color.green * 0.8f).coerceIn(0f, 1f),
                    blue = (color.blue * 0.8f).coerceIn(0f, 1f),
                    alpha = 0.5f
                ),
            style = hemStyle
        )
    }
}

// Helper function to calculate luminance without using the extension function
private fun getLuminance(color: Color): Float {
    return 0.2126f * color.red + 0.7152f * color.green + 0.0722f * color.blue
}

