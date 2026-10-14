package com.example.doanckapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doanckapi.ui.theme.AccentRed
import com.example.doanckapi.ui.theme.PrimaryGreen
import com.example.doanckapi.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, viewModel: AuthViewModel) {
    LaunchedEffect(viewModel.loginSuccess) {
        if (viewModel.loginSuccess) {
            viewModel.resetState()
            // Navigate to Main App and pop backstack
            navController.navigate("main_app") {
                popUpTo("auth") { inclusive = true }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFF0EC), // Light pink/orange top
                        Color(0xFFE4E9FC)  // Light blue bottom
                    )
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        
        Spacer(modifier = Modifier.height(64.dp))
        
        // App / Screen Title
        Text(
            text = "Đăng nhập",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "Nhập tài khoản để theo dõi hồ sơ sức khoẻ của bạn",
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 40.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        // Error message card
        if (viewModel.loginError != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AccentRed, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                    Text(
                        text = viewModel.loginError!!,
                        color = AccentRed,
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Credentials Card
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AppleLightBlueAction, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                    Text("Tài khoản của bạn", color = AppleLightBlueAction, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                LightRegInputRow(viewModel.loginUsername, { viewModel.loginUsername = it }, "Tên đăng nhập")
                LightRegInputRow(viewModel.loginPassword, { viewModel.loginPassword = it }, "Mật khẩu", isPassword = true, isLast = true)
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(color = AppleLightBlueAction, modifier = Modifier.padding(24.dp))
        } else {
            Button(
                onClick = { viewModel.login() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlueAction),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Đăng nhập", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = { navController.navigate(Screen.Register.route) },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Chưa có tài khoản? Đăng ký ngay", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
    }
}
