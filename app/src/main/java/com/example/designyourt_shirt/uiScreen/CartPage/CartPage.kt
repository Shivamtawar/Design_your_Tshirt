

package com.example.designyourt_shirt.CartScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.designyourt_shirt.Model.CartItem
import com.example.designyourt_shirt.Model.Product
import com.example.designyourt_shirt.R
import com.example.designyourt_shirt.uiScreen.CartPage.CartViewModel
import com.example.designyourt_shirt.uiScreen.HomeScreen.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController
) {
    val cartViewModel = viewModel<CartViewModel>()
    val productViewModel = viewModel<ProductViewModel>()

    val cartProducts by cartViewModel.cartProducts.collectAsState()
    val totalPrice by cartViewModel.totalPrice.collectAsState()
    val isLoading by cartViewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "My Cart",
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
        },
        bottomBar = {
            // Checkout Button with Total Price
            BottomAppBar(
                containerColor = Color.Transparent,
                contentColor = Color(0xFF2C3E50)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Total Price
                    Text(
                        text = "Total:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF2C3E50)
                    )
                    Text(
                        text = String.format("$%.2f", totalPrice),
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    // Checkout Button
                    Button(
                        onClick = { navController.navigate("Checkout") },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2C3E50),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        enabled = cartProducts.isNotEmpty()
                    ) {
                        Text(
                            text = "Checkout",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2C3E50)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp)
                ) {
                    // Empty state
                    if (cartProducts.isEmpty()) {
                        item {
                            Column(
                                modifier = Modifier.fillParentMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_design_1),
                                    contentDescription = "Empty Cart",
                                    modifier = Modifier.size(200.dp),
                                    contentScale = ContentScale.Fit
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "Your cart is empty",
                                    fontSize = 18.sp,
                                    color = Color.Gray
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                    onClick = { navController.navigate("home") },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2C3E50)
                                    )
                                ) {
                                    Text("Browse Products")
                                }
                            }
                        }
                    }

                    // Cart Items
                    items(cartProducts, key = { it.first.id }) { (product, cartItem) ->
                        var isVisible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = isVisible,
                            exit = fadeOut(
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioLowBouncy,
                                    stiffness = Spring.StiffnessLow
                                )
                            )
                        ) {
                            CartItemCard(
                                product = product,
                                cartItem = cartItem,
                                productViewModel = productViewModel,
                                onQuantityChange = { newQuantity ->
                                    cartViewModel.updateQuantity(product.id, newQuantity)
                                },
                                onRemove = {
                                    isVisible = false
                                    // Remove item after animation
                                    cartViewModel.removeFromCart(product.id)
                                }
                            )
                        }
                    }

                    // Bottom Spacer
                    item {
                        Spacer(modifier = Modifier.height(180.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemCard(
    product: Product,
    cartItem: CartItem,
    productViewModel: ProductViewModel,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    // Convert Base64 to Bitmap
    val bitmap = remember(product.imageBase64) {
        productViewModel.decodeBase64ToBitmap(product.imageBase64)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        )
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Product Image
            bitmap?.let {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = product.name,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )
            } ?: run {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Gray)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Product Details
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = product.name,
                    fontSize = 18.sp,
                    textAlign = TextAlign.Start,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2C3E50)
                )
                Text(
                    text = String.format("$%.2f", product.price),
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Quantity Control
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Decrease Quantity
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0))
                            .clickable {
                                if (cartItem.quantity > 1)
                                    onQuantityChange(cartItem.quantity - 1)
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease Quantity",
                            tint = Color(0xFF2C3E50)
                        )
                    }

                    // Quantity
                    Text(
                        text = "${cartItem.quantity}",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2C3E50)
                    )

                    // Increase Quantity
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(Color(0xFFF0F0F0))
                            .clickable {
                                onQuantityChange(cartItem.quantity + 1)
                            }
                            .padding(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase Quantity",
                            tint = Color(0xFF2C3E50)
                        )
                    }
                }
            }

            // Remove Item
            IconButton(
                onClick = onRemove
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remove Item",
                    tint = Color.Red
                )
            }
        }
    }
}