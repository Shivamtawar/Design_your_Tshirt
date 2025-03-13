package com.example.designyourt_shirt.uiScreen.Tshirt_Design


import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designyourt_shirt.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DesignLandingPage(
    navController: NavController
) {
    var selectedDesignMethod by remember { mutableStateOf<DesignMethod?>(null) }

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Your Unique Tee",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color(0xFF2C3E50)
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(16.dp,16.dp,16.dp,80.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hero Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3494E6),
                                Color(0xFF2C3E50)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_design_1),
                    contentDescription = "T-Shirt Design",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Design Method Selection
            Text(
                text = "How Do You Want to Design?",
                style = MaterialTheme.typography.titleLarge,
                color = Color(0xFF2C3E50),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Design Method Cards
            DesignMethodCard(
                method = DesignMethod.FromScratch,
                icon = Icons.Default.Create,
                title = "Design from Scratch",
                description = "Start with a blank canvas and let your creativity flow!",
                isSelected = selectedDesignMethod == DesignMethod.FromScratch,
                onSelect = { selectedDesignMethod = DesignMethod.FromScratch }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DesignMethodCard(
                method = DesignMethod.AddText,
                icon = Icons.Default.TextFields,
                title = "Add Custom Text",
                description = "Personalize your tee with unique typography.",
                isSelected = selectedDesignMethod == DesignMethod.AddText,
                onSelect = { selectedDesignMethod = DesignMethod.AddText }
            )

            Spacer(modifier = Modifier.height(16.dp))

            DesignMethodCard(
                method = DesignMethod.ChoosePalette,
                icon = Icons.Default.Palette,
                title = "Choose Color Palette",
                description = "Select your favorite colors and create a stunning design.",
                isSelected = selectedDesignMethod == DesignMethod.ChoosePalette,
                onSelect = { selectedDesignMethod = DesignMethod.ChoosePalette }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Designing Button
            Button(
                onClick = { navController.navigate("T_shirt") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2C3E50),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Start Designing",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Start Designing"
                )
            }
        }
    }
}

@Composable
fun DesignMethodCard(
    method: DesignMethod,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val elevationAnimation by animateDpAsState(
        targetValue = if (isSelected) 8.dp else 4.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        )
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF2C3E50) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevationAnimation
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) Color.White.copy(alpha = 0.2f)
                        else Color(0xFFF0F0F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else Color(0xFF2C3E50),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Content
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isSelected) Color.White else Color(0xFF2C3E50),
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isSelected) Color.White.copy(alpha = 0.7f) else Color.Gray
                )
            }
        }
    }
}

// Enum to represent design methods
enum class DesignMethod {
    FromScratch,
    AddText,
    ChoosePalette
}