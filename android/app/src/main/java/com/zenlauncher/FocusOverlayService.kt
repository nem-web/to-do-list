package com.zenlauncher

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FocusOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (overlayView != null) return START_NOT_STICKY

        val pkg = intent?.getStringExtra(EXTRA_TARGET_PACKAGE).orEmpty()
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.CENTER }

        overlayView = ComposeView(this).apply {
            setContent {
                FocusGateScreen(
                    onBackToFocus = { stopSelf() },
                    onOpenLimited = {
                        stopSelf()
                        packageManager.getLaunchIntentForPackage(pkg)?.let {
                            it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(it)
                        }
                    }
                )
            }
        }

        windowManager?.addView(overlayView, params)
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        super.onDestroy()
    }

    companion object {
        const val EXTRA_TARGET_PACKAGE = "target_package"
    }
}

@Composable
private fun FocusGateScreen(
    onBackToFocus: () -> Unit,
    onOpenLimited: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val secondsLeft = remember { mutableIntStateOf(15) }

    val transition = rememberInfiniteTransition(label = "breath")
    val circleScale by transition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "circle"
    )

    androidx.compose.runtime.LaunchedEffect(Unit) {
        while (secondsLeft.intValue > 0) {
            delay(1000)
            secondsLeft.intValue -= 1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .scale(circleScale)
                    .background(Color(0xFF303030), CircleShape)
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "Is this reel worth your future at IIT?",
                style = TextStyle(color = Color(0xFFE5E5E5), fontFamily = FontFamily.Serif, fontSize = 22.sp)
            )
            Spacer(Modifier.height(10.dp))
            Text(
                text = if (secondsLeft.intValue > 0) "Wait ${secondsLeft.intValue}s" else "Choose intentionally",
                style = TextStyle(color = Color(0xFF8E8E8E), fontFamily = FontFamily.Serif, fontSize = 16.sp)
            )
            Spacer(Modifier.height(28.dp))

            if (secondsLeft.intValue == 0) {
                Button(onClick = {
                    scope.launch { onOpenLimited() }
                }) {
                    Text("Open for 5 minutes only")
                }
                Spacer(Modifier.height(10.dp))
                Button(onClick = onBackToFocus) {
                    Text("Back to Focus")
                }
            }
        }
    }
}
