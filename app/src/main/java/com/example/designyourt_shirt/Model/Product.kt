package com.example.designyourt_shirt.Model

data class Product(
    val id: String = "",
    val name: String = "",
    val imageBase64: String = "", // Base64 encoded image
    val price: Double = 0.0,
    val description: String = "",
    val category: String = "",
    val isLiked: Boolean = false,
)