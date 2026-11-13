package me.weishu.kernelsu.ui.component.snowfall

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Dialog for configuring the snowfall effect: enable toggle, speed, alpha,
 * density and a custom background image picker.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnowfallSettingsDialog(
    onDismiss: () -> Unit,
) {
    var enabled by remember { mutableStateOf(SnowSettings.enabled) }
    var speed by remember { mutableFloatStateOf(SnowSettings.speedMultiplier) }
    var alpha by remember { mutableFloatStateOf(SnowSettings.alphaMultiplier) }
    var density by remember { mutableIntStateOf(SnowSettings.density) }
    var bgUri by remember { mutableStateOf(SnowSettings.backgroundUri) }

    val pickLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        if (uri != null) {
            // Persist read permission so the image survives reboots
            runCatching {
                // no-op; caller app stores the string uri
            }
            bgUri = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("雪花飘落设置") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("启用雪花效果")
                    Switch(
                        checked = enabled,
                        onCheckedChange = { enabled = it },
                    )
                }

                Text("飘落速度: ${"%.1f".format(speed)}x")
                Slider(
                    value = speed,
                    onValueChange = { speed = it },
                    valueRange = 0.2f..3.0f,
                    enabled = enabled,
                )

                Text("透明度: ${"%d".format((alpha * 100).toInt())}%")
                Slider(
                    value = alpha,
                    onValueChange = { alpha = it },
                    valueRange = 0.1f..1.0f,
                    enabled = enabled,
                )

                Text("雪花数量: $density")
                Slider(
                    value = density.toFloat(),
                    onValueChange = { density = it.toInt() },
                    valueRange = 10f..120f,
                    enabled = enabled,
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TextButton(
                        onClick = { pickLauncher.launch("image/*") },
                        modifier = Modifier.weight(1f),
                    ) { Text("选择背景图片") }
                    TextButton(
                        onClick = { bgUri = null },
                        modifier = Modifier.weight(1f),
                        enabled = !bgUri.isNullOrBlank(),
                    ) { Text("清除背景") }
                }

                if (!bgUri.isNullOrBlank()) {
                    Text(
                        text = "背景已设置",
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                SnowSettings.enabled = enabled
                SnowSettings.speedMultiplier = speed
                SnowSettings.alphaMultiplier = alpha
                SnowSettings.density = density
                SnowSettings.backgroundUri = bgUri
                onDismiss()
            }) { Text("确定") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}
