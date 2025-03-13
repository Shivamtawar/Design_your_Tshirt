package com.example.designyourt_shirt.uiscreens.StartPage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.designyourt_shirt.R




@Composable
fun GetStarted(
    navController: NavController
) {
    // Load the background image


    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Background Image
        Image(

            painter = painterResource(R.drawable.ic_design_1),
            contentDescription = null, // Decorative image, no need for content description
            modifier = Modifier.align(Alignment.Center)
                .background(Color.White)
                .fillMaxSize()// Crop the image to fill the screen
        )

        // Centered "Get Started" Button
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp,16.dp,16.dp,80.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Button(
                onClick = { navController.navigate("SignUp") },
                modifier = Modifier
                    .width(250.dp) // Set a fixed width to make it a bit square
                    .height(60.dp), // Set a fixed height to make it a bit square
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp) // Slightly rounded corners
            ) {
                Text(
                    text = "Get Started",
                    fontSize = 28.sp,
                    color = Color.White
                )
            }
        }
    }
}