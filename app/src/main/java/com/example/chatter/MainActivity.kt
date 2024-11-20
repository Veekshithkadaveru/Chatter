package com.example.chatter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.chatter.ui.theme.ChatterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChatterTheme {
                // A surface container using the 'background' color from the theme
                AppNavigation()
            }
        }
    }
}

