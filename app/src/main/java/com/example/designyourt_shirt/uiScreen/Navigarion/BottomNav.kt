package com.example.designyourt_shirt.uiScreen.Navigarion



import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.designyourt_shirt.uiScreen.Favorite.FavoritesScreen
import com.example.designyourt_shirt.uiScreen.HomeScreen.HomeScreen
import com.example.designyourt_shirt.Model.NavItems
import com.example.designyourt_shirt.uiScreen.Profile.ProfileScreen
import com.example.designyourt_shirt.uiScreen.Tshirt_Design.DesignLandingPage

@Composable
fun BottomNav(
    navController: NavController
) {

    val NavItemslist = listOf(
        NavItems("Home", Icons.Default.Home),
        NavItems("Customize", Icons.Default.Palette),
        NavItems("Favorite", Icons.Default.FavoriteBorder),
        NavItems("Profile", Icons.Default.Person)


    )

    var selectedIndex = remember {
        mutableIntStateOf(0)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                NavItemslist.forEachIndexed { index, navItems ->
                    NavigationBarItem(
                        selected = selectedIndex.intValue == index ,
                        onClick = {
                            selectedIndex.intValue = index
                        },
                        icon = { Icon(navItems.icon, "") },
                        label = {
                            Text(text = navItems.label)
                        }
                    )
                }


            }
        }
    )
    { innerPadding ->
        contentScreen(modifier = Modifier.padding(innerPadding), selectedIndex.intValue, navController)

    }

}

@Composable
fun contentScreen(modifier: Modifier = Modifier, selectedIndex: Int, navController: NavController) {
    when(selectedIndex){
        0 -> HomeScreen(navController)
        1 -> DesignLandingPage(navController)
        2 -> FavoritesScreen(navController)
        3 -> ProfileScreen(navController)

    }

}