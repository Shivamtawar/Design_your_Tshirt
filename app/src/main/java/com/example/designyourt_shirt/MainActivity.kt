package com.example.designyourt_shirt


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.designyourt_shirt.CartScreen.CartScreen
//import com.example.designyourt_shirt.CartScreen.CartScreen
import com.example.designyourt_shirt.uiScreen.HomeScreen.HomeScreen
import com.example.designyourt_shirt.uiScreen.Navigarion.BottomNav
import com.example.designyourt_shirt.uiScreen.SignIn.LoginScreen
import com.example.designyourt_shirt.uiScreen.SignUp.SignUpScreen
import com.example.designyourt_shirt.ui.theme.DesignYourTShirtTheme
import com.example.designyourt_shirt.uiScreen.CheckOutPage.CheckoutScreen
import com.example.designyourt_shirt.uiScreen.CheckOutPage.CheckoutScreenExported
import com.example.designyourt_shirt.uiScreen.HomeScreen.ProductViewModel
import com.example.designyourt_shirt.uiScreen.ProductDetialScreen.ProductDetailsScreen
import com.example.designyourt_shirt.uiScreen.Profile.ProductUploadScreen
import com.example.designyourt_shirt.uiScreen.Tshirt_Design.TShirtDesigner
import com.example.designyourt_shirt.uiScreen.Tshirt_Design.TShirtViewModel
import com.example.designyourt_shirt.uiscreens.StartPage.GetStarted
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DesignYourTShirtTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    val tShirtViewModel: TShirtViewModel = viewModel()

                    val user = FirebaseAuth.getInstance().currentUser
                    val start = if (user != null) "BottomNav" else "StraingPage"
                    NavHost(
                        navController = navController,
                        startDestination = start,
                        modifier = Modifier.padding()
                    ) {
                        composable("T_shirt") {
                            TShirtDesigner(
                                modifier = Modifier.padding(innerPadding),
                                onExportToCheckout = { designData ->
                                    // Save design data to ViewModel
                                    tShirtViewModel.setDesignData(designData)
                                    // Navigate to checkout screen
                                    navController.navigate("checkout")
                                }
                            )

                        }

                        composable("SignUp") {
                            SignUpScreen(navController)
                        }

                        composable("SignIn") {
                            LoginScreen(navController)
                        }

                        composable("StraingPage") {
                            GetStarted(navController)
                        }

                        composable("BottomNav") {
                            BottomNav(navController)
                        }

                        composable("Home") {
                            HomeScreen(navController)
                        }

                        composable("Cart") {
                            CartScreen(navController)
                        }

                        composable(
                            route = "productDetails/{productId}",
                            arguments = listOf(navArgument("productId") {
                                type = NavType.StringType
                            })
                        ) { backStackEntry ->
                            val productViewModel = viewModel<ProductViewModel>()
                            val productId = backStackEntry.arguments?.getString("productId")

                            // Collect products as state
                            val products by productViewModel.products.collectAsState()

                            // Find the product by ID
                            val product = products.find { it.id == productId }

                            product?.let {
                                ProductDetailsScreen(navController, product)
                            }
                        }
                        composable("Upload") {
                            ProductUploadScreen()
                        }
                        composable("Checkout") {
                            CheckoutScreen(navController)
                        }
                        composable("checkout") {
                            CheckoutScreenExported(navController, tShirtViewModel)
                        }


                    }
                }
            }
        }
    }
}

