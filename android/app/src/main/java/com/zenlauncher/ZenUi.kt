package com.zenlauncher

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay

object ZenColors {
    val Black = Color(0xFF000000)
    val MutedWhite = Color(0xFFE5E5E5)
    val MutedGray = Color(0xFF8E8E8E)
}

private val ZenTextStyle = TextStyle(
    color = ZenColors.MutedWhite,
    fontFamily = FontFamily.Serif,
    fontSize = 20.sp
)

@Composable
fun ZenLauncherHome(
    onAppLaunch: (String) -> Unit,
    vm: ZenLauncherViewModel = viewModel()
) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    var codeInput by remember { mutableStateOf("") }

    LaunchedEffect(vm.deepFocusEnabled) {
        while (vm.deepFocusEnabled) {
            delay(1000)
            vm.tickFocusTimer()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ZenColors.Black)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text("ZEN LAUNCHER", style = ZenTextStyle.copy(fontSize = 26.sp, fontWeight = FontWeight.Bold))
        Text("Monk mode for study sessions", style = ZenTextStyle.copy(color = ZenColors.MutedGray, fontSize = 14.sp))

        Spacer(Modifier.height(20.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Deep Focus", style = ZenTextStyle)
            Spacer(Modifier.weight(1f))
            Switch(
                checked = vm.deepFocusEnabled,
                onCheckedChange = {
                    hapticClick(context)
                    vm.toggleFocus(it)
                }
            )
        }

        if (vm.deepFocusEnabled) {
            Text(
                "Session: ${vm.formattedFocusTime()}",
                style = ZenTextStyle.copy(color = ZenColors.MutedGray, fontSize = 16.sp)
            )
        }

        Spacer(Modifier.height(24.dp))
        Text("Essentials", style = ZenTextStyle.copy(fontSize = 18.sp))
        Spacer(Modifier.height(12.dp))

        vm.essentialApps.forEach { app ->
            Text(
                text = app.label,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        hapticClick(context)
                        if (!vm.isBlockedApp(app.packageName)) {
                            onAppLaunch(app.packageName)
                        }
                    }
                    .padding(vertical = 10.dp),
                style = ZenTextStyle
            )
            Divider(color = ZenColors.MutedGray)
        }

        Spacer(Modifier.height(18.dp))
        Text("All Apps (mindfulness gate)", style = ZenTextStyle.copy(fontSize = 16.sp))

        OutlinedTextField(
            value = codeInput,
            onValueChange = {
                if (it.length <= 4) codeInput = it
            },
            textStyle = ZenTextStyle.copy(fontSize = 14.sp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            label = { Text("4-digit daily code", color = ZenColors.MutedGray) },
            modifier = Modifier.fillMaxWidth()
        )

        val unlocked = vm.validateMindfulnessCode(codeInput)
        AnimatedVisibility(unlocked) {
            Column {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    textStyle = ZenTextStyle.copy(fontSize = 14.sp),
                    label = { Text("Search apps", color = ZenColors.MutedGray) },
                    modifier = Modifier.fillMaxWidth()
                )

                LazyColumn {
                    items(vm.filteredApps(query)) { app ->
                        Text(
                            text = app.label,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    hapticClick(context)
                                    if (!vm.isBlockedApp(app.packageName)) {
                                        onAppLaunch(app.packageName)
                                    }
                                }
                                .padding(vertical = 10.dp),
                            style = ZenTextStyle.copy(fontSize = 16.sp)
                        )
                        Divider(color = ZenColors.MutedGray)
                    }
                }
            }
        }
    }
}

private fun hapticClick(context: Context) {
    val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
    } else {
        @Suppress("DEPRECATION")
        vibrator.vibrate(12)
    }
}
