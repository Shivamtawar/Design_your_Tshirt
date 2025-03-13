package com.example.designyourt_shirt.uiScreen.SignIn


import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(): ViewModel() {

    private val _state = MutableStateFlow<LogInStates>(LogInStates.Nothing)
    val state = _state.asStateFlow()
    val Auth = FirebaseAuth.getInstance()





    fun LogIN(navController: NavController, email:String, password:String){
        _state.value = LogInStates.Nothing

        Auth.signInWithEmailAndPassword(email,password).addOnCompleteListener{
            if(it.isSuccessful){
                _state.value = LogInStates.Success
                navController.navigate("BottomNav")

            }else{
                _state.value = LogInStates.Error
            }
        }

    }



}

sealed class LogInStates{

    object Nothing: LogInStates()
    object Error : LogInStates()
    object Success : LogInStates()
    object Loading : LogInStates()
}