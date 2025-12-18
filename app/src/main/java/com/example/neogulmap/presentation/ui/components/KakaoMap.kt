package com.example.neogulmap.presentation.ui.components

import kotlinx.coroutines.channels.awaitClose
import com.kakao.vectormap.MapType
import com.kakao.vectormap.label.Label
import com.kakao.vectormap.label.LabelManager
import com.kakao.vectormap.mapwidget.InfoWindowOptions
import com.kakao.vectormap.mapwidget.infoWindow.InfoWindow
import com.kakao.vectormap.mapwidget.infoWindow.InfoWindowLayer
import com.kakao.vectormap.mapwidget.infoWindow.InfoWindowLayerOptions
import com.kakao.vectormap.mapwidget.infoWindow.InfoWindowOptions.InfoWindowAnchor
import com.kakao.vectormap.mapwidget.infoWindow.InfoWindowPosition
import com.kakao.vectormap.mapwidget.infoWindow.InfowindowContent
import com.kakao.vectormap.mapwidget.infoWindow.DefaultInfowindowContent
import com.kakao.vectormap.mapwidget.maptype.MapTypeControlOption
import com.kakao.vectormap.mapwidget.scale.ScaleBarControlOption
import com.kakao.vectormap.mapwidget.zoom.ZoomControlOption


@Composable
fun KakaoMap(
    modifier: Modifier = Modifier,
    zones: List<Zone>,
    currentLocation: Pair<Double, Double>, // Added currentLocation parameter
    onZoneClick: (Zone) -> Unit = {}
) {
    var mapInstance by remember { mutableStateOf<KakaoMap?>(null) }
    val context = LocalContext.current
    
    // LaunchedEffect to move camera to initial location once map is ready
    LaunchedEffect(mapInstance) {
        mapInstance?.let { map ->
            val targetLatLng = LatLng.from(currentLocation.first, currentLocation.second)
            val cameraPosition = CameraPosition.from(targetLatLng.latitude, targetLatLng.longitude, 12)
            map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
        }
    }
    
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
        
        // Move camera to the first zone if available (after initial load, or if currentLocation is updated)
        if (zones.isNotEmpty() && (map.cameraPosition.position.latitude == 0.0 && map.cameraPosition.position.longitude == 0.0)) { // Only move if map is at default position
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
        
        // The FloatingActionButton for current location is now in HomeScreen.kt
    }
}
