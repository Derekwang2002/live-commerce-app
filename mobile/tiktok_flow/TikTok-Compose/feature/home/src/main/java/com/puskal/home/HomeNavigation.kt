package com.puskal.home

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.puskal.core.DestinationRoute.FORMATTED_LIVE_ROOM_ROUTE
import com.puskal.core.DestinationRoute.HOME_SCREEN_ROUTE
import com.puskal.core.DestinationRoute.PassedKey.ROOM_ID
import com.puskal.home.live.LiveRoomScreen

/**
 * Created by Puskal Khadka on 3/14/2023.
 */

fun NavGraphBuilder.homeNavGraph(navController: NavController) {
    composable(route = HOME_SCREEN_ROUTE) {
        HomeScreen(navController)
    }
    composable(
        route = FORMATTED_LIVE_ROOM_ROUTE,
        arguments = listOf(navArgument(ROOM_ID) { type = NavType.StringType })
    ) {
        LiveRoomScreen(navController = navController)
    }
}
