package com.example.doanckapi.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.doanckapi.ui.theme.AccentRed
import com.example.doanckapi.ui.theme.PrimaryGreen

val AppleLightBlueAction = Color(0xFF007AFF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthViewModel) {
    LaunchedEffect(viewModel.registerSuccess) {
        if (viewModel.registerSuccess) {
            viewModel.resetState()
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFF0EC), Color(0xFFE4E9FC))
                )
            )
            .padding(horizontal = 16.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Tạo tài khoản",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = PrimaryGreen,
            modifier = Modifier.padding(bottom = 8.dp, top = 24.dp)
        )
        Text(
            text = "Nhập thông tin của bạn để bắt đầu theo dõi sức khoẻ",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // CARD 1: ACCOUNT INFO
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AppleLightBlueAction, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                    Text("Thông tin Đăng nhập", color = AppleLightBlueAction, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                LightRegInputRow(viewModel.regUsername, { viewModel.regUsername = it }, "Tên đăng nhập")
                LightRegInputRow(viewModel.regPassword, { viewModel.regPassword = it }, "Mật khẩu", isPassword = true)
                LightRegInputRow(viewModel.regConfirmPassword, { viewModel.regConfirmPassword = it }, "Xác nhận mật khẩu", isPassword = true, isLast = true)
            }
        }

        // CARD 2: DEMOGRAPHICS
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFFF9500), modifier = Modifier.size(20.dp).padding(end = 4.dp))
                    Text("Chỉ số Cơ bản", color = Color(0xFFFF9500), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Sex Selection
                Column {
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Text("Giới tính", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Nam",
                                color = if (viewModel.regSex == 1) AppleLightBlueAction else Color.Gray,
                                fontWeight = if (viewModel.regSex == 1) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.clickable { viewModel.regSex = 1 }.padding(horizontal = 8.dp))
                            Divider(color = Color(0xFFE0E0E0), modifier = Modifier.height(20.dp).width(1.dp))
                            Text("Nữ",
                                color = if (viewModel.regSex == 0) AccentRed else Color.Gray,
                                fontWeight = if (viewModel.regSex == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 16.sp,
                                modifier = Modifier.clickable { viewModel.regSex = 0 }.padding(horizontal = 8.dp))
                        }
                    }
                    Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
                }

                if (viewModel.regSex == 0) {
                    LightRegInputRow(
                        viewModel.regPregnancies, { viewModel.regPregnancies = it },
                        "Số lần mang thai", isNumber = true,
                        label2 = "Lịch sử mang thai",
                        description = "🤰 Số lần mang thai là gì?\nSố lần bạn đã mang thai (sinh con, sảy thai hoặc đang mang thai).\n\n🔢 Giá trị nhập: 0 – 17\n• 0 = Chưa từng mang thai\n• 1, 2, 3... = Số lần đã mang thai\n\n💡 Tại sao cần?\nSố lần mang thai liên quan đến nguy cơ tiểu đường thai kỳ. Mang thai nhiều lần làm tăng kháng insulin.\n\n💬 Ví dụ: Đã sinh 2 con → Nhập: 2"
                    )
                }

                LightRegInputRow(
                    viewModel.regAge, { viewModel.regAge = it },
                    "Tuổi (18-80)", isNumber = true,
                    label2 = "Tuổi của bạn",
                    description = "🎂 Nhập tuổi thực của bạn bằng số nguyên.\n\n🔢 Giá trị hợp lệ: 18 – 80\n\n✅ Tuổi trẻ: 18 – 44\n⚠️ Tăng nguy cơ: 45 – 59 tuổi\n🚨 Nguy cơ cao: 60 tuổi trở lên\n\n💡 Tại sao quan trọng?\nTuổi tác là yếu tố nguy cơ chính của tiểu đường type 2. Tuyến tụy mất dần hiệu quả theo tuổi.\n\n💬 Ví dụ: Bạn 32 tuổi → Nhập: 32"
                )
                LightRegInputRow(
                    viewModel.regIncome, { viewModel.regIncome = it },
                    "Thu nhập (1-8)", isNumber = true,
                    label2 = "Mức thu nhập hộ gia đình",
                    description = "💰 Thang thu nhập 1–8 nghĩa là gì?\nMức thu nhập hộ gia đình hàng năm (theo đơn vị USD dùng trong y tế quốc tế).\n\n🔢 Chi tiết từng mức:\n• 1 = Dưới $10,000/năm\n• 2 = $10,000 – $15,000\n• 3 = $15,000 – $20,000\n• 4 = $20,000 – $25,000\n• 5 = $25,000 – $35,000\n• 6 = $35,000 – $50,000\n• 7 = $50,000 – $75,000\n• 8 = Trên $75,000/năm\n\n💡 Gợi ý cho người Việt Nam:\nThu nhập dưới 20 triệu/tháng → chọn 1–2\nThu nhập 20–50 triệu/tháng → chọn 3–4"
                )
                LightRegInputRow(
                    viewModel.regBMI, { viewModel.regBMI = it },
                    "BMI", isLast = true, isNumber = true,
                    label2 = "Chỉ số khối cơ thể (BMI)",
                    description = "⚖️ BMI là gì?\nChỉ số khối cơ thể = Cân nặng (kg) ÷ Chiều cao² (m)\n\n💬 Cách tính ví dụ:\nNặng 60 kg, cao 1.65 m\nBMI = 60 ÷ (1.65 × 1.65) = 22.0\n→ Nhập: 22.0\n\n✅ Bình thường: 18.5 – 24.9\n⚠️ Thừa cân: 25.0 – 29.9\n🚨 Béo phì (nguy cơ cao): ≥ 30.0\n⚠️ Thiếu cân: Dưới 18.5\n\n💡 Tại sao quan trọng?\nBMI ≥ 30 là yếu tố nguy cơ HÀNG ĐẦU của tiểu đường type 2!"
                )
            }
        }

        // CARD 3: HEALTH HISTORY
        Card(
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = AccentRed, modifier = Modifier.size(20.dp).padding(end = 4.dp))
                    Text("Tiền sử bệnh lý", color = AccentRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text("Bấm vào ô để chọn Có hoặc Không", fontSize = 12.sp, color = Color.Gray, modifier = Modifier.padding(bottom = 12.dp))

                LightRegToggleRow(
                    viewModel.regHighBP, { viewModel.regHighBP = it }, "Huyết áp cao",
                    label2 = "Huyết áp cao (Hypertension)",
                    description = "🩸 Huyết áp cao là gì?\nÁp lực máu trong động mạch vượt mức bình thường kéo dài.\n\n✅ Bình thường: < 120/80 mmHg\n⚠️ Tiền tăng huyết áp: 120–139/80–89 mmHg\n🚨 Huyết áp cao: ≥ 140/90 mmHg → Chọn CÓ\n\n💡 Liên quan đến tiểu đường:\nHuyết áp cao tăng kháng insulin và nguy cơ tiểu đường type 2 lên 2–3 lần.\n\n💬 Chọn CÓ nếu bác sĩ đã chẩn đoán hoặc bạn đang dùng thuốc huyết áp"
                )
                LightRegToggleRow(
                    viewModel.regHighChol, { viewModel.regHighChol = it }, "Cholesterol cao",
                    label2 = "Cholesterol cao trong máu",
                    description = "🧪 Cholesterol cao là gì?\nMức LDL-cholesterol (cholesterol xấu) trong máu cao hơn ngưỡng an toàn.\n\n✅ Bình thường: < 200 mg/dL\n⚠️ Biên giới cao: 200–239 mg/dL\n🚨 Cholesterol cao: ≥ 240 mg/dL → Chọn CÓ\n\n💡 Liên quan đến tiểu đường:\nCholesterol cao gây tổn thương mạch máu, ảnh hưởng chuyển hóa glucose.\n\n💬 Chọn CÓ nếu bác sĩ xác nhận hoặc bạn đang dùng thuốc hạ mỡ máu"
                )
                LightRegToggleRow(
                    viewModel.regHeartDisease, { viewModel.regHeartDisease = it }, "Bệnh tim mạch",
                    label2 = "Bệnh tim hoặc nhồi máu cơ tim",
                    description = "❤️ Bệnh tim mạch là gì?\nBao gồm: nhồi máu cơ tim, bệnh mạch vành, đã phẫu thuật tim hoặc đặt stent.\n\n💡 Liên quan đến tiểu đường:\nBệnh tim và tiểu đường chia sẻ nhiều yếu tố nguy cơ chung (béo phì, huyết áp cao).\n\n💬 Chọn CÓ nếu bạn đã từng bị nhồi máu cơ tim hoặc được chẩn đoán bệnh mạch vành"
                )
                LightRegToggleRow(
                    viewModel.regStroke, { viewModel.regStroke = it }, "Đột quỵ",
                    label2 = "Tiền sử đột quỵ",
                    description = "🧠 Đột quỵ là gì?\nĐột quỵ xảy ra khi lưu lượng máu lên não bị gián đoạn đột ngột do tắc mạch hoặc vỡ mạch.\n\n💡 Liên quan đến tiểu đường:\nĐường huyết cao làm tổn thương mạch máu não — đột quỵ và tiểu đường liên quan chặt.\n\n💬 Chọn CÓ nếu bác sĩ xác nhận bạn từng bị đột quỵ hoặc thiếu máu não thoáng qua (TIA)"
                )
                LightRegToggleRow(
                    viewModel.regDiffWalk, { viewModel.regDiffWalk = it }, "Khó khăn đi lại",
                    isLast = true,
                    label2 = "Khó đi bộ hoặc leo cầu thang",
                    description = "🚶 Khó khăn đi lại là gì?\nBạn có thường xuyên gặp khó khăn nghiêm trọng khi đi bộ hoặc leo cầu thang không?\n\n💡 Tại sao cần?\nKhó đi lại phản ánh tình trạng ít vận động hoặc biến chứng thần kinh — đều là yếu tố liên quan đến tiểu đường.\n\n💬 Chọn CÓ nếu bạn thường gặp khó khăn khi đi bộ ngắn hoặc leo 1 tầng cầu thang"
                )
            }
        }

        if (viewModel.registerError != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = AccentRed, modifier = Modifier.size(24.dp).padding(end = 8.dp))
                    Text(text = viewModel.registerError!!, color = AccentRed, fontWeight = FontWeight.Medium, fontSize = 15.sp, modifier = Modifier.weight(1f))
                }
            }
        }

        // ACTIONS
        if (viewModel.isLoading) {
            CircularProgressIndicator(color = AppleLightBlueAction, modifier = Modifier.padding(16.dp))
        } else {
            Button(
                onClick = { viewModel.register() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlueAction),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("Tạo mới tài khoản", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Text("Đã có tài khoản? Quay lại đăng nhập", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
            }
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ─── Input Row có hỗ trợ icon "i" ───────────────────────────────────────────
@Composable
fun LightRegInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isLast: Boolean = false,
    isPassword: Boolean = false,
    isNumber: Boolean = false,
    label2: String? = null,
    description: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                if (description != null) {
                    IconButton(onClick = { showDialog = true }, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                        Box(
                            modifier = Modifier.size(18.dp).background(AppleLightBlueAction, androidx.compose.foundation.shape.CircleShape),
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
                textStyle = TextStyle(color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End),
                keyboardOptions = KeyboardOptions(keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text),
                visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
                modifier = Modifier.width(150.dp),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterEnd) {
                        if (value.isEmpty()) Text("Nhập", color = Color.LightGray, fontSize = 16.sp, textAlign = TextAlign.End)
                        innerTextField()
                    }
                }
            )
        }
        if (!isLast) Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
    }

    if (showDialog && description != null) {
        RegInfoDialog(label2 ?: label, description) { showDialog = false }
    }
}

// ─── Toggle Row (Có / Không) cho trường y tế ─────────────────────────────────
@Composable
fun LightRegToggleRow(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    isLast: Boolean = false,
    label2: String? = null,
    description: String? = null
) {
    var showDialog by remember { mutableStateOf(false) }
    val isYes = value == "1"
    val isNo = value == "0"

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                Text(text = label, color = Color.Black, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                if (description != null) {
                    IconButton(onClick = { showDialog = true }, modifier = Modifier.size(28.dp).padding(start = 4.dp)) {
                        Box(
                            modifier = Modifier.size(18.dp).background(AppleLightBlueAction, androidx.compose.foundation.shape.CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("i", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isNo) Color(0xFF007AFF) else Color(0xFFF0F0F0))
                        .clickable { onValueChange("0") }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Không",
                        color = if (isNo) Color.White else Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = if (isNo) FontWeight.Bold else FontWeight.Normal)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isYes) Color(0xFFFF3B30) else Color(0xFFF0F0F0))
                        .clickable { onValueChange("1") }
                        .padding(horizontal = 16.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Có",
                        color = if (isYes) Color.White else Color.Gray,
                        fontSize = 13.sp,
                        fontWeight = if (isYes) FontWeight.Bold else FontWeight.Normal)
                }
            }
        }
        if (!isLast) Divider(color = Color(0xFFF0F0F0), thickness = 1.dp)
    }

    if (showDialog && description != null) {
        RegInfoDialog(label2 ?: label, description) { showDialog = false }
    }
}

// ─── Dialog dùng chung ───────────────────────────────────────────────────────
@Composable
fun RegInfoDialog(title: String, description: String, onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(40.dp).background(AppleLightBlueAction.copy(alpha = 0.12f), androidx.compose.foundation.shape.CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("i", color = AppleLightBlueAction, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF1C1C1E))
                        Text("Hướng dẫn nhập thông tin", fontSize = 12.sp, color = Color.Gray)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = Color(0xFFF0F0F0))
                Spacer(modifier = Modifier.height(12.dp))
                Column(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 360.dp).verticalScroll(rememberScrollState())
                ) {
                    description.split("\n").forEach { line ->
                        when {
                            line.startsWith("✅") -> InfoChip(line, Color(0xFF34C759))
                            line.startsWith("⚠️") -> InfoChip(line, Color(0xFFFF9500))
                            line.startsWith("🚨") -> InfoChip(line, Color(0xFFFF3B30))
                            line.startsWith("💡") -> InfoChip(line, Color(0xFF007AFF))
                            line.startsWith("💬") -> InfoChip(line, Color(0xFF8E8E93))
                            line.startsWith("•") -> Text(line, fontSize = 14.sp, color = Color(0xFF3A3A3C), lineHeight = 22.sp, modifier = Modifier.padding(start = 8.dp, bottom = 2.dp))
                            line.isEmpty() -> Spacer(modifier = Modifier.height(8.dp))
                            else -> Text(line, fontSize = 14.sp, color = Color(0xFF3A3A3C), lineHeight = 22.sp,
                                fontWeight = if (line.contains("?")) FontWeight.SemiBold else FontWeight.Normal,
                                modifier = Modifier.padding(bottom = 2.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AppleLightBlueAction),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Đã hiểu rồi ✓", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}
