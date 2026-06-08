package com.puskal.commentlisting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.puskal.commerce.navigation.navigateToCommerceEntry
import com.puskal.core.DestinationRoute.COMMENT_BOTTOM_SHEET_ROUTE
import com.puskal.core.DestinationRoute.FORMATTED_COMMENT_BOTTOM_SHEET_ROUTE

/**
 * Created by Puskal Khadka on 3/22/2023.
 */

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.commentListingNavGraph(navController: NavController) {
    bottomSheet(route = COMMENT_BOTTOM_SHEET_ROUTE) {
        CommentListScreen(
            onClickCancel = { navController.navigateUp() },
            onClickSearchRecommendation = { key ->
                navController.navigateUp()
                navController.navigateToCommerceEntry(key)
            },
            onClickAuthorRecommend = { key ->
                navController.navigateUp()
                navController.navigateToCommerceEntry(key)
            }
        )
    }
    bottomSheet(route = FORMATTED_COMMENT_BOTTOM_SHEET_ROUTE) {
        CommentListScreen(
            onClickCancel = { navController.navigateUp() },
            onClickSearchRecommendation = { key ->
                navController.navigateUp()
                navController.navigateToCommerceEntry(key)
            },
            onClickAuthorRecommend = { key ->
                navController.navigateUp()
                navController.navigateToCommerceEntry(key)
            }
        )
    }
}
