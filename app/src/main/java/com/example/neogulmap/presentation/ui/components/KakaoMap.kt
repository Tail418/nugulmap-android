package com.example.neogulmap.presentation.ui.components

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.neogulmap.domain.model.Zone
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles

@Composable
fun KakaoMap(
    modifier: Modifier = Modifier,
    zones: List<Zone>,
    onZoneClick: (Zone) -> Unit = {}
) {
    var mapInstance by remember { mutableStateOf<KakaoMap?>(null) }
    
    // Update markers when zones change or map becomes ready
    LaunchedEffect(mapInstance, zones) {
        val map = mapInstance ?: return@LaunchedEffect
        val labelManager = map.labelManager ?: return@LaunchedEffect
        // Try to get existing layer or add a default one. 
        // Note: API might require explicit layer creation.
        val layer = labelManager.layer
        
        // Define a simple style
        val styles = labelManager
            .addLabelStyles(LabelStyles.from(LabelStyle.from(com.example.neogulmap.R.drawable.ic_launcher_foreground)))
        
        zones.forEach { zone ->
            try {
                val options = LabelOptions.from(LatLng.from(zone.latitude, zone.longitude))
                    .setStyles(styles)
                    .setClickable(true)
                    .setTag(zone)
                
                layer?.addLabel(options)
            } catch (e: Exception) {
                Log.e("KakaoMap", "Error adding label: ${e.message}")
            }
        }
        
        // Set listener
        map.setOnLabelClickListener { kakaoMap, layer, label ->
            val clickedZone = label.tag as? Zone
            clickedZone?.let { onZoneClick(it) }
            true
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { context ->
            val mapView = MapView(context)
            mapView.start(
                object : MapLifeCycleCallback() {
                    override fun onMapDestroy() {
                        // Handle map destroy
                        Log.d("KakaoMap", "Map Destroyed")
                    }
                    override fun onMapError(error: Exception?) {
                        Log.e("KakaoMap", "Map Error: ${error?.message}")
                    }
                },
                object : KakaoMapReadyCallback() {
                    override fun onMapReady(kakaoMap: KakaoMap) {
                        Log.d("KakaoMap", "Map Ready")
                        mapInstance = kakaoMap
                        
                        // Enable Tracking if needed (requires permission check outside)
                        // kakaoMap.trackingManager?.startTracking(mapView.trackingManager)
                    }
                }
            )
            mapView
        },
        update = { mapView ->
            // Update logic if needed for view properties
        }
    )
}
