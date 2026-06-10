package com.puskal.commerce.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.puskal.commerce.data.CommerceRepository
import com.puskal.commerce.screen.CartScreen
import com.puskal.commerce.screen.OrderConfirmScreen
import com.puskal.commerce.screen.OrderDetailScreen
import com.puskal.commerce.screen.OrderListScreen
import com.puskal.commerce.screen.PaymentScreen
import com.puskal.commerce.screen.ProductDetailScreen
import com.puskal.commerce.screen.ProductSearchResultScreen
import com.puskal.core.DestinationRoute.CART_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_ORDER_CONFIRM_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_ORDER_DETAIL_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_PAYMENT_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_PRODUCT_DETAIL_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_PRODUCT_SEARCH_ROUTE
import com.puskal.core.DestinationRoute.ORDER_CONFIRM_ROUTE
import com.puskal.core.DestinationRoute.ORDER_DETAIL_ROUTE
import com.puskal.core.DestinationRoute.ORDER_LIST_ROUTE
import com.puskal.core.DestinationRoute.PAYMENT_ROUTE
import com.puskal.core.DestinationRoute.PRODUCT_DETAIL_ROUTE
import com.puskal.core.DestinationRoute.PRODUCT_SEARCH_ROUTE
import com.puskal.core.DestinationRoute.PassedKey.ORDER_ID
import com.puskal.core.DestinationRoute.PassedKey.ORDER_NO
import com.puskal.core.DestinationRoute.PassedKey.ORDER_SOURCE
import com.puskal.core.DestinationRoute.PassedKey.PRODUCT_ID
import com.puskal.core.DestinationRoute.PassedKey.QUERY

fun NavGraphBuilder.commerceNavGraph(navController: NavController) {
    composable(
        route = FORMATTED_PRODUCT_DETAIL_ROUTE,
        arguments = listOf(navArgument(PRODUCT_ID) { type = NavType.StringType })
    ) { backStackEntry ->
        ProductDetailScreen(
            navController = navController,
            productId = backStackEntry.arguments?.getString(PRODUCT_ID).orEmpty()
        )
    }
    composable(
        route = FORMATTED_PRODUCT_SEARCH_ROUTE,
        arguments = listOf(navArgument(QUERY) { type = NavType.StringType })
    ) { backStackEntry ->
        ProductSearchResultScreen(
            navController = navController,
            query = Uri.decode(backStackEntry.arguments?.getString(QUERY).orEmpty())
        )
    }
    composable(route = CART_ROUTE) {
        CartScreen(navController = navController)
    }
    composable(
        route = FORMATTED_ORDER_CONFIRM_ROUTE,
        arguments = listOf(navArgument(ORDER_SOURCE) { type = NavType.StringType })
    ) { backStackEntry ->
        OrderConfirmScreen(
            navController = navController,
            source = Uri.decode(backStackEntry.arguments?.getString(ORDER_SOURCE).orEmpty())
        )
    }
    composable(route = ORDER_LIST_ROUTE) {
        OrderListScreen(navController = navController)
    }
    composable(
        route = FORMATTED_ORDER_DETAIL_ROUTE,
        arguments = listOf(navArgument(ORDER_ID) { type = NavType.StringType })
    ) { backStackEntry ->
        OrderDetailScreen(
            navController = navController,
            orderId = backStackEntry.arguments?.getString(ORDER_ID).orEmpty()
        )
    }
    composable(
        route = FORMATTED_PAYMENT_ROUTE,
        arguments = listOf(navArgument(ORDER_NO) { type = NavType.StringType })
    ) { backStackEntry ->
        PaymentScreen(
            navController = navController,
            orderNo = backStackEntry.arguments?.getString(ORDER_NO).orEmpty()
        )
    }
}

fun NavController.navigateToCommerceEntry(entryKey: String) {
    navigate(resolveCommerceEntryRoute(entryKey))
}

fun NavController.navigateToProductDetail(productId: String) {
    navigate(productDetailRoute(productId))
}

fun NavController.navigateToProductSearch(query: String) {
    navigate(productSearchRoute(query))
}

fun productDetailRoute(productId: String): String = "$PRODUCT_DETAIL_ROUTE/${Uri.encode(productId)}"

fun productSearchRoute(query: String): String = "$PRODUCT_SEARCH_ROUTE/${Uri.encode(query)}"

fun cartRoute(): String = CART_ROUTE

fun buyNowOrderRoute(productId: String, skuId: String): String {
    val source = CommerceRepository.createBuyNowSource(productId, skuId)
    return "$ORDER_CONFIRM_ROUTE/${Uri.encode(source)}"
}

fun cartOrderRoute(): String = "$ORDER_CONFIRM_ROUTE/${Uri.encode(CommerceRepository.CART_ORDER_SOURCE)}"

fun paymentRoute(orderNo: String): String = "$PAYMENT_ROUTE/${Uri.encode(orderNo)}"

fun orderDetailRoute(orderId: String): String = "$ORDER_DETAIL_ROUTE/${Uri.encode(orderId)}"

private fun resolveCommerceEntryRoute(entryKey: String): String {
    val key = entryKey.trim()
    return when {
        key.startsWith("tiktokflow://product/") -> productDetailRoute(key.substringAfterLast("/"))
        key.startsWith("product:") -> productDetailRoute(key.substringAfter("product:"))
        key.startsWith("recommend:") -> productDetailRoute(key.substringAfter("recommend:"))
        key == "author_recommend_url" -> productDetailRoute("prod_1")
        key.startsWith("tiktokflow://ad/") -> productDetailRoute("prod_1")
        key.startsWith("ad:") -> productDetailRoute("prod_1")
        key.startsWith("search:") -> productSearchRoute(key.substringAfter("search:"))
        key == "honor_600_search_url" -> productSearchRoute("荣耀600")
        key.isBlank() -> productDetailRoute("prod_1")
        else -> productDetailRoute(key)
    }
}
