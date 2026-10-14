package com.example.doanckapi.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.doanckapi.data.db.UserDao
import com.example.doanckapi.data.db.UserEntity
import kotlinx.coroutines.launch

class AuthViewModel(private val userDao: UserDao) : ViewModel() {

    // Register State
    var regUsername by mutableStateOf("")
    var regPassword by mutableStateOf("")
    var regConfirmPassword by mutableStateOf("")
    
    // Profile Fields
    var regAge by mutableStateOf("")
    var regIncome by mutableStateOf("")
    var regBMI by mutableStateOf("")
    var regHighBP by mutableStateOf("")
    var regHighChol by mutableStateOf("")
    var regHeartDisease by mutableStateOf("")
    var regStroke by mutableStateOf("")
    var regDiffWalk by mutableStateOf("")
    var regSex by mutableStateOf(-1) // 0: Nữ, 1: Nam
    var regPregnancies by mutableStateOf("")
    var registerError by mutableStateOf<String?>(null)
    var registerSuccess by mutableStateOf(false)

    // Login State
    var loginUsername by mutableStateOf("")
    var loginPassword by mutableStateOf("")
    var loginError by mutableStateOf<String?>(null)
    var loginSuccess by mutableStateOf(false)
    var loggedInUserId by mutableStateOf<Int?>(null)

    var isLoading by mutableStateOf(false)

    fun login() {
        if (loginUsername.isBlank() || loginPassword.isBlank()) {
            loginError = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        isLoading = true
        loginError = null

        viewModelScope.launch {
            try {
                val user = userDao.getUserByUsername(loginUsername)
                if (user != null && user.passwordHash == loginPassword) {
                    loginSuccess = true
                    loggedInUserId = user.id
                } else {
                    loginError = "Sai tên đăng nhập hoặc mật khẩu"
                }
            } catch (e: Exception) {
                loginError = "Đã xảy ra lỗi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun register() {
        if (regUsername.isBlank() || regPassword.isBlank() || regConfirmPassword.isBlank()) {
            registerError = "Vui lòng nhập đầy đủ thông tin đăng nhập"
            return
        }
        if (regUsername.length < 3) {
            registerError = "Tên đăng nhập phải có ít nhất 3 ký tự"
            return
        }
        if (regPassword != regConfirmPassword) {
            registerError = "Mật khẩu xác nhận không khớp"
            return
        }
        if (regSex == -1) {
            registerError = "Vui lòng chọn giới tính"
            return
        }
        if (regHighBP.isBlank() || regHighChol.isBlank() || regHeartDisease.isBlank() || regStroke.isBlank() || regDiffWalk.isBlank()) {
            registerError = "Vui lòng chọn đầy đủ Có/Không cho phần Tiền sử bệnh lý"
            return
        }
        val age = regAge.toFloatOrNull()
        if (age == null || age < 18 || age > 80) {
            registerError = "Tuổi phải nằm trong khoảng 18 – 80"
            return
        }
        val income = regIncome.toFloatOrNull()
        if (income == null || income < 1 || income > 8) {
            registerError = "Thu nhập phải từ 1 đến 8"
            return
        }
        val bmi = regBMI.toFloatOrNull()
        if (bmi == null || bmi < 10f || bmi > 100f) {
            registerError = "BMI không hợp lệ (thường từ 15.0 đến 60.0). Kiểm tra lại công thức: Cân nặng(kg) ÷ Chiều cao²(m)"
            return
        }

        isLoading = true
        registerError = null

        viewModelScope.launch {
            try {
                val existingUser = userDao.getUserByUsername(regUsername)
                if (existingUser != null) {
                    registerError = "Tên đăng nhập đã tồn tại! Vui lòng chọn tên khác."
                } else {
                    val newUser = UserEntity(
                        username = regUsername,
                        passwordHash = regPassword,
                        Age = age,
                        Income = income,
                        BMI = bmi,
                        HighBP = regHighBP.toFloatOrNull() ?: 0f,
                        HighChol = regHighChol.toFloatOrNull() ?: 0f,
                        HeartDiseaseorAttack = regHeartDisease.toFloatOrNull() ?: 0f,
                        Stroke = regStroke.toFloatOrNull() ?: 0f,
                        DiffWalk = regDiffWalk.toFloatOrNull() ?: 0f,
                        Sex = regSex,
                        Pregnancies = if (regSex == 0) (regPregnancies.toIntOrNull() ?: 0) else 0
                    )
                    userDao.insertUser(newUser)
                    registerSuccess = true
                    loginUsername = regUsername
                    loginPassword = regPassword
                }
            } catch (e: Exception) {
                registerError = "Đã xảy ra lỗi: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun resetState() {
        registerError = null
        registerSuccess = false
        loginError = null
        loginSuccess = false
    }

    fun logout() {
        loginSuccess = false
        loginUsername = ""
        loginPassword = ""
        loggedInUserId = null
        resetState()
    }

    // Management State
    var userList by mutableStateOf<List<UserEntity>>(emptyList())
    var managementError by mutableStateOf<String?>(null)

    fun loadAllUsers() {
        viewModelScope.launch {
            try {
                userList = userDao.getAllUsers()
            } catch (e: Exception) {
                managementError = "Không thể tải danh sách tài khoản: ${e.message}"
            }
        }
    }

    fun deleteUser(userId: Int, onSelfDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                userDao.deleteUserById(userId)
                loadAllUsers() // Reload the list
                if (loggedInUserId == userId) {
                    logout()
                    onSelfDeleted()
                }
            } catch (e: Exception) {
                managementError = "Không thể xoá tài khoản: ${e.message}"
            }
        }
    }
}

class AuthViewModelFactory(private val userDao: UserDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AuthViewModel(userDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
