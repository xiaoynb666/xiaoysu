package me.weishu.kernelsu.ui.component.snowfall

import android.graphics.BitmapFactory
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.LocalContext
import me.weishu.kernelsu.R
import kotlin.math.cos
import kotlin.random.Random

/**
 * Snow particle configuration and animation.
 * Renders two meme images (中国电竞靠你了 + blue trollface) as falling snow,
 * iOS-style with sway, rotation and depth-based size/alpha.
 */
data class SnowflakeConfig(
    val enabled: Boolean = true,
    val speedMultiplier: Float = 1f,       // 0.2 .. 3.0
    val alphaMultiplier: Float = 0.85f,    // 0.1 .. 1.0
    val density: Int = 40,                  // number of particles
)

private data class Snowflake(
    var x: Float,
    var y: Float,
    var speed: Float,
    var drift: Float,
    var driftPhase: Float,
    var driftSpeed: Float,
    var rotation: Float,
    var rotationSpeed: Float,
    var size: Float,
    var alpha: Float,
    var imageIndex: Int,
)

@Composable
fun Snowfall(
    config: SnowflakeConfig,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val bitmaps = remember {
        listOf(
            BitmapFactory.decodeResource(context.resources, R.drawable.snow_particle_1),
            BitmapFactory.decodeResource(context.resources, R.drawable.snow_particle_2),
        ).map { it.asImageBitmap() }
    }

    if (!config.enabled) return

    val particles = remember(config.density) {
        List(config.density) { randomSnowflake(Random) }
    }

    // Frame tick used to force recomposition every frame so the snow keeps falling.
    var frameTick by remember { mutableLongStateOf(0L) }
    LaunchedEffect(config) {
        while (true) {
            withFrameNanos { frameTime ->
                frameTick = frameTime
            }
        }
    }

    // Read during composition (not inside draw) so each frame triggers recomposition.
    @Suppress("UNUSED_EXPRESSION")
    frameTick

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        if (w <= 0f || h <= 0f) return@Canvas

        particles.forEach { p ->
            p.y += p.speed * config.speedMultiplier
            p.driftPhase += p.driftSpeed
            p.x += p.drift * cos(p.driftPhase)
            p.rotation += p.rotationSpeed

            if (p.y > h + p.size) {
                p.y = -p.size
                p.x = Random.nextFloat() * w
            }
            if (p.x < -p.size) p.x = w + p.size
            if (p.x > w + p.size) p.x = -p.size

            val bitmap = bitmaps[p.imageIndex % bitmaps.size]
            val alpha = (p.alpha * config.alphaMultiplier).coerceIn(0f, 1f)
            rotate(p.rotation, Offset(p.x, p.y)) {
                drawImage(
                    image = bitmap,
                    topLeft = Offset(p.x - p.size / 2, p.y - p.size / 2),
                    alpha = alpha,
                )
            }
        }
    }
}

private fun randomSnowflake(rand: Random): Snowflake {
    val imageIndex = rand.nextInt(2)
    return Snowflake(
        x = rand.nextFloat() * 1000f,
        y = rand.nextFloat() * 1000f,
        speed = rand.nextFloat() * 2f + 0.8f,
        drift = rand.nextFloat() * 1.5f + 0.3f,
        driftPhase = rand.nextFloat() * (2 * Math.PI).toFloat(),
        driftSpeed = rand.nextFloat() * 0.02f + 0.005f,
        rotation = rand.nextFloat() * 360f,
        rotationSpeed = (rand.nextFloat() - 0.5f) * 4f,
        size = rand.nextFloat() * 30f + 22f,
        alpha = rand.nextFloat() * 0.5f + 0.5f,
        imageIndex = imageIndex,
    )
}
