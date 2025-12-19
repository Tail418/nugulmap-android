package com.example.neogulmap.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.presentation.ui.components.KakaoMap
import com.example.neogulmap.presentation.util.MapUtils
import com.example.neogulmap.presentation.viewmodel.HomeViewModel

import com.example.neogulmap.presentation.ui.components.ProfileMenuItem
import com.example.neogulmap.presentation.ui.components.CurrentLocationButton // Import CurrentLocationButton
import com.example.neogulmap.presentation.ui.components.AddLocationModal // Import AddLocationModal
import com.google.android.gms.location.LocationServices // Import LocationServices
import com.google.android.gms.location.LocationRequest // Import LocationRequest
import com.google.android.gms.location.LocationCallback // Import LocationCallback
import com.google.android.gms.location.LocationResult // Import LocationResult
import com.google.android.gms.location.Priority // Import Priority for LocationRequest
import com.kakao.vectormap.LatLng // Import LatLng for map long click

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMenuItemClick: (ProfileMenuItem) -> Unit = {},
    onReportClick: () -> Unit = {}
) {
    val zones by viewModel.zones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val context = LocalContext.current
    
    // FusedLocationProviderClient
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    // State to hold current map center (initially Seoul, updated by actual location)
    var currentMapCenter by remember { mutableStateOf(Pair(37.5665, 126.9780)) } // Default: Seoul

    // State for AddLocationModal
    var showAddLocationModal by remember { mutableStateOf(false) }
    var newZoneLatLng by remember { mutableStateOf<LatLng?>(null) }


    // Permission launcher for location
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val isFineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val isCoarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val granted = isFineLocationGranted || isCoarseLocationGranted

        if (granted) {
            // Permission granted, immediately try to get last location or request new one
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    currentMapCenter = Pair(it.latitude, it.longitude)
                    viewModel.loadZones(it.latitude, it.longitude)
                } ?: run {
                    requestLocationUpdates(fusedLocationClient, context) { newLocation ->
                        currentMapCenter = Pair(newLocation.latitude, newLocation.longitude)
                        viewModel.loadZones(newLocation.latitude, newLocation.longitude)
                    }
                }
            }
        } else {
            Toast.makeText(context, "Location permission denied. Cannot update map to current location.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        // Initial load for zones using default lat/lon from ViewModel's loadZones()
        viewModel.loadZones()
    }
    
    LaunchedEffect(zones, isLoading) {
        if (!isLoading && zones.isEmpty() && errorMessage == null) { // Only show toast if no error message
            Toast.makeText(context, "No zones found.", Toast.LENGTH_SHORT).show()
        }
    }
    
    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    val sheetState = rememberModalBottomSheetState()

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMap(
            modifier = Modifier.fillMaxSize(),
            zones = zones,
            onZoneClick = { zone ->
                selectedZone = zone
            },
            onMapLongClick = { latLng ->
                newZoneLatLng = latLng
                showAddLocationModal = true
            },
            currentLocation = currentMapCenter // Pass current location to map
        )
        
        // Debug Status Text (Optional)
        /*
        Column(modifier = Modifier.align(Alignment.TopStart).background(androidx.compose.ui.graphics.Color.White.copy(alpha=0.7f)).padding(8.dp)) {
            Text("Zones: ${zones.size}")
        }
        */
        
        /*
        if (isLoading) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        }
        */
        
        errorMessage?.let { msg ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Error: $msg", color = androidx.compose.ui.graphics.Color.Red)
                    Button(onClick = { 
                        // Retry with current map center (Seoul default or last known)
                        viewModel.loadZones(currentMapCenter.first, currentMapCenter.second)
                    }) {
                        Text("Retry")
                    }
                }
            }
        }
        
        // Current Location Button (bottom end)
        CurrentLocationButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 80.dp, end = 16.dp), // Adjust padding to make space for Add button
            onCurrentLocationClick = {
                val fineLocationGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
                val coarseLocationGranted = ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (fineLocationGranted || coarseLocationGranted) {
                    fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                        location?.let {
                            currentMapCenter = Pair(it.latitude, it.longitude)
                            viewModel.loadZones(it.latitude, it.longitude)
                        } ?: run {
                            requestLocationUpdates(fusedLocationClient, context) { newLocation ->
                                currentMapCenter = Pair(newLocation.latitude, newLocation.longitude)
                                viewModel.loadZones(newLocation.latitude, newZoneLatLng = null)
                            }
                        }
                    }
                } else {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            }
        )
        
        // Add Smoking Zone Floating Action Button (bottom end, above current location button)
        FloatingActionButton(
            onClick = { 
                newZoneLatLng = null // Reset LatLng for manual input or map long-press
                showAddLocationModal = true 
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Smoking Zone")
        }

        if (selectedZone != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedZone = null },
                sheetState = sheetState
            ) {
                // Sheet Content matching Frontend style
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val zone = selectedZone!!
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = zone.address ?: "주소 정보 없음",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { selectedZone = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = zone.description ?: "설명 없음",
                        style = MaterialTheme.typography.bodyMedium,
                        color = androidx.compose.ui.graphics.Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ZoneTag(text = zone.type, color = MaterialTheme.colorScheme.primaryContainer)
                        zone.subtype?.let { 
                            ZoneTag(text = it, color = MaterialTheme.colorScheme.secondaryContainer) 
                        }
                        ZoneTag(text = zone.region, color = androidx.compose.ui.graphics.Color.LightGray)
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Button(
                        onClick = {
                            MapUtils.openKakaoMap(context, zone.latitude, zone.longitude)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("카카오맵에서 열기")
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showAddLocationModal) {
            AddLocationModal(
                initialLatLng = newZoneLatLng,
                onDismiss = { showAddLocationModal = false },
                onAddLocation = { lat, lon, name, address, type, imageUri ->
                    viewModel.createZone(lat, lon, name, address, type, "current_user_id", imageUri) // TODO: Get actual user ID
                    showAddLocationModal = false
                }
            )
        }
    }
}

// Helper function to request location updates
private fun requestLocationUpdates(
    fusedLocationClient: com.google.android.gms.location.FusedLocationProviderClient,
    context: android.content.Context,
    onLocationResult: (android.location.Location) -> Unit
) {
    try {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 10000
        ).setMinUpdateIntervalMillis(5000)
            .build()

        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let {
                    onLocationResult(it)
                    fusedLocationClient.removeLocationUpdates(this) // Remove updates after getting one location
                }
            }
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    } catch (e: SecurityException) {
        Toast.makeText(context, "Location permission not granted.", Toast.LENGTH_SHORT).show()
    }
}


@Composable
fun ZoneTag(text: String, color: androidx.compose.ui.graphics.Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(color = color, shape = MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}