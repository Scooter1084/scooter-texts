package com.scooter1084.texts.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import org.fossify.commons.helpers.FONT_TYPE_CUSTOM
import org.fossify.commons.helpers.FontHelper
import com.scooter1084.texts.extensions.config
import java.io.File

/**
 * The update number of the newest note in assets/whats-new/whatsnew.html.
 * When adding notes there (at the top, with this update number), bump this too.
 */
const val WHATS_NEW_VERSION = 1

private const val SCOOTER_FONT = "scooter_nunito.ttf"

/**
 * The Scooter look (same as Household, Weather and Alarm): the orange, cream and navy colors are the defaults in
 * commons/res/values/colors.xml; this turns on the Nunito font once, on the first run.
 * If Scooter later picks another font in Customize colors, that choice stays.
 */
fun Context.applyScooterLookOnce() {
    if (config.scooterLookApplied) return
    try {
        val target = File(FontHelper.getFontsDir(this), SCOOTER_FONT)
        if (!target.exists()) {
            assets.open("fonts/$SCOOTER_FONT").use { input -> target.outputStream().use { input.copyTo(it) } }
        }
        config.fontType = FONT_TYPE_CUSTOM
        config.fontName = SCOOTER_FONT
        FontHelper.clearCache()
        config.scooterLookApplied = true
    } catch (_: Exception) {
        // Storage not ready yet (for example before the first unlock); try again next start.
    }
}

/**
 * The shared Scooter "What's new" card, shown in a see-through WebView over the screen.
 * The page decides by itself whether there's anything new; [all] shows every note (the Settings link).
 * A fresh install shows nothing; after that each note shows once.
 */
@SuppressLint("SetJavaScriptEnabled")
fun Activity.showScooterWhatsNew(all: Boolean = false) {
    val root = findViewById<ViewGroup>(android.R.id.content) ?: return
    val existing = all || config.whatsNewSeenVersion > 0
    config.whatsNewSeenVersion = WHATS_NEW_VERSION

    val web = WebView(this)
    var back: OnBackPressedCallback? = null
    fun close() {
        back?.remove()
        back = null
        if (web.parent != null) root.removeView(web)
        root.post { web.destroy() } // not inside the WebView's own callback
    }

    web.setBackgroundColor(Color.TRANSPARENT)
    web.settings.javaScriptEnabled = true
    web.settings.domStorageEnabled = true
    web.settings.allowFileAccess = true
    web.webViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            if (request.url.scheme != "whatsnew") return false
            close()
            return true
        }
    }
    root.addView(web, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
    if (this is ComponentActivity) {
        back = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = close()
        }.also { onBackPressedDispatcher.addCallback(this, it) }
    }

    val query = buildList {
        if (existing) add("existing=1")
        if (all) add("all=1")
    }.joinToString("&")
    web.loadUrl("file:///android_asset/whats-new/whatsnew.html" + if (query.isEmpty()) "" else "?$query")
}
