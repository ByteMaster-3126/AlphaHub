package com.alphaimperium.alphahub

data class AppInfo(
    val packageName: String,
    val label: String,
    val isSystem: Boolean
)

data class WebsiteShortcut(
    val id: Long,
    val name: String,
    val url: String,
    val favorite: Boolean = false,
    val imageUri: String? = null
)

data class RecentApp(
    val packageName: String,
    val label: String
)

data class RecentWebsite(
    val id: Long,
    val name: String,
    val url: String,
    val imageUri: String? = null
)

data class HubTool(
    val id: String,
    val name: String,
    val description: String,
    val iconKey: String
)

data class CustomSection(
    val id: Long,
    val name: String,
    val imageUri: String?,
    val appPackages: List<String>,
    val websiteIds: List<Long>
)

data class QuickLaunchEntry(
    val type: String,
    val key: String
)

enum class HubScreen {
    HOME, TOOLS, APPS, SHORTCUTS,
    INSTALLED_APPS, ADD_TOOL, CUSTOM_SHORTCUT, QUICK_LAUNCH,
    MORE_FEATURES, BACKGROUND, SETTINGS
}
