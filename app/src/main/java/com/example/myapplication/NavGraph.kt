// NavGraph.kt
package com.example.myapplication.navigation


import BatteryTemperatureScreen
import CellBalanceScreen
import com.example.myapplication.ChatBot.ChatScreen
import com.example.myapplication.ChargingStationScreen
import LoginScreen
import MainScreen
import SettingsScreen
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.AlarmScreen
import com.example.myapplication.BatteryChargeScreen
import com.example.myapplication.DeviceRegistration
import com.example.myapplication.resource.LoadUserInfoScreen;
import com.example.myapplication.SignUpScreen
import com.example.myapplication.FirstScreen
import com.example.myapplication.RegisterCarScreen
import com.example.myapplication.resource.NotificationViewModel
import com.example.myapplication.screens.FAQScreen
import com.example.myapplication.screens.InquiryScreen
import com.example.myapplication.screens.NoticeScreen
import com.example.myapplication.screens.PrivacyPolicyScreen
import com.example.myapplication.screens.TermsOfServiceScreen
import com.example.myapplication.screens.UserInfoScreen
import com.example.myapplication.screens.VersionInfoScreen
import com.example.myapplication.AIImageScreen

data class LoginData(
    var id: String = "null",
    var password : String = "null"
)

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(navController: NavHostController,
                  notificationViewModel: NotificationViewModel) {
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = "FirstScreen") {
        composable("FirstScreen") {
            FirstScreen(context, onNavigateToLogin = {
                navController.navigate("Login")
            })
        }
        composable("Login") {
            LoginScreen(context,
                onLogin = { navController.navigate("userInfo") },
                onSignUp = { navController.navigate("SignUp") },
                onFirstScreen = { navController.navigate("FirstScreen") },
                onNavigateToMain = { navController.navigate("main") }
            )
        }
        composable("userInfo") {
            LoadUserInfoScreen(context, onNavigateToChat = {
                navController.navigate("ChatScreen")
            })
        }
        composable("main") {
            MainScreen(
                navController = navController,
                onNavigateToSettings = { navController.navigate("Setting") },
                onNavigateToNotifications = { navController.navigate("AlarmScreen")},
                onNavigateAIscreen = { navController.navigate("AIScreen")},
                notificationViewModel = notificationViewModel
            )
        }
        composable("BatteryChargeScreen") {
            BatteryChargeScreen(
                navController = navController,
                onNavigateAIscreen = { navController.navigate("AIScreen")},
                onNavigateToSettings = { navController.navigate("Setting") },
                onNavigateToNotifications = { navController.navigate("AlarmScreen") },
                notificationViewModel = notificationViewModel
            ) }
        composable("batteryTemperature") {
            BatteryTemperatureScreen(
                navController = navController,
                onNavigateAIscreen = { navController.navigate("AIScreen")},
                onNavigateToSettings = { navController.navigate("Setting") },
                onNavigateToNotifications = { navController.navigate("AlarmScreen") },
                notificationViewModel = notificationViewModel
        ) }
        composable("cellBalance") { CellBalanceScreen(
            navController = navController,
            onNavigateAIscreen = { navController.navigate("AIScreen")},
            onNavigateToSettings = { navController.navigate("Setting") },
            onNavigateToNotifications = { navController.navigate("AlarmScreen") },
            notificationViewModel = notificationViewModel

        ) }
        composable("AlarmScreen") {
            AlarmScreen(
                onNavigateToHome = { navController.navigate("main") },
                viewModel = notificationViewModel
            )
        }
        composable("SignUp") {
            SignUpScreen(context,
                //done = { navController.navigate("Login")
                done = { navController.navigate("RegisterCar")},
                onNavigateToLogin = { navController.navigate("Login") }
            )
        }
        composable("DeviceRegistration") {
            DeviceRegistration(
                navController = navController,
                context = context,
                done = { navController.navigate("Login") }
            )
        }
        composable("RegisterCar") {
            RegisterCarScreen(context,
                done = { navController.navigate("DeviceRegistration") },
                onNavigateToPre = { navController.popBackStack() }
            )
        }
        composable("Setting") {
            SettingsScreen(
                onItemClick = { item ->
                    navController.navigate("ItemDetail/$item")
                },
                onNavigateToPre = {
                    navController.popBackStack()
                },
                navigateToFAQ = { navController.navigate("FAQ") },
                navigateToNotice = { navController.navigate("Notice")},
                navigateToInquiry = { navController.navigate("Inquiry") },
                navigateToUserInfo = { navController.navigate("UserInfo") },
                navigateToVersionInfo = { navController.navigate("VersionInfo") },
                navigateToTermsOfService = { navController.navigate("TermsOfService") },
                navigateToPrivacyPolicy = { navController.navigate("PrivacyPolicy") },

            )
        }

        composable("FAQ") {
            FAQScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Notice") {
            NoticeScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("Inquiry") {
            InquiryScreen(
                onNavigateBack = { navController.popBackStack() },
                onSubmitInquiry = { title, email, content ->
                    // onSubmitInquiry 로직 추가 또는 연결된 함수 호출
                    println("문의 제목: $title")
                    println("이메일: $email")
                    println("내용: $content")
                }
            )
        }



        composable("UserInfo") {
            UserInfoScreen(navController = navController)
        }
        composable("VersionInfo") {
            VersionInfoScreen(navController = navController)
        }
        composable("TermsOfService") {
            TermsOfServiceScreen(navController = navController)
        }
        composable("PrivacyPolicy") {
            PrivacyPolicyScreen(navController = navController)
        }


        composable("ChatScreen") {
            ChatScreen(
                navController = navController,
                )
        }
        composable("AIScreen") {
            AIImageScreen (
                onNavigateToPre = {
                    navController.popBackStack()
                }
            )
        }
        composable("chargingStation") {
            ChargingStationScreen(
                navController = navController,
                onNavigateAIscreen = { navController.navigate("AIScreen") },
                onNavigateToSettings = { navController.navigate("Setting") },
                onNavigateToNotifications = { navController.navigate("AlarmScreen") },
                notificationViewModel = notificationViewModel
            )
        }
    }
}
