package com.example.designyourt_shirt.Model

data class Users(
    val id : String = "",
    val username: String = "",
    val imageUrl:String? = null,
    val email : String = "",
    val phoneNumber : String = "",
    val password: String = "",
)