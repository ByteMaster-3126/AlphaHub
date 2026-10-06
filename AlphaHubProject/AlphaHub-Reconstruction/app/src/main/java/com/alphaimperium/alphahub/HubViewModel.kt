package com.alphaimperium.alphahub

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import org.json.JSONArray
import org.json.JSONObject

class HubViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs = app.getSharedPreferences("alpha_hub", 0)
    private val pm = app.packageManager

    var apps by mutableStateOf<List<AppInfo>>(emptyList())
        private set
    var websites by mutableStateOf(loadWebsites())
        private set
    var recentApps by mutableStateOf(loadRecentApps())
        private set
    var recentWebsites by mutableStateOf(loadRecentWebsites())
        private set
    var customSections by mutableStateOf(loadSections())
        private set
    var quickLaunch by mutableStateOf(loadQuickLaunch())
        private set
    var backgroundUri by mutableStateOf(prefs.getString("background_uri", null))
        private set
    var backgroundAnimation by mutableStateOf(prefs.getBoolean("background_animation", true))
        private set
    var backgroundSpeed by mutableStateOf(prefs.getInt("background_speed", 60))
        private set
    var backgroundBrightness by mutableStateOf(prefs.getInt("background_brightness", 60))
        private set
    var animationsEnabled by mutableStateOf(prefs.getBoolean("animations_enabled", true))
        private set
    var compactMode by mutableStateOf(prefs.getBoolean("compact_mode", false))
        private set
    var panelOpacity by mutableStateOf(prefs.getInt("panel_opacity", 92))
        private set
    var recentsEnabled by mutableStateOf(prefs.getBoolean("recents_enabled", true))
        private set
    var bubbleEnabled by mutableStateOf(prefs.getBoolean("bubble_enabled", false))
        private set

    init { refreshApps() }

    fun refreshApps() {
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps = pm.queryIntentActivities(launcherIntent, PackageManager.MATCH_ALL)
            .mapNotNull { info ->
                val ai = info.activityInfo.applicationInfo ?: return@mapNotNull null
                AppInfo(
                    packageName = ai.packageName,
                    label = ai.loadLabel(pm).toString(),
                    isSystem = (ai.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }

    fun launchApp(info: AppInfo) {
        val intent = pm.getLaunchIntentForPackage(info.packageName) ?: return
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        getApplication<Application>().startActivity(intent)
        recordRecentApp(info)
    }

    fun launchApp(packageName: String) {
        apps.firstOrNull { it.packageName == packageName }?.let(::launchApp)
    }

    fun openWeb(web: WebsiteShortcut) {
        val fixed = if (web.url.startsWith("http://") || web.url.startsWith("https://")) web.url else "https://${web.url}"
        runCatching {
            getApplication<Application>().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(fixed)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }
        recordRecentWebsite(web.copy(url = fixed))
    }

    fun addWebsite(name: String, url: String, imageUri: String?) {
        val now = System.currentTimeMillis()
        websites = websites + WebsiteShortcut(now, name.ifBlank { "Website" }, url.trim(), false, imageUri)
        saveWebsites()
    }

    fun toggleFavorite(id: Long) {
        websites = websites.map { if (it.id == id) it.copy(favorite = !it.favorite) else it }
        saveWebsites()
    }

    fun removeWebsite(id: Long) {
        websites = websites.filterNot { it.id == id }
        saveWebsites()
    }

    fun addQuickLaunchApp(packageName: String) {
        if (quickLaunch.none { it.type == "app" && it.key == packageName }) {
            quickLaunch = quickLaunch + QuickLaunchEntry("app", packageName)
            saveQuickLaunch()
        }
    }

    fun addQuickLaunchWeb(id: Long) {
        if (quickLaunch.none { it.type == "web" && it.key == id.toString() }) {
            quickLaunch = quickLaunch + QuickLaunchEntry("web", id.toString())
            saveQuickLaunch()
        }
    }

    fun removeQuickLaunch(entry: QuickLaunchEntry) {
        quickLaunch = quickLaunch - entry
        saveQuickLaunch()
    }

    fun addSection(name: String, imageUri: String?, appPackages: List<String>, websiteIds: List<Long>) {
        customSections = customSections + CustomSection(System.currentTimeMillis(), name.ifBlank { "My Section" }, imageUri, appPackages, websiteIds)
        saveSections()
    }

    fun deleteSection(id: Long) {
        customSections = customSections.filterNot { it.id == id }
        saveSections()
    }

    fun setBackground(uri: String?, animation: Boolean, speed: Int, brightness: Int) {
        backgroundUri = uri
        backgroundAnimation = animation
        backgroundSpeed = speed.coerceIn(10, 100)
        backgroundBrightness = brightness.coerceIn(20, 100)
        prefs.edit()
            .putString("background_uri", uri)
            .putBoolean("background_animation", animation)
            .putInt("background_speed", backgroundSpeed)
            .putInt("background_brightness", backgroundBrightness)
            .apply()
    }

    fun setAnimations(value: Boolean) { animationsEnabled = value; prefs.edit().putBoolean("animations_enabled", value).apply() }
    fun setCompact(value: Boolean) { compactMode = value; prefs.edit().putBoolean("compact_mode", value).apply() }
    fun setOpacity(value: Int) { panelOpacity = value.coerceIn(60, 100); prefs.edit().putInt("panel_opacity", panelOpacity).apply() }
    fun setRecents(value: Boolean) { recentsEnabled = value; prefs.edit().putBoolean("recents_enabled", value).apply() }
    fun setBubble(value: Boolean) { bubbleEnabled = value; prefs.edit().putBoolean("bubble_enabled", value).apply() }
    fun clearRecents() { recentApps = emptyList(); recentWebsites = emptyList(); saveRecentApps(); saveRecentWebsites() }

    private fun recordRecentApp(info: AppInfo) {
        if (!recentsEnabled) return
        recentApps = (listOf(RecentApp(info.packageName, info.label)) + recentApps.filterNot { it.packageName == info.packageName }).take(4)
        saveRecentApps()
    }

    private fun recordRecentWebsite(web: WebsiteShortcut) {
        if (!recentsEnabled) return
        recentWebsites = (listOf(RecentWebsite(web.id, web.name, web.url, web.imageUri)) + recentWebsites.filterNot { it.id == web.id }).take(4)
        saveRecentWebsites()
    }

    private fun loadWebsites(): List<WebsiteShortcut> {
        val raw = prefs.getString("websites", null) ?: return listOf(
            WebsiteShortcut(1, "YouTube", "https://youtube.com", true),
            WebsiteShortcut(2, "Google", "https://google.com", true),
            WebsiteShortcut(3, "ChatGPT", "https://chatgpt.com", true),
            WebsiteShortcut(4, "Netflix", "https://netflix.com", true)
        )
        return runCatching {
            val arr = JSONArray(raw)
            List(arr.length()) { i ->
                val o = arr.getJSONObject(i)
                WebsiteShortcut(o.getLong("id"), o.getString("name"), o.getString("url"), o.optBoolean("favorite"), o.optString("imageUri").ifBlank { null })
            }
        }.getOrDefault(emptyList())
    }

    private fun saveWebsites() {
        val arr = JSONArray()
        websites.forEach { w -> arr.put(JSONObject().apply {
            put("id", w.id); put("name", w.name); put("url", w.url); put("favorite", w.favorite); put("imageUri", w.imageUri ?: "")
        }) }
        prefs.edit().putString("websites", arr.toString()).apply()
    }

    private fun loadRecentApps(): List<RecentApp> = runCatching {
        val arr = JSONArray(prefs.getString("recent_apps", "[]"))
        List(arr.length()) { i -> val o = arr.getJSONObject(i); RecentApp(o.getString("packageName"), o.getString("label")) }
    }.getOrDefault(emptyList())

    private fun saveRecentApps() {
        val arr = JSONArray(); recentApps.forEach { a -> arr.put(JSONObject().apply { put("packageName", a.packageName); put("label", a.label) }) }
        prefs.edit().putString("recent_apps", arr.toString()).apply()
    }

    private fun loadRecentWebsites(): List<RecentWebsite> = runCatching {
        val arr = JSONArray(prefs.getString("recent_websites", "[]"))
        List(arr.length()) { i -> val o = arr.getJSONObject(i); RecentWebsite(o.getLong("id"), o.getString("name"), o.getString("url"), o.optString("imageUri").ifBlank { null }) }
    }.getOrDefault(emptyList())

    private fun saveRecentWebsites() {
        val arr = JSONArray(); recentWebsites.forEach { w -> arr.put(JSONObject().apply { put("id", w.id); put("name", w.name); put("url", w.url); put("imageUri", w.imageUri ?: "") }) }
        prefs.edit().putString("recent_websites", arr.toString()).apply()
    }

    private fun loadSections(): List<CustomSection> = runCatching {
        val arr = JSONArray(prefs.getString("sections", "[]"))
        List(arr.length()) { i ->
            val o = arr.getJSONObject(i)
            val appsArray = o.optJSONArray("apps") ?: JSONArray()
            val webArray = o.optJSONArray("web") ?: JSONArray()
            CustomSection(
                o.getLong("id"), o.getString("name"), o.optString("imageUri").ifBlank { null },
                List(appsArray.length()) { j -> appsArray.getString(j) },
                List(webArray.length()) { j -> webArray.getLong(j) }
            )
        }
    }.getOrDefault(emptyList())

    private fun saveSections() {
        val arr = JSONArray(); customSections.forEach { s ->
            val appsArray = JSONArray(); s.appPackages.forEach(appsArray::put)
            val webArray = JSONArray(); s.websiteIds.forEach(webArray::put)
            arr.put(JSONObject().apply { put("id", s.id); put("name", s.name); put("imageUri", s.imageUri ?: ""); put("apps", appsArray); put("web", webArray) })
        }
        prefs.edit().putString("sections", arr.toString()).apply()
    }

    private fun loadQuickLaunch(): List<QuickLaunchEntry> = runCatching {
        val arr = JSONArray(prefs.getString("quick_launch", "[]"))
        List(arr.length()) { i -> val o = arr.getJSONObject(i); QuickLaunchEntry(o.getString("type"), o.getString("key")) }
    }.getOrDefault(emptyList())

    private fun saveQuickLaunch() {
        val arr = JSONArray(); quickLaunch.forEach { q -> arr.put(JSONObject().apply { put("type", q.type); put("key", q.key) }) }
        prefs.edit().putString("quick_launch", arr.toString()).apply()
    }
}
