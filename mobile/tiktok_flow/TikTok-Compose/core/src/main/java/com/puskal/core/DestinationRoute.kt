package com.puskal.core

import com.puskal.core.DestinationRoute.PassedKey.USER_ID
import com.puskal.core.DestinationRoute.PassedKey.VIDEO_INDEX
import com.puskal.core.DestinationRoute.PassedKey.ROOM_ID
import com.puskal.core.DestinationRoute.PassedKey.VIDEO_ID
import com.puskal.core.DestinationRoute.PassedKey.PRODUCT_ID
import com.puskal.core.DestinationRoute.PassedKey.QUERY
import com.puskal.core.DestinationRoute.PassedKey.ORDER_SOURCE
import com.puskal.core.DestinationRoute.PassedKey.ORDER_ID
import com.puskal.core.DestinationRoute.PassedKey.ORDER_NO

/**
 * Created by Puskal Khadka on 3/19/2023.
 */
object DestinationRoute {
    const val HOME_SCREEN_ROUTE = "home_screen_route"
    const val COMMENT_BOTTOM_SHEET_ROUTE = "comment_bottom_sheet_route"
    const val FORMATTED_COMMENT_BOTTOM_SHEET_ROUTE = "$COMMENT_BOTTOM_SHEET_ROUTE/{$VIDEO_ID}"
    const val CREATOR_PROFILE_ROUTE = "creator_profile_route"

    const val CREATOR_VIDEO_ROUTE = "creator_video_route"
    const val FORMATTED_COMPLETE_CREATOR_VIDEO_ROUTE =
        "$CREATOR_VIDEO_ROUTE/{$USER_ID}/{$VIDEO_INDEX}"

    const val INBOX_ROUTE = "inbox_route"
    const val MY_PROFILE_ROUTE = "my_profile_route"
    const val FRIENDS_ROUTE = "friends_route"
    const val CAMERA_ROUTE = "camera_route"

    const val AUTHENTICATION_ROUTE = "authentication_route"
    const val LOGIN_OR_SIGNUP_WITH_PHONE_EMAIL_ROUTE = "login_signup_phone_email_route"

    const val SETTING_ROUTE="setting_route"
    const val LIVE_ROOM_ROUTE = "live_room_route"
    const val FORMATTED_LIVE_ROOM_ROUTE = "$LIVE_ROOM_ROUTE/{$ROOM_ID}"
    const val PRODUCT_DETAIL_ROUTE = "product_detail_route"
    const val FORMATTED_PRODUCT_DETAIL_ROUTE = "$PRODUCT_DETAIL_ROUTE/{$PRODUCT_ID}"
    const val PRODUCT_SEARCH_ROUTE = "product_search_route"
    const val FORMATTED_PRODUCT_SEARCH_ROUTE = "$PRODUCT_SEARCH_ROUTE/{$QUERY}"
    const val CART_ROUTE = "cart_route"
    const val ORDER_CONFIRM_ROUTE = "order_confirm_route"
    const val FORMATTED_ORDER_CONFIRM_ROUTE = "$ORDER_CONFIRM_ROUTE/{$ORDER_SOURCE}"
    const val ORDER_LIST_ROUTE = "order_list_route"
    const val ORDER_DETAIL_ROUTE = "order_detail_route"
    const val FORMATTED_ORDER_DETAIL_ROUTE = "$ORDER_DETAIL_ROUTE/{$ORDER_ID}"
    const val PAYMENT_ROUTE = "payment_route"
    const val FORMATTED_PAYMENT_ROUTE = "$PAYMENT_ROUTE/{$ORDER_NO}"

    object PassedKey {
        const val USER_ID = "user_id"
        const val VIDEO_INDEX = "video_index"
        const val ROOM_ID = "room_id"
        const val VIDEO_ID = "video_id"
        const val PRODUCT_ID = "product_id"
        const val QUERY = "query"
        const val ORDER_SOURCE = "order_source"
        const val ORDER_ID = "order_id"
        const val ORDER_NO = "order_no"
    }
}
