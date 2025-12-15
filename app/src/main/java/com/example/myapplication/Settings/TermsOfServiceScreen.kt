package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.myapplication.ui.theme.Colors // 사용자 정의 Colors import

@Composable
fun TermsOfServiceScreen(navController: NavHostController) {
    // 더미 약관 내용
    val termsContent = """
        제1조 (목적)
        본 약관은 EV-Watcher(이하 "서비스")의 이용과 관련하여 회사와 이용자 간의 권리, 의무 및 책임사항을 규정함을 목적으로 합니다.

        제2조 (정의)
        1. "회사"란 EV-Watcher 서비스를 제공하는 주체를 의미합니다.
        2. "이용자"란 본 약관에 따라 서비스를 이용하는 고객을 의미합니다.

        제3조 (서비스 제공 및 변경)
        회사는 서비스의 제공 및 변경, 종료와 관련된 사항을 공지하며, 이용자는 이에 동의한 후 서비스를 이용할 수 있습니다.

        제4조 (이용자의 의무)
        1. 이용자는 법령 및 본 약관에서 규정한 사항을 준수하여야 합니다.
        2. 이용자는 서비스의 정상적인 운영을 방해하는 행위를 하여서는 안 됩니다.

        제5조 (개인정보 보호)
        회사는 이용자의 개인정보를 적법하게 보호하며, 관련 법령에 따라 개인정보를 관리합니다.
        
    """.trimIndent()

    Scaffold(
        backgroundColor = Colors.Background // 배경화면 색상
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Title 및 Divider
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(12.dp)) // 화면 위와 title 간격
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로 가기",
                            tint = Colors.IconButton // 화살표 아이콘 색상
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 타이틀 간 간격
                    Text(
                        text = "이용약관",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Colors.Title // 타이틀 텍스트 색상
                    )
                }
                Spacer(modifier = Modifier.height(12.dp)) // 화면 위와 title 간격
                Divider(
                    color = Colors.Divider,
                    thickness = 1.dp,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // 본문 텍스트
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()) // 스크롤 가능하도록 설정
            ) {
                Text(
                    text = termsContent,
                    fontSize = 16.sp,
                    color = Colors.Text, // 본문 텍스트 색상
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
