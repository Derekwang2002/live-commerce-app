package com.puskal.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.puskal.home.tab.following.FollowingScreen
import com.puskal.home.tab.foryou.ForYouTabScreen
import com.puskal.theme.R
import com.puskal.theme.TikTokTheme
import com.puskal.theme.White
import kotlinx.coroutines.launch

/**
 * Created by Puskal Khadka on 3/14/2023.
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    navController: NavController
) {
    val pagerState = rememberPagerState(initialPage = 1)
    val coroutineScope = rememberCoroutineScope()
    val navTabs = listOf("团购", "武汉", "关注", "商城", "推荐")

    TikTokTheme(darkTheme = true) {
        Box(modifier = Modifier.fillMaxSize()) {

            HorizontalPager(
                pageCount = 2,
                state = pagerState,
            ) {
                when (it) {
                    0 -> FollowingScreen(navController, pagerState)
                    1 -> ForYouTabScreen(navController)
                }
            }
            LiveChineseTopNav(
                selectedIndex = if (pagerState.currentPage == 0) 2 else 4,
                tabs = navTabs,
                onTabClick = { index ->
                    val targetPage = when (index) {
                        2 -> 0
                        4 -> 1
                        else -> 1
                    }
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(targetPage)
                    }
                },
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }


}

@Composable
private fun LiveChineseTopNav(
    selectedIndex: Int,
    tabs: List<String>,
    onTabClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            Color.Black.copy(alpha = 0.42f),
                            Color.Black.copy(alpha = 0.18f),
                            Color.Transparent
                        )
                    )
                )
                .padding(start = 8.dp, top = 8.dp, end = 8.dp, bottom = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_list),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    tabs.forEachIndexed { index, label ->
                        val selected = selectedIndex == index
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { onTabClick(index) }
                        ) {
                            Text(
                                text = label,
                                color = if (selected) Color.White else Color.White.copy(alpha = 0.58f),
                                fontWeight = if (selected) FontWeight.Black else FontWeight.Bold,
                                fontSize = if (selected) 22.sp else 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                softWrap = false
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(
                                modifier = Modifier
                                    .height(4.dp)
                                    .width(if (selected) 34.dp else 0.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(99.dp)
                                    )
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier.size(34.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

