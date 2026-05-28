package com.example.tts_like.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tts_like.feature.cart.CartScreen
import com.example.tts_like.feature.feed.FeedScreen
import com.example.tts_like.feature.order.OrderConfirmScreen
import com.example.tts_like.feature.order.OrderDetailScreen
import com.example.tts_like.feature.order.OrderListScreen
import com.example.tts_like.feature.payment.PaymentFailedScreen
import com.example.tts_like.feature.payment.PaymentScreen
import com.example.tts_like.feature.payment.PaymentSuccessScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.Feed.route,
    ) {
        composable(Screen.Feed.route) {
            FeedScreen(navController = navController)
        }
        composable(Screen.Cart.route) {
            CartScreen(navController = navController)
        }
        composable(Screen.OrderConfirm.route) {
            OrderConfirmScreen(navController = navController)
        }
        composable(Screen.OrderList.route) {
            OrderListScreen(navController = navController)
        }
        composable(Screen.OrderDetail.route) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreen(navController = navController, orderId = orderId)
        }
        composable(Screen.Payment.route) { backStackEntry ->
            val orderNo = backStackEntry.arguments?.getString("orderNo") ?: ""
            PaymentScreen(navController = navController, orderNo = orderNo)
        }
        composable(Screen.PaymentSuccess.route) { backStackEntry ->
            val orderNo = backStackEntry.arguments?.getString("orderNo") ?: ""
            PaymentSuccessScreen(navController = navController, orderNo = orderNo)
        }
        composable(Screen.PaymentFailed.route) { backStackEntry ->
            val orderNo = backStackEntry.arguments?.getString("orderNo") ?: ""
            PaymentFailedScreen(navController = navController, orderNo = orderNo)
        }
    }
}
