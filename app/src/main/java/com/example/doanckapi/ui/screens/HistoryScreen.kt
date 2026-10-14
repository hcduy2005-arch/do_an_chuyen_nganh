package com.example.doanckapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanckapi.data.api.RetrofitClient
import com.example.doanckapi.data.db.AppDatabase
import com.example.doanckapi.data.repository.PredictionRepository
import com.example.doanckapi.ui.theme.AccentRed
import com.example.doanckapi.ui.theme.PrimaryGreen
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke

@Composable
fun HistoryScreen(userId: Int) {
    val context = LocalContext.current
    val repository = remember {
        PredictionRepository(
            RetrofitClient.apiService,
            AppDatabase.getDatabase(context).historyDao()
        )
    }
    val viewModel: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(repository))
    androidx.compose.runtime.LaunchedEffect(userId) {
        viewModel.setUserId(userId)
    }
    val historyList = viewModel.historyFlow.collectAsState(initial = emptyList()).value
    var itemToDelete by remember { mutableStateOf<com.example.doanckapi.data.db.HistoryEntity?>(null) }

    // Xác nhận xóa
    if (itemToDelete != null) {
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Xóa lịch sử", fontWeight = FontWeight.Bold, color = Color(0xFFFF3B30)) },
            text = { Text("Bạn có chắc muốn xóa lần đo này không? Hành động này không thể hoàn tác.", fontSize = 15.sp, color = Color.Black) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.deleteHistory(itemToDelete!!)
                    itemToDelete = null
                }) {
                    Text("Xóa", color = Color(0xFFFF3B30), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Huỷ", color = Color(0xFF007AFF), fontSize = 16.sp)
                }
            },
            containerColor = Color.White,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
        )
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
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lịch sử dự đoán",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (historyList.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Chưa có dữ liệu lịch sử", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp) // padding for Bottom Nav
            ) {
                item { HealthScoreChart(historyList) }
                items(historyList) { item ->
                    val resultColor = when (item.predictionResult) {
                        2 -> Color(0xFFFF3B30)
                        1 -> Color(0xFFFF9500)
                        else -> Color(0xFF34C759)
                    }
                    val resultText = when (item.predictionResult) {
                        2 -> item.diabetesType ?: "Bị tiểu đường"
                        1 -> item.diabetesType ?: "Tiền tiểu đường"
                        else -> "Không bị"
                    }
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    val dateString = dateFormat.format(Date(item.timestamp))

                    val score = calculateHealthScore(item)

                    val ringColor = when {
                        score >= 80f -> Color(0xFF4CAF50) // Green
                        score >= 50f -> Color(0xFFFF9800) // Orange
                        else -> Color(0xFFF44336) // Red
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                    ) {
                        Column(modifier = Modifier.padding(20.dp)) {
                            // Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Default.Info, 
                                        contentDescription = null,
                                        tint = ringColor,
                                        modifier = Modifier.size(20.dp).padding(end = 4.dp)
                                    )
                                    Text(
                                        text = dateString,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = ringColor
                                    )
                                }
                                IconButton(
                                    onClick = { itemToDelete = item },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        tint = Color(0xFFFF3B30).copy(alpha = 0.5f)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            // Body Row
                            Row(
                                modifier = Modifier.fillMaxWidth(), 
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Left Side Metrics
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = resultText,
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                    Text(
                                        text = "${score.toInt()} Health Points",
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(bottom = 12.dp)
                                    )
                                    
                                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                        Column {
                                            Text("Huyết áp", fontSize = 11.sp, color = ringColor, fontWeight = FontWeight.Bold)
                                            Text(if (item.HighBP == 1f) "Cao" else "T.Bình", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                        }
                                        Column {
                                            Text("Cholesterol", fontSize = 11.sp, color = ringColor, fontWeight = FontWeight.Bold)
                                            Text(if (item.HighChol == 1f) "Cao" else "T.Bình", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Black)
                                        }
                                    }
                                }

                                // Right Side Circular Ring
                                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(80.dp)) {
                                    androidx.compose.foundation.Canvas(modifier = Modifier.size(80.dp)) {
                                        // Background Track
                                        drawArc(
                                            color = Color(0xFFF0F0F0),
                                            startAngle = 0f,
                                            sweepAngle = 360f,
                                            useCenter = false,
                                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                                        )
                                        // Foreground Progress Ring
                                        drawArc(
                                            color = ringColor,
                                            startAngle = -90f,
                                            sweepAngle = (score / 100f) * 360f,
                                            useCenter = false,
                                            style = Stroke(width = 12f, cap = StrokeCap.Round)
                                        )
                                    }
                                    Text(
                                        text = "${score.toInt()}",
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

fun calculateHealthScore(item: com.example.doanckapi.data.db.HistoryEntity): Float {
    var score = 100f
    if (item.HighBP == 1f) score -= 15f
    if (item.HighChol == 1f) score -= 15f
    if (item.HeartDiseaseorAttack == 1f) score -= 20f
    if (item.Stroke == 1f) score -= 20f
    score -= (item.GenHlth - 1f) * 5f
    if (item.PhysActivity == 0f) score -= 10f
    if (score < 0f) score = 0f
    return score
}

@Composable
fun HealthScoreChart(historyList: List<com.example.doanckapi.data.db.HistoryEntity>) {
    if (historyList.isEmpty()) return

    val chronologicallyOrdered = historyList.sortedBy { it.timestamp }
    val scores = chronologicallyOrdered.map { calculateHealthScore(it) }

    val latestScore = scores.last()
    val avgScore = if (scores.isNotEmpty()) scores.average().toFloat() else 0f

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            // Header
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = Color(0xFFFF3B30),
                    modifier = Modifier.size(20.dp).padding(end = 4.dp)
                )
                Text(
                    text = "Điểm sức khoẻ",
                    color = Color(0xFFFF3B30),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Theo dõi sức khoẻ của bạn qua thời gian",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Điểm sức khoẻ (0–100) được tính dựa trên: huyết áp (-15), cholesterol (-15), bệnh tim (-20), đột quỵ (-20), sức khoẻ tổng quát (-5 mỗi mức) và ít vận động (-10). Điểm càng cao càng tốt.",
                fontSize = 13.sp,
                color = Color.Gray,
                lineHeight = 20.sp
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFFFF3B30), androidx.compose.foundation.shape.CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Gần nhất (Latest)", color = Color.Gray, fontSize = 12.sp)
                    }
                    Text(
                        text = "${latestScore.toInt()}",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFF3B30)
                    )
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color.Gray, androidx.compose.foundation.shape.CircleShape))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Trung bình (Avg)", color = Color.Gray, fontSize = 12.sp)
                    }
                    Text(
                        text = "${avgScore.toInt()}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (scores.size > 1) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                        val maxScore = 100f
                        val minScore = 0f
                        val range = maxScore - minScore
                        
                        val canvasWidth = size.width
                        val canvasHeight = size.height
                        
                        // We need to plot over the entire width.
                        // Since there might be a few points, let's step smoothly.
                        val stepX = canvasWidth / (scores.size - 1).coerceAtLeast(1)
                        
                        val path = androidx.compose.ui.graphics.Path()
                        val points = mutableListOf<androidx.compose.ui.geometry.Offset>()
                        
                        scores.forEachIndexed { index, score ->
                            val x = index * stepX
                            val y = canvasHeight - ((score - minScore) / range * canvasHeight)
                            points.add(androidx.compose.ui.geometry.Offset(x, y))
                            
                            if (index == 0) {
                                path.moveTo(x, y)
                            } else {
                                path.lineTo(x, y)
                            }
                        }
                        
                        drawPath(
                            path = path,
                            color = Color(0xFFFF3B30),
                            style = Stroke(width = 8f, cap = StrokeCap.Round, join = androidx.compose.ui.graphics.StrokeJoin.Round)
                        )
                        
                        val fillPath = androidx.compose.ui.graphics.Path().apply {
                            addPath(path)
                            lineTo(canvasWidth, canvasHeight)
                            lineTo(0f, canvasHeight)
                            close()
                        }
                        
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFFFF3B30).copy(alpha = 0.2f), Color.Transparent),
                                startY = 0f,
                                endY = canvasHeight
                            )
                        )
                        
                        drawCircle(
                            color = Color(0xFFFF3B30),
                            radius = 16f,
                            center = points.last()
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 8f,
                            center = points.last()
                        )
                    }
                }
            } else {
                Text(
                    text = "Cần ít nhất 2 lần đo để lập đồ thị.",
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 32.dp).align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
