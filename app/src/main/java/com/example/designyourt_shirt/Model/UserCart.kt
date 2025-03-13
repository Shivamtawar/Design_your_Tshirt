package com.example.designyourt_shirt.Model



data class UserCart(
    val userId: String = "",
    val items: Map<String, CartItem> = mapOf()
)
