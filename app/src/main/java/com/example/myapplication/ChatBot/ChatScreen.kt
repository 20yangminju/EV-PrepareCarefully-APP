package com.example.myapplication.ChatBot

import Place
import PromptRef
import ResponsesRequest
import RetrofitInstance
import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.myapplication.navigation.BottomNavigationBar
import com.example.myapplication.resource.ChatBubble
import com.example.myapplication.ui.theme.Colors
import kotlinx.coroutines.launch
import retrofit2.HttpException

var firstMessage = true

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController,
) {
    var userInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf<Pair<String, Boolean>>()) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    var currentLocation by remember { mutableStateOf<Location?>(null) }
    val repairShops = remember { mutableStateOf<List<Place>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true ) }
    val errorMessage = remember { mutableStateOf<String?>(null) }




    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        )
    }



    val launcher = rememberLauncherForActivityResult(
        contract =  ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        hasLocationPermission = isGranted
        if(isGranted){
            Toast.makeText(context, "위치 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "위치 권한이 거부되었습니다", Toast.LENGTH_SHORT).show()
        }
    }



    // 초기 프롬프트 설정
    LaunchedEffect(Unit) {

        if (!hasLocationPermission){
            launcher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        getCurrentLocation(context) {
            location -> currentLocation = location
            fetchNearbyRepairShops(currentLocation!!.latitude,
                currentLocation!!.longitude,
                onResult = { places -> repairShops.value = places
                isLoading.value =false

                },
                onError = { error ->
                    errorMessage.value = error
                    isLoading.value =false
                })

            firstMessage = true
        }





        chatHistory = chatHistory + Pair("궁금한 것이 있으면 물어보세요.", false)
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { Text(text = "AI 챗봇", color = Colors.Title) },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = Colors.Background
                    )
                )
                Divider(
                    color = Colors.Divider,
                    thickness = 1.dp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        },
        bottomBar = {
            BottomNavigationBar(
                navController = navController,
                currentScreen = navController.currentDestination?.route ?: "main"
            )
        },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Colors.Background)
                    .padding(paddingValues)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        items(chatHistory) { (message, isUserMessage) ->
                            ChatBubble(message = message, isUserMessage = isUserMessage)
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.White, RoundedCornerShape(12.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = userInput,
                            onValueChange = { userInput = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(12.dp),
                            label = { Text("챗봇에게 메세지 보내기", color = Colors.Placeholder) },
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Colors.ChatField,
                                focusedIndicatorColor = Colors.Text,
                                unfocusedIndicatorColor = Colors.Text,
                                textColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (userInput.isNotEmpty()) {
                                    coroutineScope.launch {
                                        // 사용자의 메시지 추가
                                        chatHistory = chatHistory + Pair(userInput, true)

                                        // 초기 프롬프트를 함께 포함하여 메시지를 보냄
                                        val response = sendMessageToGPT(userInput,  repairShops.value)

                                        // 응답 추가
                                        chatHistory = chatHistory + Pair(response, false)
                                        userInput = ""

                                        val lastVisibleItemIndex =
                                            listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index
                                        if (lastVisibleItemIndex == null || lastVisibleItemIndex >= chatHistory.size - 3) {
                                            listState.animateScrollToItem(chatHistory.size - 1)
                                        }
                                    }
                                }
                            },
                            modifier = Modifier
                                .height(48.dp)
                                .padding(horizontal = 8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Colors.Button),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Text("전송")
                        }
                    }
                }
            }
        }
    )
}

// GPT API 호출 함수 (기존처럼 정의하되 초기 프롬프트 포함)
suspend fun sendMessageToGPT(userMessage: String, nearShops: List<Place>): String {
    // (1) 정비소 목록 상한 + 요약
    val limit = 5
    val trimmed = nearShops.take(limit)
    val shopsText = if (trimmed.isNotEmpty()) {
        val base = trimmed.joinToString("\n") { "- ${it.name} | ${it.vicinity}" }
        val extra = nearShops.size - trimmed.size
        if (extra > 0) "$base\n(외 ${extra}곳)" else base
    } else {
        "현재 주변에 정비소가 없습니다."
    }
//

    val inputText = buildString {
        appendLine("사용자 질문: ${userMessage.trim()}")
        appendLine("정비소 목록:")
        append(shopsText)
    }

    // (3) 요청 본문 (Responses API)
    val request = ResponsesRequest(
        model = "gpt-5-nano",
        prompt = PromptRef( // 대시보드 Prompt ID/버전
            id = "pmpt_68b57ad3d9908195bcd155d501c1393d03266771c39cca3e",
            version = "18", // 또는 "latest"
            variables = mapOf(
                "user_message" to userMessage.trim(),
                "near_shops" to shopsText
            )
        ),
        max_output_tokens = 2000 )

    // (4) 호출 + 응답 파싱
    return try {
        val resp = RetrofitInstance.api.createResponse(request)

        // ✅ output[0].content[0].text에서 텍스트 추출
        var text = resp.output
            ?.firstOrNull()
            ?.content
            ?.firstOrNull { it.type == "output_text" }
            ?.text
            ?.trim()

        if (text.isNullOrBlank()) {
            text = resp.output.orEmpty()
                .filter { it.type == "message" }
                .flatMap { it.content.orEmpty() }
                .filter { it.type == "output_text" }
                .mapNotNull { it.text }
                .joinToString("")
                .trim()
                .ifEmpty { null }
        }

        text ?: "죄송합니다. 명령을 이해하지 못했어요."
    } catch (e: HttpException) {
        val body = e.response()?.errorBody()?.string()
        if (e.code() == 429) {
            val retryAfter = e.response()?.headers()?.get("Retry-After")
            "요청이 많아요. ${retryAfter ?: "잠시"} 후 다시 시도해주세요. ($body)"
        } else {
            "HTTP 오류: ${e.code()} ${e.message()} / $body"
        }
    } catch (e: Exception) {
        "요청 처리 중 오류가 발생했어요. (${e.message})"
    }
}


@Preview(showBackground = true, name = "ChatScreen Preview")
@Composable
fun ChatScreenPreview() {
    ChatScreen(navController = NavController(LocalContext.current))
}