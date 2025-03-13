import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.designyourt_shirt.Model.Product
import com.example.designyourt_shirt.uiScreen.CartPage.CartViewModel
import com.example.designyourt_shirt.uiScreen.HomeScreen.ProductViewModel

@Composable
fun ProductCard(
    product: Product,
    navController: NavController
) {
    val productViewModel = viewModel<ProductViewModel>()
    val cartViewModel = viewModel<CartViewModel>()

    val isInCart by remember {
        derivedStateOf { cartViewModel.isInCart(product.id) }
    }

    // Convert Base64 to Bitmap
    val bitmap = remember(product.imageBase64) {
        productViewModel.decodeBase64ToBitmap(product.imageBase64)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                navController.navigate("productDetails/${product.id}")
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(modifier = Modifier.aspectRatio(1f)) {
            // Display decoded bitmap
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                // Like button
                IconButton(
                    onClick = {
                        productViewModel.toggleFavorite(product.id)
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Color.White.copy(alpha = 0.7f),
                            shape = CircleShape
                        )
                ) {
                    Icon(
                        imageVector = if (product.isLiked) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = "Like",
                        tint = if (product.isLiked) Color.Red else Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))


            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$${String.format("%.2f", product.price)}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF2C3E50)
                )

                if (isInCart) {
                    Badge(
                        containerColor = Color(0xFF2C3E50),
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Text("In Cart", color = Color.White)
                    }
                }
            }
        }
    }
}