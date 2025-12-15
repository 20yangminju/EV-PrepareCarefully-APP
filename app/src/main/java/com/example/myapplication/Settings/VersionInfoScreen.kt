package com.example.myapplication.screens

import androidx.compose.foundation.layout.*
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
fun VersionInfoScreen(navController: NavHostController) {
    // 임의의 데이터
    val appName = "EV-Watcher"
    val version = "2.0.1"
    val lastUpdated = "2024-11-30"

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
                            tint = Colors.IconButton // 화살표 색상
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp)) // 아이콘과 타이틀 간 간격
                    Text(
                        text = "버전 정보",
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

            // 본문 내용
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 앱 이름
                Text(
                    text = "앱 이름: $appName",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Colors.Title, // 제목 텍스트 색상
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 현재 버전
                Text(
                    text = "현재 버전: $version",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.Text // 일반 텍스트 색상
                )

                // 최근 업데이트 날짜
                Text(
                    text = "최근 업데이트: $lastUpdated",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.Text // 일반 텍스트 색상
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 부가 설명
                Text(
                    text = "이 버전에서는 다음과 같은 기능이 포함되었습니다:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Colors.Text // 일반 텍스트 색상
                )
                Text(
                    text = "- 충전소 검색 및 길찾기 기능 추가\n" +
                            "- 배터리 온도 모니터링 개선\n" +
                            "- UI 최적화 및 안정성 향상",
                    fontSize = 14.sp,
                    color = Colors.Text // 일반 텍스트 색상
                )
            }
        }
    }
}
