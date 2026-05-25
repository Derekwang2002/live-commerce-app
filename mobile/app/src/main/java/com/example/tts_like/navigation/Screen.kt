package com.example.tts_like.navigation

sealed class Screen(val route: String) {
    object Feed : Screen("feed")
    object Cart : Screen("cart")
    object OrderConfirm : Screen("order_confirm")
    object OrderList : Screen("order_list")
    object OrderDetail : Screen("order_detail/{orderId}") {
        fun createRoute(orderId: String) = "order_detail/$orderId"
    }
    object Payment : Screen("payment/{orderNo}") {
        fun createRoute(orderNo: String) = "payment/$orderNo"
    }
}