package com.example.neogulmap.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.neogulmap.ui.theme.NeogulmapTheme

private data class Announcement(
    val id: Int,
    val title: String,
    val date: String,
    val content: String
)

private val mockAnnouncements = listOf(
    Announcement(1, "서비스 정식 출시 안내", "2025-12-01", "너굴맵 서비스가 정식으로 출시되었습니다!"),
    Announcement(2, "서버 점검 안내 (오전 2시 - 4시)", "2025-11-28", "보다 안정적인 서비스 제공을 위해 서버 점검이 진행될 예정입니다."),
    Announcement(3, "개인정보처리방침 개정 안내", "2025-11-15", "개인정보처리방침이 일부 개정되어 안내드립니다."),
    Announcement(4, "너굴맵 v1.1 업데이트", "2025-11-10", "사용자 경험 개선 및 버그 수정 업데이트가 있었습니다."),
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementsScreen(onBackClick: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("공지사항") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(mockAnnouncements) { announcement ->
                AnnouncementItem(announcement)
            }
        }
    }
}

@Composable
private fun AnnouncementItem(announcement: Announcement) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = announcement.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = announcement.date,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnnouncementsScreenPreview() {
    NeogulmapTheme {
        AnnouncementsScreen(onBackClick = {})
    }
}
