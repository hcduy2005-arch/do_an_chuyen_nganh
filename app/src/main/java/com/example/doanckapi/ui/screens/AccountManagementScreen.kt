package com.example.doanckapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.doanckapi.data.db.UserEntity
import com.example.doanckapi.ui.theme.AccentRed
import com.example.doanckapi.ui.theme.PrimaryGreen
import com.example.doanckapi.ui.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagementScreen(
    navController: NavController,
    authViewModel: AuthViewModel
) {
    var userToDelete by remember { mutableStateOf<UserEntity?>(null) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        authViewModel.loadAllUsers()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quản lý tài khoản", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(authViewModel.userList) { user ->
                    val isCurrentUser = authViewModel.loggedInUserId == user.id

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "User Icon",
                                    tint = if (isCurrentUser) PrimaryGreen else Color.Gray,
                                    modifier = Modifier.size(40.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Column {
                                    Text(
                                        text = user.username,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isCurrentUser) PrimaryGreen else MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "ID: ${user.id} | Tuổi: ${user.Age.toInt()}",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                    if (isCurrentUser) {
                                        Text(text = "(Đang đăng nhập)", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }

                            IconButton(
                                onClick = {
                                    userToDelete = user
                                    showConfirmDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete User",
                                    tint = AccentRed
                                )
                            }
                        }
                    }
                }
            }

            if (authViewModel.userList.isEmpty()) {
                Text(
                    text = "Chưa có tài khoản nào.",
                    modifier = Modifier.align(Alignment.Center),
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
    }

    if (showConfirmDialog && userToDelete != null) {
        val isDeletingSelf = authViewModel.loggedInUserId == userToDelete?.id
        
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(text = "Xoá tài khoản", fontWeight = FontWeight.Bold, color = AccentRed) },
            text = { 
                Column {
                    Text(text = "Bạn có chắc chắn muốn xoá tài khoản '${userToDelete?.username}' không?")
                    if (isDeletingSelf) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Lưu ý: Đây chính là tài khoản bạn đang đăng nhập. Nếu xoá, hệ thống sẽ đăng xuất lập tức.",
                            color = AccentRed,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        userToDelete?.let { user ->
                            authViewModel.deleteUser(user.id) {
                                navController.navigate(Screen.Login.route) {
                                    popUpTo(0) { inclusive = true }
                                }
                            }
                        }
                        showConfirmDialog = false
                    }
                ) {
                    Text("Xoá vĩnh viễn", color = AccentRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("Huỷ bỏ")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}
