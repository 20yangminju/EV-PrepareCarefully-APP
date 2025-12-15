import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.myapplication.R
import com.example.myapplication.navigation.BottomNavigationBar
import com.example.myapplication.resource.CalendarApp
import com.example.myapplication.resource.LabelledBox
import com.example.myapplication.resource.NotificationViewModel
import com.example.myapplication.ui.theme.Colors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import com.example.myapplication.network.RetrofitInstance
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavController,
    onNavigateToSettings: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onNavigateAIscreen: () -> Unit,
    notificationViewModel: NotificationViewModel
) {
    var currentModuleIndex by remember { mutableStateOf(0) }
    var temperatureData by remember { mutableStateOf<List<TempResponse?>>(listOf(null, null, null, null)) }
    // 권장 완속 충전 날짜 설정
    val recommendedChargeDate = Calendar.getInstance().apply {
        add(Calendar.DAY_OF_YEAR, 30) // 30일 뒤의 날짜로 설정
    }
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val recommendedChargeDateText = dateFormatter.format(recommendedChargeDate.time) // 권장 완속 충전 날짜 → 추후 수정
    var currentDate by remember { mutableStateOf(LocalDate.now()) }


    // 주행 관련 변수 설정
    var batteryPercentage by remember { mutableStateOf(80) }
    val totalDistance by remember { mutableStateOf("1200 km") } // → 추후 수정
    val totalTime by remember { mutableStateOf("15:30:00") } // → 추후 수정
    val fuelEfficiency by remember { mutableStateOf("12.5 km/kWh") } // → 추후 수정

    LaunchedEffect(Unit) {
        try {
            // 배터리 상태 요청
            val statusResponse: StatusResponse = withContext(Dispatchers.IO) {
                RetrofitInstance.api.status(StatusRequest(device_number = "888777"))
            }

            // 배터리 충전 상태 업데이트
            withContext(Dispatchers.Main) {
                batteryPercentage = statusResponse.charging_percent // 서버에서 받아온 충전 퍼센트
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            val requests = listOf(
                TempRequest(device_number = "888777", 1),
                TempRequest(device_number = "888777", 2),
                TempRequest(device_number = "888777", 3),
                TempRequest(device_number = "888777", 4)
            )
            temperatureData = withContext(Dispatchers.IO) {
                requests.map { request ->
                    try {
                        RetrofitInstance.api.temperature(request)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        null
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // 2초마다 모듈 인덱스를 순환
        while (true) {
            delay(3000)
            currentModuleIndex = (currentModuleIndex + 1) % 4
        }
    }


    Scaffold(
        backgroundColor = Colors.Background,
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "EV-Watcher", color = Colors.Title) },
                    backgroundColor = Colors.Background,
                    actions = {
                        IconButton(onClick = { onNavigateAIscreen() }) {
                            Image(
                                painter = painterResource(id = R.drawable.baseline_assessment_24), // drawable 이미지 리소스
                                contentDescription = "AI 분석",
                                modifier = Modifier.size(24.dp), // 아이콘 크기 설정
                                alignment = Alignment.Center
                            )
                        }
                        IconButton(onClick = { onNavigateToSettings() }) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "환경설정",
                                tint = Colors.IconButton
                            )
                        }
                        IconButton(onClick = { onNavigateToNotifications() }) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "알림",
                                tint = Colors.IconButton
                            )
                        }
                    }
                )
                Divider(color = Colors.Divider, thickness = 1.dp)
                Spacer(modifier = Modifier.height(20.dp))
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentScreen = navController.currentDestination?.route ?: "main"
            )
        },
        content = { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    // 권장 완속 충전 날짜 정보
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Colors.Button, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "경고 아이콘",
                            tint = Colors.IconButton
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "권장 완속 충전 날짜: $recommendedChargeDateText",
                            color = Colors.Text,
                            fontSize = 18.sp
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    // 배터리 온도 측정 및 현재 배터리 충전량
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(150.dp)
                                .background(Color.White, RoundedCornerShape(12.dp))
                                .border(2.dp, Color.Gray, RoundedCornerShape(12.dp))
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Crossfade(targetState = currentModuleIndex,
                                animationSpec = tween(durationMillis = 2500)
                            ) { index ->
                                val currentTemperature = temperatureData.getOrNull(index)?.module_temp?.toString() ?: "24"
                                val moduleNumber = index + 1
                                val tempValue = currentTemperature.toFloatOrNull() ?: -1f
                                val statusMessage = when {
                                    tempValue >= 50 -> "과열 경고"
                                    tempValue >= 40 -> "점검 필요"
                                    tempValue in 20f..39f -> "안정적"
                                    else -> "온도 낮음"
                                }
                                val iconColor = when {
                                    tempValue >= 50 -> Color.Red
                                    tempValue >= 40 -> Color(0xFFFFA726)
                                    tempValue in 20f..39f -> Color.Green
                                    else -> Color.Blue
                                }
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = "온도 정보",
                                        tint = iconColor,
                                        modifier = Modifier.size(30.dp)
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "온도 모듈 $moduleNumber",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "$currentTemperature°C",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = statusMessage,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }
                        // 현재 배터리 충전량 버튼
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(150.dp)
                                .background(Colors.Background)
                                .border(width = 2.dp, color = Colors.Text)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text("현재 배터리 충전량", color = Colors.Text, fontSize = 16.sp)
                                Box(
                                    modifier = Modifier
                                        .size(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (batteryPercentage == 0)
                                        batteryPercentage = 80
                                    // 기본 검은색 배경에 흰색으로 배터리 잔량 표시
                                    CircularProgressIndicator(
                                        progress = 1f,
                                        color = Colors.Text,
                                        modifier = Modifier.size(100.dp),
                                        strokeWidth = 8.dp
                                    )
                                    CircularProgressIndicator(
                                        progress = batteryPercentage / 100f,
                                        color = Colors.Button,
                                        modifier = Modifier.size(100.dp),
                                        strokeWidth = 8.dp
                                    )
                                    Text(
                                        "$batteryPercentage%",
                                        color = Colors.Text,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Colors.Text, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    // 주변 충전소 찾기 버튼
                    OutlinedButton(
                        onClick = { navController.navigate("chargingStation") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        border = BorderStroke(2.dp, Colors.Button),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Transparent,
                            contentColor = Colors.Button
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "충전소",
                            tint = Colors.Button,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "주변 충전소 찾기",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Colors.Text
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowForward,
                            contentDescription = "이동",
                            tint = Colors.Button,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                item {
                    Text(
                        text = "차량 주행 정보",
                        color = Colors.Title,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
                item {
                    // 총 주행 거리
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.animmain))
                        val progress by animateLottieCompositionAsState(
                            composition,
                            iterations = LottieConstants.IterateForever
                        )

                        if (composition != null) {
                            LottieAnimation(
                                composition = composition,
                                progress = progress,
                                modifier = Modifier.size(300.dp)
                            )
                        }
                        Column(
                            modifier = Modifier
                                .offset(x = (-100).dp, y = (-80).dp) // 위치 조정
                                .background(Colors.Button, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "전비: $fuelEfficiency",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        // 총 주행 거리 표시
                        Column(
                            modifier = Modifier
                                .offset(x = 0.dp, y = 90.dp) // 위치 조정
                                .background(Colors.Button, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "총 주행 거리: $totalDistance",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        // 총 주행 시간 표시
                        Column(
                            modifier = Modifier
                                .offset(x = 100.dp, y = (-80).dp) // 위치 조정
                                .background(Colors.Button, shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "총 주행 시간: $totalTime",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                // 완속 충전 스케줄링 캘린더
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "완속 충전 스케줄링",
                            color = Colors.Title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopStart)
                        ) {
                        }
                    }
                }
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp)
                    ) {
                        Text(
                            text = "•  한 달에 한 번 이상 100% 완속 충전으로 배터리 성능\n 저하를 예방할 수 있습니다.",
                            color = Colors.Text,
                            fontSize = 16.sp,
                            lineHeight = 20.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            LabelledBox(text = "오늘 날짜", backgroundColor = Colors.Button)
                            LabelledBox(text = "권장 날짜", backgroundColor = Color.Red)
                            LabelledBox(text = "과거 충전 날짜", backgroundColor = Color.Yellow)
                        }
                    }
                }
                item {
                    CalendarApp(recommendedChargeDate = recommendedChargeDate, currentDate = currentDate)
                }
            }
        }
    )
}

