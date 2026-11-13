package me.weishu.kernelsu.ui.component.snowfall

import android.content.Context
import android.net.Uri
import androidx.core.content.edit
import me.weishu.kernelsu.ksuApp

/**
 * Persisted configuration for the snowfall effect + custom background.
 */
object SnowSettings {

    private const val PREFS = "snow_settings"
    private const val KEY_ENABLED = "snow_enabled"
    private const val KEY_SPEED = "snow_speed"
    private const val KEY_ALPHA = "snow_alpha"
    private const val KEY_DENSITY = "snow_density"
    private const val KEY_BG_URI = "snow_bg_uri"

    private val prefs by lazy {
        ksuApp.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    var enabled: Boolean
        get() = prefs.getBoolean(KEY_ENABLED, true)
        set(value) = prefs.edit { putBoolean(KEY_ENABLED, value) }

    var speedMultiplier: Float
        get() = prefs.getFloat(KEY_SPEED, 1f)
        set(value) = prefs.edit { putFloat(KEY_SPEED, value.coerceIn(0.2f, 3f)) }

    var alphaMultiplier: Float
        get() = prefs.getFloat(KEY_ALPHA, 0.85f)
        set(value) = prefs.edit { putFloat(KEY_ALPHA, value.coerceIn(0.1f, 1f)) }

    var density: Int
        get() = prefs.getInt(KEY_DENSITY, 40)
        set(value) = prefs.edit { putInt(KEY_DENSITY, value.coerceIn(10, 120)) }

    var backgroundUri: String?
        get() = prefs.getString(KEY_BG_URI, null)
        set(value) = prefs.edit { putString(KEY_BG_URI, value) }

    fun background(): Uri? = backgroundUri?.takeIf { it.isNotBlank() }?.let(Uri::parse)
}
