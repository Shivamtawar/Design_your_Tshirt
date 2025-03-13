package com.example.designyourt_shirt.uiScreen.SignUp

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.designyourt_shirt.Model.Users

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.io.encoding.ExperimentalEncodingApi

@HiltViewModel
class SignInViewModel @Inject constructor(@ApplicationContext val context: Context) : ViewModel() {

    private val _state = MutableStateFlow<SignInStates>(SignInStates.Nothing)
    val state = _state.asStateFlow()
    val Auth = FirebaseAuth.getInstance()


    fun SignIN(
        navController: NavController,
        email: String,
        password: String,
        username: String,
        phonenumber: String,
        image: Bitmap
    ) {
        _state.value = SignInStates.Loading
        val imageString = encodeImageToBase64(image)

        Auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                _state.value = SignInStates.Success
                val userId = FirebaseAuth.getInstance().currentUser!!.uid
                val user = Users(
                    id = userId,
                    username,
                    imageString,
                    email,
                    phonenumber,
                    password

                )
                FirebaseDatabase.getInstance().getReference("Users").child(userId).setValue(user)
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            Toast.makeText(context, "User signin succesfull", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                navController.navigate("BottomNav")

            } else {
                _state.value = SignInStates.Error
            }
        }

    }

    @OptIn(ExperimentalEncodingApi::class)
    fun encodeImageToBase64(image: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)

    }

    fun decodeBase64ToImage(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(base64Str, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }


}

sealed class SignInStates {
    object Nothing : SignInStates()
    object Error : SignInStates()
    object Success : SignInStates()
    object Loading : SignInStates()
}