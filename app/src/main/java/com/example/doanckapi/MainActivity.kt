package com.example.doanckapi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.doanckapi.ui.navigation.BottomNavigationBar
import com.example.doanckapi.ui.navigation.Screen
import com.example.doanckapi.ui.screens.AboutScreen
import com.example.doanckapi.ui.screens.HistoryScreen
import com.example.doanckapi.ui.screens.HomeScreen
import com.example.doanckapi.ui.screens.PredictScreen
import com.example.doanckapi.ui.screens.LoginScreen
import com.example.doanckapi.ui.screens.RegisterScreen
import com.example.doanckapi.ui.screens.AccountManagementScreen
import com.example.doanckapi.ui.screens.AuthViewModel
import com.example.doanckapi.ui.screens.AuthViewModelFactory
import com.example.doanckapi.data.db.AppDatabase
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.doanckapi.ui.theme.DoanckapiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val systemTheme = androidx.compose.foundation.isSystemInDarkTheme()
            var isDarkTheme by androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(systemTheme) }

            DoanckapiTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val isAuthScreen = currentRoute == Screen.Login.route || currentRoute == Screen.Register.route

                // Provide AuthViewModel
                val appDb = AppDatabase.getDatabase(applicationContext)
                val authViewModel: AuthViewModel = viewModel(factory = AuthViewModelFactory(appDb.userDao()))

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (!isAuthScreen && currentRoute != null) {
                            BottomNavigationBar(navController = navController)
                        }
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Login.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Login.route) { LoginScreen(navController, authViewModel) }
                        composable(Screen.Register.route) { RegisterScreen(navController, authViewModel) }
                        // Fake main_app route to bounce to Home
                        composable("main_app") { 
                            androidx.compose.runtime.LaunchedEffect(Unit) {
                                navController.navigate(Screen.Home.route) {
                                    popUpTo("main_app") { inclusive = true }
                                }
                            }
                        }
                        composable(Screen.Home.route) { 
                            HomeScreen(onNavigateToPredict = {
                                navController.navigate(Screen.Predict.route) {
                                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }) 
                        }
                        composable(Screen.Predict.route) { 
                            val uId = authViewModel.loggedInUserId ?: -1
                            PredictScreen(uId) 
                        }
                        composable(Screen.History.route) { 
                            val uId = authViewModel.loggedInUserId ?: -1
                            HistoryScreen(uId) 
                        }
                        composable(Screen.About.route) { 
                            AboutScreen(
                                navController = navController,
                                authViewModel = authViewModel,
                                isDarkTheme = isDarkTheme,
                                onThemeChange = { isDarkTheme = it }
                            ) 
                        }
                        composable(Screen.ManageAccounts.route) {
                            AccountManagementScreen(
                                navController = navController,
                                authViewModel = authViewModel
                            )
                        }

                    }
                }
            }
        }
    }
}