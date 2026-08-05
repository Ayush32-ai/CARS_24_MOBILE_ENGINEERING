package com.example.cars24sdui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.cars24sdui.renderer.ScreenRenderer
import com.example.cars24sdui.model.Screen
import com.example.cars24sdui.model.Component
import com.example.cars24sdui.ui.theme.Cars24SDUITheme

/**
 * Static version of the Home Screen for benchmarking purposes.
 * This skips JSON parsing and uses hardcoded data.
 */
class StaticMainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Cars24SDUITheme {
                StaticApp()
            }
        }
    }
}

@Composable
fun StaticApp() {
    val staticScreen = getStaticHomeData()
    Scaffold(
        bottomBar = { /* Reuse BottomNav if needed */ }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            ScreenRenderer(staticScreen, onAction = {})
        }
    }
}

fun getStaticHomeData(): Screen {
    return Screen(
        components = listOf(
            Component("homeHeader", mapOf("location" to "New Delhi", "placeholder" to "Search Baleno")),
            Component("tileRail", mapOf("title" to "Buy car", "items" to listOf(mapOf("title" to "All used cars")))),
            Component("carRail", mapOf("title" to "Used cars you'll love", "items" to listOf(
                mapOf("name" to "2015 Hyundai Eon", "price" to "₹1.72 lakh", "image" to "eon")
            )))
            // Simplified for brevity in benchmarking
        )
    )
}
