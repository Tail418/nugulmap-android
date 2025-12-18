package com.example.neogulmap.presentation.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import com.example.neogulmap.presentation.viewmodel.AddLocationViewModel
import com.example.neogulmap.presentation.viewmodel.AddLocationUiState
import com.example.neogulmap.ui.theme.NeogulmapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationModal(
    viewModel: AddLocationViewModel = hiltViewModel(),
    onDismiss: () -> Unit,
    onLocationAdded: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onImageSelected(it) }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.95f),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "신규 흡연구역 등록",
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Address Search
                OutlinedTextField(
                    value = uiState.address,
                    onValueChange = viewModel::onAddressChanged,
                    label = { Text("주소 검색") },
                    placeholder = { Text("흡연구역 주소를 입력하거나 현재 위치 가져오기") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(onClick = { /* TODO: Implement address search logic */ }) {
                            Icon(Icons.Default.Search, contentDescription = "검색")
                        }
                    },
                    leadingIcon = {
                        IconButton(onClick = { /* TODO: Implement current location fill */ }) {
                            Icon(Icons.Default.LocationOn, contentDescription = "현재 위치")
                        }
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Detailed Description
                OutlinedTextField(
                    value = uiState.description,
                    onValueChange = viewModel::onDescriptionChanged,
                    label = { Text("상세 설명") },
                    placeholder = { Text("흡연구역에 대한 상세 정보를 입력하세요 (예: 크기, 시설, 이용 시간)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    maxLines = 5
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Image Upload
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "사진 추가", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = { imagePicker.launch("image/*") }) {
                        Icon(Icons.Default.AddAPhoto, contentDescription = "사진 추가")
                    }
                }
                if (uiState.imageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Image(
                        painter = rememberAsyncImagePainter(uiState.imageUri),
                        contentDescription = "Uploaded Image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Zone Type/Size selection (Placeholder for now)
                OutlinedTextField(
                    value = uiState.zoneType,
                    onValueChange = viewModel::onZoneTypeChanged,
                    label = { Text("구역 타입 (예: 실내, 실외)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = uiState.zoneSize,
                    onValueChange = viewModel::onZoneSizeChanged,
                    label = { Text("구역 크기 (예: 소형, 중형, 대형)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.submitLocation()
                        onLocationAdded() // Close modal on submission for now
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = uiState.canSubmit,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("등록하기")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddLocationModalPreview() {
    NeogulmapTheme {
        AddLocationModal(onDismiss = {}, onLocationAdded = {})
    }
}
