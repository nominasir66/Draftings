package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.screens.EditorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EditorViewModel
import com.example.viewmodel.HomeViewModel
import com.example.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settings by settingsViewModel.settings.collectAsState()

            val isDark = when (settings.isDarkMode) {
                true -> true
                false -> false
                null -> isSystemInDarkTheme()
            }

            MyApplicationTheme(darkTheme = isDark) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    DraftingsApp(settingsViewModel = settingsViewModel)
                }
            }
        }
    }
}

@Composable
fun DraftingsApp(settingsViewModel: SettingsViewModel) {
    val navController = rememberNavController()
    val homeViewModel: HomeViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                viewModel = homeViewModel,
                onOpenDocument = { documentId ->
                    navController.navigate("editor/$documentId")
                },
                onOpenTemplates = {
                    navController.navigate("templates")
                },
                onOpenSettings = {
                    navController.navigate("settings")
                }
            )
        }

        composable(
            route = "editor/{documentId}",
            arguments = listOf(navArgument("documentId") { type = NavType.LongType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getLong("documentId") ?: 0L
            val editorViewModel: EditorViewModel = viewModel()
            EditorScreen(
                documentId = documentId,
                viewModel = editorViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("templates") {
            TemplatesScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onUseTemplate = { template ->
                    homeViewModel.createFromTemplate(template) { newId ->
                        navController.navigate("editor/$newId") {
                            popUpTo("home")
                        }
                    }
                }
            )
        }

        composable("settings") {
            SettingsScreen(
                viewModel = settingsViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
