package com.example.doanckapi.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Home : Screen("home", "Trang Chủ", Icons.Default.Home)
    object Predict : Screen("predict", "Dự Đoán", Icons.Default.PlayArrow)
    object History : Screen("history", "Lịch Sử", Icons.Default.List)
    object About : Screen("about", "Trợ Giúp", Icons.Default.Info)

    // Non-tab screens
    object Login : Screen("login", "Login", Icons.Default.Home)
    object Register : Screen("register", "Register", Icons.Default.Home)
    object ManageAccounts : Screen("manage_accounts", "Manage Accounts", Icons.Default.Person)
}
