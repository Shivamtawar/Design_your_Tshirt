package com.example.designyourt_shirt.Model



data class CartItem(
    val productId: String = "",
    val quantity: Int = 1,
    val size: String = "M",
    val color: String = "Default",
    val timestamp: Long = System.currentTimeMillis()
)
