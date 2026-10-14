package com.example.doanckapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.doanckapi.data.api.RetrofitClient
import com.example.doanckapi.data.db.AppDatabase
import com.example.doanckapi.data.repository.PredictionRepository
import com.example.doanckapi.ui.theme.AccentRed
import com.example.doanckapi.ui.theme.PrimaryGreen

val AppleLightRed = Color(0xFFFF3B30)
val AppleLightGreen = Color(0xFF34C759)
val AppleLightOrange = Color(0xFFFF9500)
val AppleLightBlue = Color(0xFF007AFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictScreen(userId: Int) {
    val context = LocalContext.current
    val database = remember { AppDatabase.getDatabase(context) }
    val repository = remember {
        PredictionRepository(
            RetrofitClient.apiService,
            database.historyDao()
        )
    }
    val userDao = database.userDao()
    val viewModel: PredictViewModel = viewModel(factory = PredictViewModelFactory(repository, userDao))
    androidx.compose.runtime.LaunchedEffect(userId) {
        viewModel.loadUserProfile(userId)
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
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nhập dữ liệu dự đoán",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Result Dialogs
        if (viewModel.predictionResult != null) {
            val result = viewModel.predictionResult!!
            val resultTitle = when (result) {
                2 -> "Bị tiểu đường"
                1 -> "Tiền tiểu đường"
                else -> "An toàn"
            }
            val resultColor = when (result) {
                2 -> AppleLightRed
                1 -> AppleLightOrange
                else -> AppleLightGreen
            }
            val resultIcon = when (result) {
                2 -> Icons.Default.Warning
                1 -> Icons.Default.Info
                else -> Icons.Default.CheckCircle
            }
            val resultSubText = when (result) {
                2 -> "Chỉ số cho thấy rủi ro cao mắc bệnh tiểu đường. Hãy đến cơ sở y tế để kiểm tra chuyên sâu."
                1 -> "Cảnh báo chỉ số tiền tiểu đường. Bạn nên điều chỉnh ngay chế độ ăn uống và sinh hoạt."
                else -> "Chỉ số hiện tại của bạn nằm ở mức an toàn. Hãy tiếp tục duy trì lối sống lành mạnh nhé!"
            }
            
            Dialog(onDismissRequest = { viewModel.predictionResult = null }) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(resultColor.copy(alpha = 0.15f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(resultIcon, contentDescription = null, tint = resultColor, modifier = Modifier.size(32.dp))
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        Text(text = "Kết quả phân tích", fontSize = 15.sp, color = Color.Gray, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = resultTitle, fontSize = 28.sp, fontWeight = FontWeight.Bold, color = resultColor, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = resultSubText, fontSize = 15.sp, lineHeight = 22.sp, color = Color.DarkGray, textAlign = TextAlign.Center)
                        
                        if (result == 2) {
                            Spacer(modifier = Modifier.height(24.dp))
                            Divider(color = Color(0xFFF0F0F0))
                            Spacer(modifier = Modifier.height(16.dp))
                            
                            viewModel.diabetesType?.let {
                                Text("Phân loại: $it", fontWeight = FontWeight.Bold, color = AppleLightRed, fontSize = 16.sp, textAlign = TextAlign.Center)
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            viewModel.insulinRecommendation?.let {
                                Text("Lời khuyên Insulin:", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                                Text(it, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.align(Alignment.Start))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            viewModel.longevityImpact?.let {
                                Text("Tuổi thọ:", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                                Text(it, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.align(Alignment.Start))
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                            
                            viewModel.suggestions?.takeIf { it.isNotEmpty() }?.let { suggestions ->
                                Text("Gợi ý:", fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 14.sp, modifier = Modifier.align(Alignment.Start))
                                val adviceText = suggestions.joinToString(separator = "\n- ", prefix = "- ")
                                Text(adviceText, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.align(Alignment.Start))
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(32.dp))
                        
                        Button(
                            onClick = { viewModel.predictionResult = null },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Đóng", fontSize = 17.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        }
                    }
                }
            }
        }

        if (viewModel.errorMessage != null) {
            AlertDialog(
                onDismissRequest = { viewModel.errorMessage = null },
                title = { Text("Lỗi", fontWeight = FontWeight.Bold, color = AppleLightRed) },
                text = { Text(viewModel.errorMessage!!, fontSize = 16.sp, color = Color.Black) },
                confirmButton = {
                    TextButton(onClick = { viewModel.errorMessage = null }) {
                        Text("Đóng", color = AppleLightBlue, fontSize = 17.sp)
                    }
                },
                containerColor = Color.White,
                shape = RoundedCornerShape(16.dp)
            )
        }

        viewModel.userProfile?.let { profile ->
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = AppleLightRed, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                        Text("Hồ sơ cố định", color = AppleLightRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LightHealthInfoRow("Người dùng", profile.username)
                    LightHealthInfoRow("Giới tính", if (profile.Sex == 1) "Nam" else "Nữ")
                    LightHealthInfoRow("Tuổi (18-80)", profile.Age.toInt().toString())
                    LightHealthInfoRow("Thu nhập (1-8)", profile.Income.toInt().toString())
                    LightHealthInfoRow("Chỉ số BMI", profile.BMI.toString())
                    
                    val conditions = mutableListOf<String>()
                    if (profile.HighBP == 1f) conditions.add("Huyết áp cao")
                    if (profile.HighChol == 1f) conditions.add("Cholesterol cao")
                    if (profile.HeartDiseaseorAttack == 1f) conditions.add("Bệnh tim")
                    if (profile.Stroke == 1f) conditions.add("Đột quỵ")
                    if (profile.DiffWalk == 1f) conditions.add("Khó đi lại")
                    
                    val conditionsText = if (conditions.isEmpty()) "Không" else conditions.joinToString(", ")
                    val conditionsColor = if (conditions.isEmpty()) AppleLightGreen else AppleLightRed
                    
                    LightHealthInfoRow("Tiền sử bệnh", conditionsText, isLast = true, valueColor = conditionsColor)
                }
            }
        } ?: run {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 24.dp), color = PrimaryGreen)
        }

        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.DateRange, contentDescription = null, tint = AppleLightOrange, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                    Text("Chỉ số hôm nay", color = AppleLightOrange, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Hãy nhập trung thực các chỉ số hiện tại để thu được kết quả chính xác.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LightHealthInputRow(
                    viewModel.GenHlth, { viewModel.GenHlth = it },
                    "SK tổng quát (1:Tốt -> 5:Kém)",
                    label2 = "Sức khỏe tổng quát",
                    description = "📋 Sức khỏe tổng quát là gì?\nĐánh giá CHỦ QUAN của bạn về sức khỏe tổng thể hiện tại.\n\n🔢 Thang điểm:\n• 1 = Rất tốt (Excellent)\n• 2 = Tốt (Very Good)\n• 3 = Trung bình (Good)\n• 4 = Kém (Fair)\n• 5 = Rất kém (Poor)\n\n✅ Bình thường: 1 – 2\n⚠️ Cần chú ý: 3\n🚨 Nguy cơ cao: 4 – 5\n\n💡 Mẹo xác định: Hãy tự hỏi 'Nhìn chung sức khỏe tôi đang thế nào?' rồi chọn con số phù hợp nhất với cảm nhận thực tế của bạn."
                )
                LightHealthInputRow(
                    viewModel.MentHlth, { viewModel.MentHlth = it },
                    "SK tinh thần (ngày)",
                    label2 = "Sức khỏe tinh thần",
                    description = "🧠 Sức khỏe tinh thần là gì?\nSố ngày trong 30 ngày qua bạn cảm thấy căng thẳng, lo âu, buồn chán, trầm cảm hoặc cảm xúc không ổn định.\n\n🔢 Giá trị nhập: 0 – 30 (ngày)\n• 0 = Không có ngày nào cảm thấy tệ\n• 30 = Cả tháng đều căng thẳng/buồn\n\n✅ Bình thường: 0 – 5 ngày\n⚠️ Cần chú ý: 6 – 13 ngày\n🚨 Nguy cơ cao: 14 ngày trở lên\n\n💡 Lưu ý: Căng thẳng mãn tính làm tăng cortisol, gây kháng insulin và tăng nguy cơ tiểu đường.\n\n💬 Ví dụ: Tháng qua bạn stress công việc 10 ngày → Nhập: 10"
                )
                LightHealthInputRow(
                    viewModel.PhysHlth, { viewModel.PhysHlth = it },
                    "SK thể chất (ngày)",
                    label2 = "Sức khỏe thể chất",
                    description = "🦴 Sức khỏe thể chất là gì?\nSố ngày trong 30 ngày qua bạn cảm thấy cơ thể không khỏe do ốm đau, bệnh tật, chấn thương hoặc đau nhức.\n\n🔢 Giá trị nhập: 0 – 30 (ngày)\n• 0 = Cả tháng cơ thể khỏe mạnh\n• 30 = Cả tháng đều cảm thấy không khỏe\n\n✅ Bình thường: 0 – 5 ngày\n⚠️ Cần chú ý: 6 – 14 ngày\n🚨 Nguy cơ cao: 15 ngày trở lên\n\n💡 Lưu ý: Đây chỉ tính ảnh hưởng thể chất (đau đầu, sốt, đau lưng, mệt mỏi...) – KHÁC với sức khỏe tinh thần.\n\n💬 Ví dụ: Tháng qua bị cảm 3 ngày + đau lưng 2 ngày → Nhập: 5"
                )
                LightHealthInputRow(
                    viewModel.PhysActivity, { viewModel.PhysActivity = it },
                    "Vận động (0-1)",
                    isLast = true,
                    label2 = "Hoạt động thể chất",
                    description = "🏃 Hoạt động thể chất là gì?\nBạn có thực hiện bất kỳ hoạt động thể chất nào NGOÀI công việc thường ngày trong 30 ngày qua không?\n\n🔢 Giá trị nhập:\n• 1 = CÓ vận động thêm\n• 0 = KHÔNG vận động gì thêm\n\n✅ Nhập 1 nếu bạn:\n• Đi bộ ít nhất 30 phút/ngày\n• Tập thể dục 2-3 lần/tuần trở lên\n• Có hoạt động thể thao nhẹ đến vừa (bơi, đạp xe, yoga...)\n\n⚠️ Nhập 0 nếu bạn:\n• Ngồi suốt ngày (làm việc văn phòng)\n• Không có hoạt động thể chất bổ sung\n\n💡 Tại sao quan trọng? Vận động đều đặn giúp kiểm soát đường huyết và giảm 30–58% nguy cơ tiểu đường type 2."
                )
            }
        }
        
        if (viewModel.userProfile?.Sex == 0) {
            Card(
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                        Text("Dữ liệu Thai kỳ (Tùy chọn)", color = PrimaryGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    LightHealthInputRow(
                        viewModel.Glucose, { viewModel.Glucose = it },
                        "Đường huyết (Glucose)",
                        label2 = "Chỉ số Glucose máu",
                        description = "🩸 Glucose trong máu là gì?\nGlucose là lượng đường trong máu (mg/dL). Đây là chỉ số QUAN TRỌNG NHẤT để phát hiện tiểu đường thai kỳ.\n\n🔢 Cách đo & nhập:\n• Đo lúc đói (nhịn ăn ít nhất 8 giờ)\n• Nhập số đo từ máy đo đường huyết hoặc xét nghiệm máu\n• Đơn vị: mg/dL\n\n✅ Bình thường (lúc đói): < 92 mg/dL\n⚠️ Tiền tiểu đường thai kỳ: 92 – 125 mg/dL\n🚨 Tiểu đường thai kỳ: ≥ 126 mg/dL\n\n💡 Nếu chưa có chỉ số:\n• Đến cơ sở y tế để xét nghiệm máu tĩnh mạch\n• Dùng máy đo đường huyết cầm tay (mua tại nhà thuốc)\n• Có thể để trống nếu chưa đo"
                    )
                    LightHealthInputRow(
                        viewModel.Insulin, { viewModel.Insulin = it },
                        "Lượng Insulin",
                        isLast = true,
                        label2 = "Hormone Insulin máu",
                        description = "💉 Insulin là gì?\nInsulin là hormone do tuyến tụy sản xuất, giúp đưa glucose từ máu vào tế bào. Trong thai kỳ, cơ thể cần nhiều insulin hơn bình thường.\n\n🔢 Cách đo & nhập:\n• Đo qua xét nghiệm máu tại cơ sở y tế\n• Đơn vị: μIU/mL (đo lúc đói)\n\n✅ Bình thường (lúc đói): 2 – 25 μIU/mL\n⚠️ Cần theo dõi: 25 – 166 μIU/mL\n🚨 Bất thường: > 166 μIU/mL\n\n💡 Ý nghĩa lâm sàng:\n• Insulin THẤP → Tuyến tụy không sản xuất đủ → Nguy cơ tiểu đường type 1\n• Insulin CAO → Cơ thể kháng insulin → Nguy cơ tiểu đường type 2 / thai kỳ\n\n⚕️ Để trống nếu chưa có kết quả xét nghiệm."
                    )
                }
            }
        }

        if (viewModel.isLoading) {
            CircularProgressIndicator(color = PrimaryGreen, modifier = Modifier.padding(16.dp))
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { viewModel.predict() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlue),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("Xem kết quả dự đoán", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = { viewModel.reset() },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
                ) {
                    Text("Làm mới dữ liệu", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

@Composable
fun LightHealthInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isLast: Boolean = false,
    label2: String? = null,
    description: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (description != null) {
                    IconButton(
                        onClick = { showDialog = true },
                        modifier = Modifier.size(28.dp).padding(start = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(18.dp)
                                .background(AppleLightBlue, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("i", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(color = Color.Black, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.width(100.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterEnd) {
                        if (value.isEmpty()) {
                            Text("Nhập", color = Color.LightGray, fontSize = 18.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.End)
                        }
                        innerTextField()
                    }
                }
            )
        }
        if (!isLast) {
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
        }
    }

    if (showDialog && description != null) {
        Dialog(onDismissRequest = { showDialog = false }) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Header
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(AppleLightBlue.copy(alpha = 0.12f), androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("i", color = AppleLightBlue, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = label2 ?: label,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = Color(0xFF1C1C1E)
                            )
                            Text(
                                text = "Hướng dẫn nhập chỉ số",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = Color(0xFFF0F0F0))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Scrollable description
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 380.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        description.split("\n").forEach { line ->
                            when {
                                line.startsWith("✅") -> InfoChip(line, Color(0xFF34C759))
                                line.startsWith("⚠️") -> InfoChip(line, Color(0xFFFF9500))
                                line.startsWith("🚨") -> InfoChip(line, Color(0xFFFF3B30))
                                line.startsWith("💡") -> InfoChip(line, Color(0xFF007AFF))
                                line.startsWith("💬") -> InfoChip(line, Color(0xFF8E8E93))
                                line.startsWith("⚕️") -> InfoChip(line, Color(0xFF30A9DE))
                                line.startsWith("•") -> {
                                    Text(
                                        text = line,
                                        fontSize = 14.sp,
                                        color = Color(0xFF3A3A3C),
                                        lineHeight = 22.sp,
                                        modifier = Modifier.padding(start = 8.dp, bottom = 2.dp)
                                    )
                                }
                                line.isEmpty() -> Spacer(modifier = Modifier.height(8.dp))
                                else -> Text(
                                    text = line,
                                    fontSize = 14.sp,
                                    color = Color(0xFF3A3A3C),
                                    lineHeight = 22.sp,
                                    fontWeight = if (line.contains("?") || line.endsWith("?")) FontWeight.SemiBold else FontWeight.Normal,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                        onClick = { showDialog = false },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Đã hiểu rồi ✓", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoChip(text: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(10.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 13.sp,
            color = color,
            fontWeight = FontWeight.SemiBold,
            lineHeight = 20.sp
        )
    }
    Spacer(modifier = Modifier.height(3.dp))
}

@Composable
fun LightHealthInfoRow(
    label: String,
    value: String,
    isLast: Boolean = false,
    valueColor: Color = Color.DarkGray,
    description: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (description != null) {
                    IconButton(onClick = { showDialog = true }, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                        Icon(Icons.Default.Info, contentDescription = "Thông tin thêm", tint = Color(0xFFAAAAAA), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Text(text = value, color = valueColor, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
        }
        if (!isLast) {
            Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
        }
    }

    if (showDialog && description != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Thông tin", fontWeight = FontWeight.Bold, color = AppleLightBlue) },
            text = { Text(description, fontSize = 15.sp, color = Color.Black, lineHeight = 22.sp) },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Đã hiểu", color = AppleLightBlue, fontSize = 17.sp)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
