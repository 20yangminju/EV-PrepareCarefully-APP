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
fun PrivacyPolicyScreen(navController: NavHostController) {
    // 임의의 개인정보 처리 방침 내용
    val privacyPolicyContent = """
        제1조 (개인정보의 처리 목적)
        회사는 다음의 목적을 위하여 개인정보를 처리합니다. 처리된 개인정보는 다음 목적 이외의 용도로는 사용되지 않습니다.
        1. 서비스 제공: EV-Watcher 서비스와 관련한 사용자 식별 및 인증
        2. 고객 문의 대응: 고객의 문의사항에 신속하게 답변 제공
        3. 법적 의무 이행: 관련 법령에 따라 필요한 경우 정보 제출

        제2조 (개인정보의 처리 및 보유 기간)
        회사는 개인정보를 수집한 목적이 달성되거나, 법적 보유 기간이 만료되었을 때 해당 정보를 즉시 파기합니다.

        제3조 (개인정보의 제3자 제공)
        회사는 원칙적으로 개인정보를 외부에 제공하지 않습니다. 단, 다음과 같은 경우 예외로 처리됩니다.
        1. 법령에 따른 요구가 있는 경우
        2. 사용자의 동의가 있는 경우

        제4조 (개인정보의 보호 조치)
        회사는 개인정보의 안전성을 확보하기 위해 다음과 같은 조치를 취합니다:
        1. 데이터 암호화
        2. 접근 권한 제한
        3. 정기적인 보안 점검
        
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
                        text = "개인 정보 처리 방침",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Colors.Title // 제목 텍스트 색상
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
                    text = privacyPolicyContent,
                    fontSize = 16.sp,
                    color = Colors.Text, // 본문 텍스트 색상
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}
