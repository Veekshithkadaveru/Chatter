package com.example.chatter

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.chatter.feature.auth.signin.SignInScreen
import com.example.chatter.feature.auth.signup.SignUpScreen
import com.example.chatter.feature.chat.ChatScreen
import com.example.chatter.feature.home.HomeScreen
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavigation() {
    Surface(modifier = Modifier.fillMaxSize()) {

        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        val start = if (currentUser != null) "home" else "signin"

        NavHost(navController = navController, startDestination = start) {
            composable("signin") {
                SignInScreen(navController)
            }
            composable("signup") {
                SignUpScreen(navController)
            }
            composable("home") {
                HomeScreen(navController)
            }
            composable("chat/{channelId}", arguments = listOf(
                navArgument("channelId") {
                    type = NavType.StringType
                }
            )) {
                val channelId = it.arguments?.getString("channelId") ?: ""
                ChatScreen(navController = navController, channelId = channelId)
            }
        }
    }
}
