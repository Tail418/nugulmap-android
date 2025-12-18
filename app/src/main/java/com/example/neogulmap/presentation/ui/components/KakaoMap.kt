package com.example.neogulmap.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.presentation.util.MapUtils
import com.google.android.gms.location.LocationServices
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraPosition
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

@Composable
fun KakaoMap(
    modifier: Modifier = Modifier,
    zones: List<Zone>,
    isLocationPermissionGranted: Boolean = false,
    onZoneClick: (Zone) -> Unit = {}
) {
    var mapInstance by remember { mutableStateOf<KakaoMap?>(null) }
    val context = LocalContext.current
    
    // Update markers when zones change or map becomes ready
    LaunchedEffect(mapInstance, zones) {
        val map = mapInstance ?: return@LaunchedEffect
        val labelManager = map.labelManager ?: return@LaunchedEffect
        
        // Explicitly create a layer with a unique ID
        val layerId = "zone_layer"
        var layer = labelManager.getLayer(layerId)
        if (layer == null) {
            layer = labelManager.addLayer(LabelLayerOptions.from(layerId))
        }
        
        // Clear existing labels
        layer?.removeAll()
        
        // Use Nugul Logo as marker
        val bitmap = BitmapFactory.decodeResource(context.resources, com.example.neogulmap.R.drawable.ic_marker_nugul)
        
        // Resize bitmap if too large (Optional, but good practice for map markers)
        val scaledBitmap = if (bitmap != null) {
            val size = 100 // Target size in pixels
            Bitmap.createScaledBitmap(bitmap, size, size, true)
        } else {
            MapUtils.createRedMarkerBitmap(context)
        }
        
        // Create style
        val styles = labelManager.addLabelStyles(
            LabelStyles.from(LabelStyle.from(scaledBitmap))
        )
        
        zones.forEach { zone ->
            try {
                // (Optional: If you want to distinguish types, you can create multiple styles)
                
                val latLng = LatLng.from(zone.latitude, zone.longitude)
                val options = LabelOptions.from(latLng)
                    .setStyles(styles)
                    .setClickable(true)
                    .setTag(zone)
                
                layer?.addLabel(options)
                Log.d("KakaoMap", "Added marker at ${zone.latitude}, ${zone.longitude}")
            } catch (e: Exception) {
                Log.e("KakaoMap", "Error adding label: ${e.message}")
            }
        }
        
        // Move camera to the first zone if available
        if (zones.isNotEmpty()) {
            val firstZone = zones[0]
            val targetLatLng = LatLng.from(firstZone.latitude, firstZone.longitude)
            
            // Zoom level 12 for wider view
            val cameraPosition = CameraPosition.from(
                targetLatLng.latitude, 
                targetLatLng.longitude, 
                12, 
                0.0, 
                0.0, 
                0.0
            )
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            Log.d("KakaoMap", "Moved camera to Lat: ${firstZone.latitude}, Lng: ${firstZone.longitude} with zoom 12")
        }
        
        // Set listener
        map.setOnLabelClickListener { kakaoMap, layer, label ->
            val clickedZone = label.tag as? Zone
            clickedZone?.let { onZoneClick(it) }
            true
        }
    }
    
    Box(modifier = modifier) {
        AndroidView(
            modifier = Modifier.matchParentSize(),
            factory = { context ->
                Log.d("KakaoMap", "Initializing MapView")
                val mapView = MapView(context)
                try {
                    mapView.start(
                        object : MapLifeCycleCallback() {
                            override fun onMapDestroy() {
                                Log.d("KakaoMap", "Map Destroyed")
                            }
                            override fun onMapError(error: Exception?) {
                                Log.e("KakaoMap", "Map Error: ${error?.message}")
                                error?.printStackTrace()
                            }
                        },
                        object : KakaoMapReadyCallback() {
                            override fun onMapReady(kakaoMap: KakaoMap) {
                                Log.d("KakaoMap", "Map Ready")
                                mapInstance = kakaoMap
                            }
                        }
                    )
                } catch (e: Exception) {
                    Log.e("KakaoMap", "Error starting map: ${e.message}")
                    e.printStackTrace()
                }
                mapView
            },
            update = { mapView ->
                // Update logic if needed for view properties
            }
        )
        
        // Location Button
        FloatingActionButton(
            onClick = {
                if (isLocationPermissionGranted && mapInstance != null) {
                    try {
                        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val cameraPosition = CameraPosition.from(
                                    location.latitude,
                                    location.longitude,
                                    15, // Zoom level 15 for My Location
                                    0.0, 0.0, 0.0
                                )
                                mapInstance?.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                                Log.d("KakaoMap", "Moved to my location: ${location.latitude}, ${location.longitude}")
                            } else {
                                Toast.makeText(context, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: SecurityException) {
                        Log.e("KakaoMap", "Security Exception: ${e.message}")
                    }
                } else {
                    Toast.makeText(context, "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(Icons.Default.Place, contentDescription = "My Location")
        }
    }
}
