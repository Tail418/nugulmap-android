package com.example.neogulmap.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.neogulmap.presentation.viewmodel.TermsViewModel
import com.example.neogulmap.ui.theme.NeogulmapTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermsScreen(
    viewModel: TermsViewModel = hiltViewModel(),
    onTermsAgreed: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("약관 동의") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Agree to All
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("모든 약관에 동의합니다.", style = MaterialTheme.typography.titleMedium)
                Checkbox(
                    checked = uiState.agreeAll,
                    onCheckedChange = viewModel::onAgreeAllChanged,
                    colors = CheckboxDefaults.colors(
                        checkedColor = MaterialTheme.colorScheme.primary,
                        uncheckedColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Divider(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            // Service Terms (Required)
            TermsItem(
                title = "서비스 이용약관 (필수)",
                checked = uiState.agreeServiceTerms,
                onCheckedChange = { viewModel.onServiceTermsChanged(it) },
                isMandatory = true,
                termsContent = "서비스 이용약관 내용입니다. 여기에 길고 긴 약관 내용이 들어갑니다..."
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Privacy Policy (Required)
            TermsItem(
                title = "개인정보 처리방침 (필수)",
                checked = uiState.agreePrivacyPolicy,
                onCheckedChange = { viewModel.onPrivacyPolicyChanged(it) },
                isMandatory = true,
                termsContent = "개인정보 처리방침 내용입니다. 여기에 길고 긴 개인정보 처리방침 내용이 들어갑니다..."
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Marketing Consent (Optional)
            TermsItem(
                title = "마케팅 정보 수신 (선택)",
                checked = uiState.agreeMarketing,
                onCheckedChange = { viewModel.onMarketingChanged(it) },
                isMandatory = false,
                termsContent = "마케팅 정보 수신 동의 내용입니다. 여기에 길고 긴 마케팅 정보 수신 동의 내용이 들어갑니다..."
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onTermsAgreed,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 16.dp),
                enabled = uiState.canProceed,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Text("확인")
            }
        }
    }
}

@Composable
fun TermsItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isMandatory: Boolean,
    termsContent: String
) {
    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = checked,
                onCheckedChange = { if (!isMandatory || it) onCheckedChange(it) }, // Prevent unchecking mandatory terms
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(title, style = MaterialTheme.typography.bodyLarge)
        }
        TextButton(onClick = { showDialog = true }) {
            Text("내용 보기")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(title) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    Text(termsContent)
                }
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("닫기")
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TermsScreenPreview() {
    NeogulmapTheme {
        TermsScreen(onTermsAgreed = {})
    }
}