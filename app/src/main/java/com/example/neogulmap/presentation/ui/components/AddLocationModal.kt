package com.example.neogulmap.presentation.ui.components

import android.net.Uri
import android.widget.Toast // Added import for Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.presentation.viewmodel.AddLocationViewModel
import com.example.neogulmap.presentation.viewmodel.AddLocationFormState
import kotlinx.coroutines.launch
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationModal(
    isOpen: Boolean,
    onClose: () -> Unit
) {
    if (!isOpen) return

    val viewModel: AddLocationViewModel = hiltViewModel()
    val formState by viewModel.formState.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val selectImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        viewModel.onImageUriChange(uri)
    }

    // Effect to initialize form and get current location when modal opens
    LaunchedEffect(isOpen) {
        if (isOpen) {
            viewModel.clearForm() // Clear form when opened
            viewModel.getCurrentLocationForForm() // Fetch current location
        }
    }

    // Effect to close modal on successful zone creation
    LaunchedEffect(formState.showSuccess) {
        if (formState.showSuccess) {
            Toast.makeText(context, "흡연구역이 성공적으로 추가되었습니다.", Toast.LENGTH_SHORT).show()
            onClose()
        }
    }

    Dialog(onDismissRequest = onClose) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
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
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "닫기")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                // Address input with search button
                OutlinedTextField(
                    value = formState.address,
                    onValueChange = viewModel::onAddressChange,
                    label = { Text("주소") },
                    modifier = Modifier.fillMaxWidth(),
                    trailingIcon = {
                        IconButton(
                            onClick = { coroutineScope.launch { viewModel.searchAddress() } },
                            enabled = !formState.isGeocoding && !formState.isLoading
                        ) {
                            if (formState.isGeocoding) {
                                CircularProgressIndicator(Modifier.size(24.dp))
                            } else {
                                Icon(Icons.Default.Search, contentDescription = "주소 검색")
                            }
                        }
                    },
                    isError = formState.error != null
                )
                
                // Region input
                OutlinedTextField(
                    value = formState.region,
                    onValueChange = viewModel::onRegionChange,
                    label = { Text("지역") },
                    modifier = Modifier.fillMaxWidth(),
                    isError = formState.error != null // Simplified error display
                )

                // Type Dropdown
                var typeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = !typeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formState.type,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("유형") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = formState.error != null
                    )
                    ExposedDropdownMenu(
                        expanded = typeExpanded,
                        onDismissRequest = { typeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("지정구역") },
                            onClick = {
                                viewModel.onTypeChange("지정구역")
                                typeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("일반구역") },
                            onClick = {
                                viewModel.onTypeChange("일반구역")
                                typeExpanded = false
                            }
                        )
                    }
                }

                // Size Dropdown
                var sizeExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = sizeExpanded,
                    onExpandedChange = { sizeExpanded = !sizeExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = formState.size,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("크기") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sizeExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        isError = formState.error != null
                    )
                    ExposedDropdownMenu(
                        expanded = sizeExpanded,
                        onDismissRequest = { sizeExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("소형") },
                            onClick = {
                                viewModel.onSizeChange("소형")
                                sizeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("중형") },
                            onClick = {
                                viewModel.onSizeChange("중형")
                                sizeExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("대형") },
                            onClick = {
                                viewModel.onSizeChange("대형")
                                sizeExpanded = false
                            }
                        )
                    }
                }
                
                // Description input
                OutlinedTextField(
                    value = formState.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("상세 설명 (선택 사항)") },
                    modifier = Modifier.fillMaxWidth()
                )

                // Latitude and Longitude (disabled inputs)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    OutlinedTextField(
                        value = formState.latitude.toString(),
                        onValueChange = {},
                        label = { Text("위도") },
                        readOnly = true,
                        modifier = Modifier.weight(1f).padding(end = 4.dp)
                    )
                    OutlinedTextField(
                        value = formState.longitude.toString(),
                        onValueChange = {},
                        label = { Text("경도") },
                        readOnly = true,
                        modifier = Modifier.weight(1f).padding(start = 4.dp)
                    )
                }

                // Image input
                Button(
                    onClick = { selectImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !formState.isLoading && !formState.isGeocoding
                ) {
                    Text(text = if (formState.imageUri != null) "이미지 선택됨" else "이미지 선택")
                }

                // Error display
                formState.error?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth())
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onClose,
                        enabled = !formState.isLoading && !formState.isGeocoding,
                        colors = ButtonDefaults.outlinedButtonColors()
                    ) {
                        Text("취소")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            coroutineScope.launch {
                                // Dummy userId for now. Real implementation needs actual user ID.
                                viewModel.createZone("dummy-user-id")
                            }
                        },
                        enabled = !formState.isLoading && !formState.isGeocoding
                    ) {
                        if (formState.isLoading) {
                            CircularProgressIndicator(Modifier.size(24.dp), color = MaterialTheme.colorScheme.onPrimary)
                        } else if (formState.showSuccess) {
                            Icon(Icons.Default.Check, contentDescription = "성공")
                        } else {
                            Text("저장")
                        }
                    }
                }
            }
        }
    }
}
