package com.example.neogulmap.presentation.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Scaffold // Added import
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.presentation.ui.components.KakaoMap
import com.example.neogulmap.presentation.ui.components.SearchBar
import com.example.neogulmap.presentation.ui.components.FloatingUserProfile
import com.example.neogulmap.presentation.ui.components.CurrentLocationButton
import com.example.neogulmap.presentation.ui.components.AddLocationModal
import com.example.neogulmap.presentation.ui.components.ProfileMenuItem
import com.example.neogulmap.presentation.util.MapUtils
import com.example.neogulmap.presentation.viewmodel.HomeUiState
import com.example.neogulmap.presentation.viewmodel.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onMenuItemClick: (ProfileMenuItem) -> Unit // Changed from onProfileClick
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var isLocationPermissionGranted by remember {
        mutableStateOf<Boolean>(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        isLocationPermissionGranted = isGranted
        if (isGranted) {
            viewModel.moveToCurrentLocation() // Move to current location if permission granted
        }
    }

    var selectedZone by remember { mutableStateOf<Zone?>(null) }
    var showAddLocationModal by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        if (!isLocationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        viewModel.loadZones(latitude = 37.5665, longitude = 126.9780) // Initial load of zones for a default location
    }

    LaunchedEffect(isLocationPermissionGranted) {
        if (isLocationPermissionGranted) {
            viewModel.moveToCurrentLocation()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is HomeUiState.Error -> {
                    Text(
                        text = "Error: ${state.message}",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                }
                is HomeUiState.Success -> {
                    KakaoMap(
                        modifier = Modifier.fillMaxSize(),
                        zones = state.zones.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                                    (it.address?.contains(searchQuery, ignoreCase = true) == true)
                        },
                        onZoneClick = { zone ->
                            selectedZone = zone
                        },
                        currentLocation = currentLocation // Pass current location to map
                    )

                    // Floating Search Bar
                    SearchBar(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                            .fillMaxWidth(),
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        onSearch = { query ->
                            searchQuery = query
                        }
                    )

                    // Floating User Profile Button
                    FloatingUserProfile(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .statusBarsPadding()
                            .padding(top = 8.dp, end = 16.dp),
                        onMenuItemClick = onMenuItemClick // Pass new menu item click handler
                    )

                    // Current Location Button
                    CurrentLocationButton(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .navigationBarsPadding()
                            .padding(start = 16.dp, bottom = 16.dp),
                        onCurrentLocationClick = {
                            if (isLocationPermissionGranted) {
                                viewModel.moveToCurrentLocation()
                            } else {
                                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                            }
                        }
                    )

                    // Floating Action Button for adding new locations
                    FloatingActionButton(
                        onClick = { showAddLocationModal = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(end = 16.dp, bottom = 16.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add new location")
                    }

                    // Zone Detail Card
                    selectedZone?.let { zone ->
                        Card(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(16.dp)
                                .clickable { MapUtils.openKakaoMap(context, zone.latitude, zone.longitude) },
                            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = zone.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "주소: ${zone.address ?: "정보 없음"}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                zone.description?.let {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "설명: $it",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Add Location Modal
            if (showAddLocationModal) {
                AddLocationModal(
                    onDismiss = { showAddLocationModal = false },
                    onLocationAdded = {
                        showAddLocationModal = false
                        viewModel.loadZones(latitude = 37.5665, longitude = 126.9780)
                    }
                )
            }
        }
    }
}
