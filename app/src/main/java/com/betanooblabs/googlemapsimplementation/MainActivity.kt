package com.betanooblabs.googlemapsimplementation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.betanooblabs.googlemapsimplementation.ui.theme.GoogleMapsImplementationTheme
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import com.betanooblabs.googlemapsimplementation.BuildConfig

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (!Places.isInitialized()) {
            Places.initialize(
                applicationContext,
                BuildConfig.Google_Maps_API_Key
            )
        }
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
    val coroutineScope = rememberCoroutineScope()


    // Map type state
    var selectedMapType by remember { mutableStateOf("Normal") }

    val cameraPositionState = rememberCameraPositionState()

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
            cameraPositionState = cameraPositionState,
            properties = MapProperties(
                mapType = mapType
            )
        )

        Row(
            modifier = Modifier
                .align(Alignment.TopCenter),
                //.padding(16.dp),
            horizontalArrangement = Arrangement.Start
        ){
            // Map Type Dropdown
            MapTypeDropdown(
                selectedMapType = selectedMapType,
                expanded = expanded,
                onExpandChange = { expanded = it },
                onTypeSelected = {
                    selectedMapType = it
                    expanded = false
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    //.padding(end = 8.dp)
            )

            // Search Bar
            PlaceSearchBar(
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                onPlaceSelected = { latLng ->
                    coroutineScope.launch {
                        cameraPositionState.animate(
                            update = CameraUpdateFactory.newLatLngZoom(latLng, 15f),
                            durationMs = 1000
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun MapTypeDropdown(
    selectedMapType: String,
    expanded: Boolean,
    onExpandChange: (Boolean) -> Unit,
    onTypeSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { onExpandChange(true) }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(selectedMapType)
            Icon(
                painter = painterResource(R.drawable.ic_arrow_drop_down),
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandChange(false) }
        ) {
            listOf("Normal", "Satellite", "Terrain", "Hybrid").forEach {
                DropdownMenuItem(
                    text = { Text(it) },
                    onClick = { onTypeSelected(it) }
                )
            }
        }
    }
}


@Composable
fun PlaceSearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (LatLng) -> Unit
) {
    val context = LocalContext.current
    val placesClient = remember { Places.createClient(context) }

    var query by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }

    Column(
        modifier = modifier
            .padding(end = 10.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(50.dp))
            .background(Color.White, RoundedCornerShape(50.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(50.dp))
    ) {

        TextField(
            value = query,
            onValueChange = {
                query = it
                if (it.isNotEmpty()) {
                    val request = FindAutocompletePredictionsRequest.builder()
                        .setQuery(it)
                        .build()

                    placesClient.findAutocompletePredictions(request)
                        .addOnSuccessListener { response ->
                            predictions = response.autocompletePredictions
                        }
                } else {
                    predictions = emptyList()
                }
            },
            placeholder = { Text("Search places...") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
        )

        predictions.forEach { prediction ->
            Text(
                text = prediction.getFullText(null).toString(),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        query = prediction.getFullText(null).toString()
                        predictions = emptyList()

                        val placeRequest = FetchPlaceRequest.builder(
                            prediction.placeId,
                            listOf(Place.Field.LAT_LNG)
                        ).build()

                        placesClient.fetchPlace(placeRequest)
                            .addOnSuccessListener { response ->
                                response.place.latLng?.let {
                                    onPlaceSelected(it)
                                }
                            }
                    }
                    .padding(12.dp)
                    .background(Color.White)
            )
        }
    }
}

