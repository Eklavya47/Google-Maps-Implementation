package com.betanooblabs.googlemapsimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.betanooblabs.googlemapsimplementation.ui.theme.GoogleMapsImplementationTheme
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GoogleMapsImplementationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MapScreen(innerPadding)
                }
            }
        }
    }
}

@Composable
fun MapScreen(innerPadding: PaddingValues){
    var expanded by remember { mutableStateOf(false) }

    // Map type state
    var selectedMapType by remember { mutableStateOf("Normal") }

    val mapType = when (selectedMapType) {
        "Normal" -> MapType.NORMAL
        "Satellite" -> MapType.SATELLITE
        "Terrain" -> MapType.TERRAIN
        "Hybrid" -> MapType.HYBRID
        else -> MapType.NORMAL
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = MapProperties(
                mapType = mapType
            )
        )

        Box(
            modifier = Modifier
                .padding(16.dp)
                .background(Color.White, RoundedCornerShape(8.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                .clickable { expanded = true }
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .align(Alignment.TopStart)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(selectedMapType)
                Icon(painter = painterResource(R.drawable.ic_arrow_drop_down), contentDescription = null)
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                listOf("Normal", "Satellite", "Terrain", "Hybrid").forEach { type ->
                    DropdownMenuItem(
                        text = { Text(type) },
                        onClick = {
                            selectedMapType = type
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
