package com.example.lastactive

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.example.lastactive.ui.HomeScreen
import com.example.lastactive.ui.HomeViewModel
import com.example.lastactive.ui.theme.LastActiveTheme

class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HomeViewModel>()
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LastActiveTheme {
                HomeScreen(viewModel = viewModel)
            }
        }
    }
}
