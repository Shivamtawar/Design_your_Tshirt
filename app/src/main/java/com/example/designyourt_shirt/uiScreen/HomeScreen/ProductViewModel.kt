package com.example.designyourt_shirt.uiScreen.HomeScreen



import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.designyourt_shirt.Model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Base64

class ProductViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    init {
        listenToProductChanges()
    }

    private fun listenToProductChanges() {
        val productsRef = database.getReference("wall_designs")
        productsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val productList = snapshot.children.mapNotNull { childSnapshot ->
                    childSnapshot.getValue(Product::class.java)?.copy(
                        id = childSnapshot.key ?: ""
                    )
                }
                _products.value = productList
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error fetching products: ${error.message}")
            }
        })
    }

    fun toggleFavorite(productId: String) {
        val currentProducts = _products.value
        val productIndex = currentProducts.indexOfFirst { it.id == productId }

        if (productIndex != -1) {
            val product = currentProducts[productIndex]
            val updatedProduct = product.copy(isLiked = !product.isLiked)

            // Update local state immediately for responsive UI
            val updatedList = currentProducts.toMutableList()
            updatedList[productIndex] = updatedProduct
            _products.value = updatedList

            // Update in Firebase
            viewModelScope.launch {
                try {
                    database.getReference("wall_designs")
                        .child(productId)
                        .child("isLiked")
                        .setValue(updatedProduct.isLiked)
                        .await()
                } catch (e: Exception) {
                    println("Error updating favorite status: ${e.message}")
                    // Revert local state if update fails
                    _products.value = currentProducts
                }
            }
        }
    }




    @SuppressLint("NewApi")
    fun uploadProduct(
        name: String,
        price: Double,
        description: String,
        category: String,
        imageBytes: ByteArray
    ) {
        viewModelScope.launch {
            try {
                // Convert image to Base64 string using Java's Base64
                val imageBase64 = Base64.getEncoder().encodeToString(imageBytes)

                // Create product object
                val newProduct = Product(
                    name = name,
                    imageBase64 = imageBase64,
                    price = price,
                    description = description,
                    category = category
                )

                // Save to Realtime Database
                val productsRef = database.getReference("wall_designs")
                val newProductRef = productsRef.push()
                newProductRef.setValue(newProduct).await()
            } catch (e: Exception) {
                println("Error uploading product: ${e.message}")
            }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            try {
                database.getReference("wall_designs").child(productId).removeValue().await()
            } catch (e: Exception) {
                println("Error deleting product: ${e.message}")
            }
        }
    }

    // Utility function to convert Base64 to Bitmap
    @SuppressLint("NewApi")
    fun decodeBase64ToBitmap(base64String: String): Bitmap? {
        return try {
            // Decode Base64 using Java's Base64
            val decodedBytes = Base64.getDecoder().decode(base64String)
            BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
        } catch (e: Exception) {
            null
        }
    }
}
