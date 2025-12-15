package com.example.myapplication


import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.core.app.NotificationCompat
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.myapplication.resource.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// 화면에 뜨는 알림 UI
@Composable
fun ShowTemperatureDialog(showDialog: Boolean, onDismiss: () -> Unit, isToHot: Boolean) {
    var showHelpDialog by remember { mutableStateOf(false) } // 도움말 다이얼로그 상태

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { onDismiss() },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "경고 아이콘",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "이상 온도 발생",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.Default.Info, // 정보 아이콘 추가
                        contentDescription = "도움말",
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { showHelpDialog = true } // 클릭 시 도움말 다이얼로그 표시
                    )
                }
            },
            text = {
                Text(
                    text = "배터리 이상온도가 감지되었습니다. 차량을 확인해주세요.",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(onClick = { onDismiss() }) {
                    Text("닫기")
                }
            },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )
    }

    // 도움말 다이얼로그
    HelpDialog(showHelp = showHelpDialog, onDismiss = { showHelpDialog = false }, isToHot = isToHot)
}


// 조건에 맞춰 알림이 뜨도록 함(트리거) → 상태 표시줄에 적용
fun createNotification(context: Context, viewModel: NotificationViewModel, status: Int) {
    val channelId = "channel_id"
    val channelName = "Default Channel"
    val notificationText = when {
        // 각 경우마다 다른 알림 출력
        status == 0 -> "이상온도가 감지되었습니다. 차량을 확인해주세요."
        status == 1 -> "충전기가 분리되었습니다. 차량을 확인해주세요."
        status == 2 -> "셀 밸런스가 틀어졌습니다. 완속 충전을 권장합니다."
        status == 3 -> "충전을 일정 이상 하셨습니다. 배터리 효율을 위해 완속 충전을 권장합니다."// n회 이상 충전 시 완속 충전 권장 알림
        else -> ""
    }
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
        val channel =
            // 헤드업 알림이 뜨게끔 중요도 설정
            NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
                enableVibration(true)
            }
        notificationManager.createNotificationChannel(channel)
    }

    val notificationId = 1

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(R.drawable.baseline_warning_24) // 알림 앱 아이콘
        .setContentTitle("EV-Watcher") // 앱 이름 설정
        .setContentText(notificationText) // 임시 텍스트
        .setPriority(NotificationCompat.PRIORITY_HIGH) // 헤드업 알림이 뜨게끔 설정
        .setAutoCancel(true)
        .build()

    notificationManager.notify(notificationId, notification)

    // 알림 내역 저장
    val currentTimeMillis = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd, HH:mm:ss", Locale.getDefault())
    val formattedTime = dateFormat.format(currentTimeMillis)

    viewModel.addNotification(
        icon = R.drawable.baseline_warning_24,
        title = notificationText,
        date = formattedTime
    )
}