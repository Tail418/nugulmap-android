package com.example.neogulmap.presentation.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationSearching
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neogulmap.ui.theme.NeogulmapTheme

@Composable
fun CurrentLocationButton(modifier: Modifier = Modifier, onCurrentLocationClick: () -> Unit) {
    FloatingActionButton(
        onClick = onCurrentLocationClick,
        modifier = modifier.size(56.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Icon(
            imageVector = Icons.Default.LocationSearching,
            contentDescription = "Current Location",
            tint = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CurrentLocationButtonPreview() {
    NeogulmapTheme {
        CurrentLocationButton(onCurrentLocationClick = {})
    }
}