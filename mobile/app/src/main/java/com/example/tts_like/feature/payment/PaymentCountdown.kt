package com.example.tts_like.feature.payment

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.delay
import kotlin.math.max

@Composable
fun PaymentCountdown(expireAt: Long?, onTimeout: () -> Unit) {
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    val remain = max(0L, (expireAt ?: now) - now)
    val minutes = remain / 1000 / 60
    val seconds = remain / 1000 % 60
    val urgent = remain in 1..120_000

    LaunchedEffect(expireAt) {
        while (expireAt != null && System.currentTimeMillis() < expireAt) {
            now = System.currentTimeMillis()
            delay(1000)
        }
        if (expireAt != null) onTimeout()
    }

    Text(
        text = "支付剩余 %02d:%02d".format(minutes, seconds),
        color = if (urgent) Color(0xFFB3261E) else MaterialTheme.colorScheme.primary,
        fontWeight = FontWeight.Bold,
    )
}
