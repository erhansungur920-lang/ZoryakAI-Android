package com.zoryakai.app

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.view.ViewGroup
import android.view.View
import android.graphics.Color
import android.content.Context
import android.content.SharedPreferences

class MainActivity : Activity() {
    private lateinit var web: WebView
    private lateinit var prefs: SharedPreferences
    private var pendingWebPermission: PermissionRequest? = null
    private val requestCodeMedia = 440

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("zoryak", Context.MODE_PRIVATE)
        web = WebView(this)
        web.setBackgroundColor(Color.WHITE)
        web.settings.javaScriptEnabled = true
        web.settings.domStorageEnabled = true
        web.settings.mediaPlaybackRequiresUserGesture = false
        web.settings.allowFileAccess = false
        web.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                return !(url.startsWith("https://appassets.androidplatform.net/") || url.startsWith("https://") || url.startsWith("http://"))
            }
        }
        web.webChromeClient = object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest) {
                runOnUiThread {
                    val wanted = request.resources.toList()
                    val needsMic = wanted.contains(PermissionRequest.RESOURCE_AUDIO_CAPTURE)
                    val needsCamera = wanted.contains(PermissionRequest.RESOURCE_VIDEO_CAPTURE)
                    val missing = mutableListOf<String>()
                    if (needsMic && checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) missing.add(Manifest.permission.RECORD_AUDIO)
                    if (needsCamera && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) missing.add(Manifest.permission.CAMERA)
                    if (missing.isEmpty()) request.grant(wanted.toTypedArray())
                    else {
                        pendingWebPermission = request
                        requestPermissions(missing.toTypedArray(), requestCodeMedia)
                    }
                }
            }
        }
        setContentView(web)
        val html = assets.open("index.html").bufferedReader(Charsets.UTF_8).use { it.readText() }
        web.loadDataWithBaseURL("https://appassets.androidplatform.net/assets/", html, "text/html", "UTF-8", null)
        if (prefs.getString("api", "").isNullOrBlank()) showApiSetup(true)
    }

    private fun showApiSetup(firstRun: Boolean) {
        val input = EditText(this)
        input.hint = "https://senin-zoryak-sunucun.example"
        input.setText(prefs.getString("api", ""))
        val info = TextView(this)
        info.text = "AI yanıtları için internette çalışan Zoryak sunucusu gerekir. Windows'taki 127.0.0.1 adresi telefondan erişilemez. Şimdilik boş bırakıp arayüzü açabilirsin."
        info.setPadding(36, 12, 36, 8)
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.addView(info)
        layout.addView(input, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        AlertDialog.Builder(this)
            .setTitle("Zoryak AI sunucu ayarı")
            .setView(layout)
            .setCancelable(!firstRun)
            .setPositiveButton("Kaydet") { _, _ ->
                prefs.edit().putString("api", input.text.toString().trim().trimEnd('/')).apply()
                val api = prefs.getString("api", "") ?: ""
                web.evaluateJavascript("localStorage.setItem('zoryak_api_base', ${org.json.JSONObject.quote(api)});", null)
                if (api.isNotBlank()) web.reload()
            }
            .setNegativeButton(if (firstRun) "Şimdilik geç" else "İptal") { _, _ -> }
            .show()
    }

    override fun onBackPressed() {
        if (web.canGoBack()) web.goBack() else showApiSetup(false)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeMedia) {
            val request = pendingWebPermission
            pendingWebPermission = null
            if (request != null && grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                request.grant(request.resources)
            } else request?.deny()
        }
    }
}
