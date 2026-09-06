package com.example.splitter

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.splitter.ui.screens.MainScreen
import com.example.splitter.ui.theme.SplitterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplitterTheme {
                MainScreen()
            }
        }
    }
}

// so this is the main activity , just wanted to commit
