package com.example.myapplication.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.myapplication.screen.EditScreen
import com.example.myapplication.screen.HomeScreen
import com.example.myapplication.screen.InsertScreen
import com.example.myapplication.screen.Screen

@Composable
fun NavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(
            route = Screen.Home.route
        ) {
            HomeScreen(navController)
        }
        composable(
            route = Screen.Insert.route
        ) {
            InsertScreen(navController)
        }
        composable(
            route = Screen.Edit.route
        ) {
            EditScreen(navController)
        }
    }
}