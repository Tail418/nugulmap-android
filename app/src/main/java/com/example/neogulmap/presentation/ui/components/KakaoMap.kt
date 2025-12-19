package com.example.neogulmap.presentation.ui.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.neogulmap.domain.model.Zone
import com.example.neogulmap.presentation.util.MapUtils
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
    currentLocation: Pair<Double, Double>,
    onZoneClick: (Zone) -> Unit = {},
    onMapLongClick: (LatLng) -> Unit = {},
    cameraMoveState: Int = 0
) {
    var mapInstance by remember { mutableStateOf<KakaoMap?>(null) }
    var isMapInitialized by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    // 1. Initial Map Setup (Run ONCE when map becomes ready)
    LaunchedEffect(mapInstance) {
        val map = mapInstance ?: return@LaunchedEffect
        
        if (!isMapInitialized) {
            val target = LatLng.from(currentLocation.first, currentLocation.second)
            map.moveCamera(CameraUpdateFactory.newCenterPosition(target))
            
            // Set up listeners
            map.setOnTerrainLongClickListener { _, latLng, _ ->
                onMapLongClick(latLng)
                Log.d("KakaoMap", "Map Long Clicked at: $latLng")
            }
            
            map.setOnLabelClickListener { _, _, label ->
                val clickedZone = label.tag as? Zone
                clickedZone?.let { onZoneClick(it) }
                true
            }
            
            isMapInitialized = true
            Log.d("KakaoMap", "Initial Camera Move to $target")
        }
    }

    // 2. User Triggered Move (Only when button is pressed)
    LaunchedEffect(cameraMoveState) {
        if (cameraMoveState > 0) { // Assume 0 is initial state, >0 means clicked
            mapInstance?.let { map ->
                val target = LatLng.from(currentLocation.first, currentLocation.second)
                map.moveCamera(CameraUpdateFactory.newCenterPosition(target))
                Log.d("KakaoMap", "User Triggered Move to $target")
            }
        }
    }
    
    // 3. Update markers when zones change (Does NOT move camera)
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
        
        // Resize bitmap if too large
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
        
        // NOTE: Camera movement logic removed from here to prevent fighting with user controls
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
                            // Removed overrides for getPosition/getZoomLevel to rely on explicit move logic
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
    }
}
