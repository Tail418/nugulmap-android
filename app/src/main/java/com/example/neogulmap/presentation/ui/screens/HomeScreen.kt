package com.example.neogulmap.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.presentation.ui.components.KakaoMap
import com.example.neogulmap.presentation.util.MapUtils
import com.example.neogulmap.presentation.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val zones by viewModel.zones.collectAsState()
    val context = LocalContext.current
    
    var isLocationPermissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        isLocationPermissionGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }
    
    var selectedZone by remember { mutableStateOf<Zone?>(null) }

    LaunchedEffect(Unit) {
        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        KakaoMap(
            modifier = Modifier.fillMaxSize(),
            zones = zones,
            onZoneClick = { zone ->
                selectedZone = zone
            }
        )
        
        if (zones.isEmpty()) {
             Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                 CircularProgressIndicator()
             }
        }
        
        selectedZone?.let { zone ->
            AlertDialog(
                onDismissRequest = { selectedZone = null },
                title = { Text(text = zone.type) },
                text = { 
                    Column {
                        Text(text = "Region: ${zone.region}")
                        zone.description?.let { Text(text = it) }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            MapUtils.openKakaoMap(context, zone.latitude, zone.longitude)
                        }
                    ) {
                        Text("Open in KakaoMap")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedZone = null }) {
                        Text("Close")
                    }
                }
            )
        }
    }
}
