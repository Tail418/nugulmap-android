package com.example.neogulmap.presentation.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neogulmap.ui.theme.NeogulmapTheme

enum class ProfileMenuItem {
    MY_INFO, SETTINGS, ANNOUNCEMENTS, LOGOUT
}

@Composable
fun FloatingUserProfile(modifier: Modifier = Modifier, onMenuItemClick: (ProfileMenuItem) -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        FloatingActionButton(
            onClick = { expanded = true },
            modifier = Modifier.size(56.dp),
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            // Placeholder for user profile image
            Icon(
                imageVector = Icons.Default.Person, // Placeholder icon
                contentDescription = "User Profile",
                tint = MaterialTheme.colorScheme.primary // Use primary color for visibility
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("내 정보") },
                onClick = {
                    onMenuItemClick(ProfileMenuItem.MY_INFO)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("설정") },
                onClick = {
                    onMenuItemClick(ProfileMenuItem.SETTINGS)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("공지사항") },
                onClick = {
                    onMenuItemClick(ProfileMenuItem.ANNOUNCEMENTS)
                    expanded = false
                }
            )
            DropdownMenuItem(
                text = { Text("로그아웃") },
                onClick = {
                    onMenuItemClick(ProfileMenuItem.LOGOUT)
                    expanded = false
                }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingUserProfilePreview() {
    NeogulmapTheme {
        FloatingUserProfile(onMenuItemClick = {})
    }
}