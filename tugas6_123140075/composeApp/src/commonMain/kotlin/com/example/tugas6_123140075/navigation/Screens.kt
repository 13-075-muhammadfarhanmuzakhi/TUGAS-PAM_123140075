package com.example.tugas6_123140075.navigation

sealed class Screen(val route: String) {
    data object NewsList : Screen("news_list")

    data object NewsDetail : Screen("news_detail/{articleIndex}") {
        fun createRoute(articleIndex: Int) = "news_detail/$articleIndex"
    }
}