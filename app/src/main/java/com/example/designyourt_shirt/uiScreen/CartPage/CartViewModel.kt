package com.example.designyourt_shirt.uiScreen.CartPage


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designyourt_shirt.Model.CartItem
import com.example.designyourt_shirt.Model.Product
import com.example.designyourt_shirt.Model.UserCart
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: "anonymous"

    private val _cartItems = MutableStateFlow<Map<String, CartItem>>(emptyMap())
    val cartItems = _cartItems.asStateFlow()

    private val _cartProducts = MutableStateFlow<List<Pair<Product, CartItem>>>(emptyList())
    val cartProducts = _cartProducts.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _totalPrice = MutableStateFlow(0.0)
    val totalPrice = _totalPrice.asStateFlow()

    init {
        loadUserCart()
    }

    private fun loadUserCart() {
        _isLoading.value = true

        val cartRef = database.getReference("user_carts").child(currentUserId)
        cartRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = snapshot.child("items").children.associate { itemSnapshot ->
                    val cartItem = itemSnapshot.getValue(CartItem::class.java) ?: CartItem()
                    itemSnapshot.key!! to cartItem
                }

                _cartItems.value = cartItems
                loadCartProducts(cartItems)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error loading cart: ${error.message}")
                _isLoading.value = false
            }
        })
    }

    private fun loadCartProducts(cartItems: Map<String, CartItem>) {
        if (cartItems.isEmpty()) {
            _cartProducts.value = emptyList()
            _totalPrice.value = 0.0
            _isLoading.value = false
            return
        }

        val productsRef = database.getReference("wall_designs")
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = mutableListOf<Pair<Product, CartItem>>()
                var total = 0.0

                for ((productId, cartItem) in cartItems) {
                    val productSnapshot = snapshot.child(productId)
                    if (productSnapshot.exists()) {
                        val product = productSnapshot.getValue(Product::class.java)?.copy(
                            id = productId
                        )

                        product?.let {
                            productList.add(it to cartItem)
                            total += it.price * cartItem.quantity
                        }
                    }
                }

                _cartProducts.value = productList
                _totalPrice.value = total
                _isLoading.value = false
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error loading products: ${error.message}")
                _isLoading.value = false
            }
        })
    }

    fun addToCart(product: Product, quantity: Int = 1, size: String = "M", color: String = "Default") {
        viewModelScope.launch {
            try {
                val cartItem = CartItem(
                    productId = product.id,
                    quantity = quantity,
                    size = size,
                    color = color
                )

                database.getReference("user_carts")
                    .child(currentUserId)
                    .child("items")
                    .child(product.id)
                    .setValue(cartItem)
                    .await()

            } catch (e: Exception) {
                println("Error adding to cart: ${e.message}")
            }
        }
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(productId)
            return
        }

        viewModelScope.launch {
            try {
                database.getReference("user_carts")
                    .child(currentUserId)
                    .child("items")
                    .child(productId)
                    .child("quantity")
                    .setValue(quantity)
                    .await()
            } catch (e: Exception) {
                println("Error updating quantity: ${e.message}")
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            try {
                database.getReference("user_carts")
                    .child(currentUserId)
                    .child("items")
                    .child(productId)
                    .removeValue()
                    .await()
            } catch (e: Exception) {
                println("Error removing from cart: ${e.message}")
            }
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            try {
                database.getReference("user_carts")
                    .child(currentUserId)
                    .child("items")
                    .removeValue()
                    .await()
            } catch (e: Exception) {
                println("Error clearing cart: ${e.message}")
            }
        }
    }

    fun isInCart(productId: String): Boolean {
        return _cartItems.value.containsKey(productId)
    }
}