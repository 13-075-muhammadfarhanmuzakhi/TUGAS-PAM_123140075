package com.example.tugas6_123140075.screens

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.tugas6_123140075.navigation.Screen
import com.example.tugas6_123140075.viewmodel.NewsUiState
import com.example.tugas6_123140075.viewmodel.NewsViewModel

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val viewModel: NewsViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.NewsList.route
    ) {
        composable(route = Screen.NewsList.route) {
            NewsListScreen(
                viewModel = viewModel,
                onArticleClick = { index ->
                    navController.navigate(Screen.NewsDetail.createRoute(index))
                }
            )
        }

        composable(
            route = Screen.NewsDetail.route,
            arguments = listOf(
                navArgument("articleIndex") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val articleIndex = backStackEntry.arguments?.getInt("articleIndex") ?: 0
            val articles = (viewModel.screenState.value.uiState as? NewsUiState.Success)?.articles ?: emptyList()

            if (articleIndex < articles.size) {
                NewsDetailScreen(
                    article = articles[articleIndex],
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}