package com.example.doanckapi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doanckapi.data.db.HistoryEntity
import com.example.doanckapi.data.model.PredictionRequest
import com.example.doanckapi.data.repository.PredictionRepository
import com.example.doanckapi.data.db.UserDao
import com.example.doanckapi.data.db.UserEntity
import kotlinx.coroutines.launch

class PredictViewModel(private val repository: PredictionRepository, private val userDao: UserDao) : ViewModel() {
    var currentUserId: Int = -1

    var userProfile by mutableStateOf<UserEntity?>(null)

    // Dynamic inputs
    var GenHlth by mutableStateOf("")
    var MentHlth by mutableStateOf("")
    var PhysHlth by mutableStateOf("")
    var PhysActivity by mutableStateOf("")
    var Glucose by mutableStateOf("")
    var Insulin by mutableStateOf("")

    var isLoading by mutableStateOf(false)
    var predictionResult by mutableStateOf<Int?>(null)
    var diabetesType by mutableStateOf<String?>(null)
    var insulinRecommendation by mutableStateOf<String?>(null)
    var longevityImpact by mutableStateOf<String?>(null)
    var suggestions by mutableStateOf<List<String>?>(null)
    var errorMessage by mutableStateOf<String?>(null)

    fun loadUserProfile(userId: Int) {
        currentUserId = userId
        viewModelScope.launch {
            userProfile = userDao.getUserById(userId)
        }
    }

    fun predict() {
        if (!validateInputs() || userProfile == null) {
            if (userProfile == null) errorMessage = "Không tìm thấy hồ sơ người dùng"
            return
        }

        val profile = userProfile!!

        isLoading = true
        errorMessage = null
        val request = PredictionRequest(
            HighBP = profile.HighBP,
            HighChol = profile.HighChol,
            HeartDiseaseorAttack = profile.HeartDiseaseorAttack,
            Stroke = profile.Stroke,
            GenHlth = GenHlth.toFloat(),
            MentHlth = MentHlth.toFloat(),
            PhysHlth = PhysHlth.toFloat(),
            BMI = profile.BMI,
            Age = profile.Age,
            Income = profile.Income,
            PhysActivity = PhysActivity.toFloat(),
            DiffWalk = profile.DiffWalk,
            Sex = profile.Sex,
            Pregnancies = profile.Pregnancies,
            Glucose = Glucose.toFloatOrNull() ?: -1.0f,
            Insulin = Insulin.toFloatOrNull() ?: -1.0f
        )

        viewModelScope.launch {
            try {
                val response = repository.predictDiabetes(request)
                predictionResult = response.predictionLabel
                diabetesType = response.diabetesType
                insulinRecommendation = response.insulinRecommendation
                longevityImpact = response.longevityImpact
                suggestions = response.suggestions
                
                // Save to history
                repository.saveHistory(
                    HistoryEntity(
                        userId = currentUserId,
                        timestamp = System.currentTimeMillis(),
                        HighBP = request.HighBP,
                        HighChol = request.HighChol,
                        HeartDiseaseorAttack = request.HeartDiseaseorAttack,
                        Stroke = request.Stroke,
                        GenHlth = request.GenHlth,
                        MentHlth = request.MentHlth,
                        PhysHlth = request.PhysHlth,
                        BMI = request.BMI,
                        Age = request.Age,
                        Income = request.Income,
                        PhysActivity = request.PhysActivity,
                        DiffWalk = request.DiffWalk,
                        predictionResult = response.predictionLabel,
                        diabetesType = response.diabetesType
                    )
                )
            } catch (e: Exception) {
                errorMessage = "Lỗi kết nối API: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun reset() {
        GenHlth = ""
        MentHlth = ""
        PhysHlth = ""
        PhysActivity = ""
        Glucose = ""
        Insulin = ""
        predictionResult = null
        diabetesType = null
        insulinRecommendation = null
        longevityImpact = null
        suggestions = null
        errorMessage = null
    }

    private fun validateInputs(): Boolean {
        if (GenHlth.isBlank() || MentHlth.isBlank() || PhysHlth.isBlank() || PhysActivity.isBlank()) {
            errorMessage = "Vui lòng nhập đầy đủ 4 chỉ số hôm nay"
            return false
        }
        try {
            GenHlth.toFloat(); MentHlth.toFloat(); PhysHlth.toFloat(); PhysActivity.toFloat()
        } catch (e: NumberFormatException) {
            errorMessage = "Dữ liệu nhập vào phải là số hợp lệ"
            return false
        }
        return true
    }
}

class PredictViewModelFactory(private val repository: PredictionRepository, private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PredictViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PredictViewModel(repository, userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
