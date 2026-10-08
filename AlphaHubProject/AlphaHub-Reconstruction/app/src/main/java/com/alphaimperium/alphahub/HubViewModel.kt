package com.alphaimperium.alphahub

import android.app.Application
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import org.json.JSONArray
import org.json.JSONObject

class HubViewModel(app: Application) : AndroidViewModel(app) {
    private val prefs=app.getSharedPreferences("alpha_hub",0)
    private val pm=app.packageManager
    private val context=getApplication<Application>()
    var apps by mutableStateOf<List<AppInfo>>(emptyList()); private set
    var websites by mutableStateOf(loadWebsites()); private set
    var recentApps by mutableStateOf(loadRecentApps()); private set
    var recentWebsites by mutableStateOf(loadRecentWebsites()); private set
    var recentItems by mutableStateOf(loadRecentItems()); private set
    var customSections by mutableStateOf(loadSections()); private set
    var quickLaunch by mutableStateOf(loadQuickLaunch()); private set
    var favoriteAppPackages by mutableStateOf(loadStringList("favorite_apps")); private set
    var phoneSettings by mutableStateOf(loadPhoneSettings()); private set
    var railSlots by mutableStateOf(loadRailSlots()); private set
    var backgroundUri by mutableStateOf(prefs.getString("background_uri",null)); private set
    var backgroundType by mutableStateOf(prefs.getString("background_type","image")?:"image"); private set
    var backgroundAnimation by mutableStateOf(prefs.getBoolean("background_animation",true)); private set
    var backgroundSpeed by mutableStateOf(prefs.getInt("background_speed",60)); private set
    var backgroundBrightness by mutableStateOf(prefs.getInt("background_brightness",60)); private set
    var animationsEnabled by mutableStateOf(prefs.getBoolean("animations_enabled",true)); private set
    var compactMode by mutableStateOf(prefs.getBoolean("compact_mode",false)); private set
    var panelOpacity by mutableStateOf(prefs.getInt("panel_opacity",92)); private set
    var recentsEnabled by mutableStateOf(prefs.getBoolean("recents_enabled",true)); private set
    var bubbleEnabled by mutableStateOf(prefs.getBoolean("bubble_enabled",false)); private set
    var logoAnimation by mutableStateOf(prefs.getBoolean("logo_animation",true)); private set
    var titleGlow by mutableStateOf(prefs.getBoolean("title_glow",true)); private set
    var glassEnabled by mutableStateOf(prefs.getBoolean("glass_enabled",true)); private set
    var blurEnabled by mutableStateOf(prefs.getBoolean("blur_enabled",true)); private set
    var neonBorderEnabled by mutableStateOf(prefs.getBoolean("neon_border",true)); private set
    var subtitleEnabled by mutableStateOf(prefs.getBoolean("subtitle_enabled",true)); private set
    var subtitleText by mutableStateOf(prefs.getString("subtitle_text","Nah, I'd Win")?:"Nah, I'd Win"); private set
    var titleText by mutableStateOf(prefs.getString("title_text","Alpha Hub")?:"Alpha Hub"); private set
    var logoUri by mutableStateOf(prefs.getString("logo_uri",null)); private set
    var titleSize by mutableStateOf(prefs.getInt("title_size",27)); private set
    var titleColorHex by mutableStateOf(prefs.getString("title_color","00E5FF")?:"00E5FF"); private set
    var voiceSearchEnabled by mutableStateOf(prefs.getBoolean("voice_search",true)); private set
    var searchHistoryEnabled by mutableStateOf(prefs.getBoolean("search_history",true)); private set
    var railEnabled by mutableStateOf(prefs.getBoolean("rail_enabled",true)); private set
    var railEditMode by mutableStateOf(false); private set

    val availablePhoneSettings=listOf(
        PhoneSettingShortcut("wifi","Wi-Fi","wifi",Settings.ACTION_WIFI_SETTINGS),
        PhoneSettingShortcut("bluetooth","Bluetooth","bluetooth",Settings.ACTION_BLUETOOTH_SETTINGS),
        PhoneSettingShortcut("mobile","Mobile Data","mobile",Settings.ACTION_WIRELESS_SETTINGS),
        PhoneSettingShortcut("hotspot","Hotspot","hotspot",Settings.ACTION_WIRELESS_SETTINGS),
        PhoneSettingShortcut("airplane","Airplane Mode","airplane",Settings.ACTION_AIRPLANE_MODE_SETTINGS),
        PhoneSettingShortcut("location","Location","location",Settings.ACTION_LOCATION_SOURCE_SETTINGS),
        PhoneSettingShortcut("battery","Battery","battery",Settings.ACTION_BATTERY_SAVER_SETTINGS),
        PhoneSettingShortcut("display","Display","display",Settings.ACTION_DISPLAY_SETTINGS),
        PhoneSettingShortcut("sound","Sound","sound",Settings.ACTION_SOUND_SETTINGS),
        PhoneSettingShortcut("apps","App Settings","apps",Settings.ACTION_APPLICATION_SETTINGS),
        PhoneSettingShortcut("storage","Storage","storage",Settings.ACTION_INTERNAL_STORAGE_SETTINGS),
        PhoneSettingShortcut("security","Security","security",Settings.ACTION_SECURITY_SETTINGS),
        PhoneSettingShortcut("date","Date & Time","date",Settings.ACTION_DATE_SETTINGS),
        PhoneSettingShortcut("accessibility","Accessibility","accessibility",Settings.ACTION_ACCESSIBILITY_SETTINGS)
    )

    init{refreshApps()}
    fun refreshApps(){
        val intent=Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        apps=pm.queryIntentActivities(intent,PackageManager.MATCH_ALL).mapNotNull{
            val ai=it.activityInfo.applicationInfo?:return@mapNotNull null
            AppInfo(ai.packageName,ai.loadLabel(pm).toString(),(ai.flags and ApplicationInfo.FLAG_SYSTEM)!=0)
        }.distinctBy{it.packageName}.sortedBy{it.label.lowercase()}
        if(favoriteAppPackages.isEmpty()){
            val preferred=listOf("Chrome","YouTube","Play Store","Settings","Gmail","Facebook")
            favoriteAppPackages=preferred.mapNotNull{w->apps.firstOrNull{it.label.equals(w,true)}?.packageName}.take(4).ifEmpty{apps.take(4).map{it.packageName}}
            saveStringList("favorite_apps",favoriteAppPackages)
        }else{
            favoriteAppPackages=favoriteAppPackages.filter{p->apps.any{it.packageName==p}}
            saveStringList("favorite_apps",favoriteAppPackages)
        }
    }
    fun favoriteApps()=favoriteAppPackages.mapNotNull{p->apps.firstOrNull{it.packageName==p}}
    fun toggleFavoriteApp(p:String){favoriteAppPackages=if(p in favoriteAppPackages)favoriteAppPackages-p else favoriteAppPackages+p;saveStringList("favorite_apps",favoriteAppPackages)}
    fun moveFavoriteApp(from:Int,to:Int){if(from !in favoriteAppPackages.indices||to !in favoriteAppPackages.indices)return;val l=favoriteAppPackages.toMutableList();val x=l.removeAt(from);l.add(to.coerceIn(0,l.size),x);favoriteAppPackages=l;saveStringList("favorite_apps",favoriteAppPackages)}
    fun launchApp(info:AppInfo){pm.getLaunchIntentForPackage(info.packageName)?.let{it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);context.startActivity(it);recordRecentApp(info)}}
    fun launchApp(p:String){apps.firstOrNull{it.packageName==p}?.let(::launchApp)}
    fun openWeb(web:WebsiteShortcut){
        val fixed=if(web.url.startsWith("http://")||web.url.startsWith("https://"))web.url else "https://"+web.url
        runCatching{val i=Intent(Intent.ACTION_VIEW,Uri.parse(fixed)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);if(!web.browserPackage.isNullOrBlank()&&isPackageInstalled(web.browserPackage!!))i.setPackage(web.browserPackage);context.startActivity(i)}
        recordRecentWebsite(web.copy(url=fixed))
    }
    fun addWebsite(name:String,url:String,imageUri:String?,browserPackage:String?=null){val n=name.ifBlank{"Website"};val u=url.trim();if(u.isBlank()||websites.any{it.name.equals(n,true)&&it.url.equals(u,true)})return;websites=websites+WebsiteShortcut(System.currentTimeMillis(),n,u,false,imageUri,browserPackage);saveWebsites()}
    fun updateWebsite(id:Long,name:String,url:String,imageUri:String?,browserPackage:String?){websites=websites.map{if(it.id==id)it.copy(name=name.ifBlank{it.name},url=url.trim(),imageUri=imageUri,browserPackage=browserPackage)else it};saveWebsites()}
    fun setWebsiteBrowser(id:Long,p:String?){websites=websites.map{if(it.id==id)it.copy(browserPackage=p)else it};saveWebsites()}
    fun toggleFavorite(id:Long){websites=websites.map{if(it.id==id)it.copy(favorite=!it.favorite)else it};saveWebsites()}
    fun favoriteWebsites()=websites.filter{it.favorite}
    fun removeWebsite(id:Long){websites=websites.filterNot{it.id==id};quickLaunch=quickLaunch.filterNot{it.type=="web"&&it.key==id.toString()};saveWebsites();saveQuickLaunch()}
    fun addQuickLaunchApp(p:String){if(quickLaunch.none{it.type=="app"&&it.key==p}){quickLaunch+=QuickLaunchEntry("app",p);saveQuickLaunch()}}
    fun addQuickLaunchWeb(id:Long){if(quickLaunch.none{it.type=="web"&&it.key==id.toString()}){quickLaunch+=QuickLaunchEntry("web",id.toString());saveQuickLaunch()}}
    fun removeQuickLaunch(e:QuickLaunchEntry){quickLaunch-=e;saveQuickLaunch()}
    fun addSection(n:String,i:String?,a:List<String>,w:List<Long>){customSections+=CustomSection(System.currentTimeMillis(),n.ifBlank{"My Section"},i,a,w);saveSections()}
    fun deleteSection(id:Long){customSections=customSections.filterNot{it.id==id};saveSections()}
    fun setBackground(uri:String?,animation:Boolean,speed:Int,brightness:Int,type:String="image"){backgroundUri=uri;backgroundType=type;backgroundAnimation=animation;backgroundSpeed=speed.coerceIn(10,100);backgroundBrightness=brightness.coerceIn(20,100);prefs.edit().putString("background_uri",uri).putString("background_type",type).putBoolean("background_animation",animation).putInt("background_speed",backgroundSpeed).putInt("background_brightness",backgroundBrightness).apply()}
    fun setAnimations(v:Boolean){animationsEnabled=v;prefs.edit().putBoolean("animations_enabled",v).apply()}
    fun setCompact(v:Boolean){compactMode=v;prefs.edit().putBoolean("compact_mode",v).apply()}
    fun setOpacity(v:Int){panelOpacity=v.coerceIn(60,100);prefs.edit().putInt("panel_opacity",panelOpacity).apply()}
    fun setRecents(v:Boolean){recentsEnabled=v;prefs.edit().putBoolean("recents_enabled",v).apply()}
    fun setBubble(v:Boolean){bubbleEnabled=v;prefs.edit().putBoolean("bubble_enabled",v).apply()}
    fun setLogoAnimation(v:Boolean){logoAnimation=v;prefs.edit().putBoolean("logo_animation",v).apply()}
    fun setTitleGlow(v:Boolean){titleGlow=v;prefs.edit().putBoolean("title_glow",v).apply()}
    fun setGlass(v:Boolean){glassEnabled=v;prefs.edit().putBoolean("glass_enabled",v).apply()}
    fun setBlur(v:Boolean){blurEnabled=v;prefs.edit().putBoolean("blur_enabled",v).apply()}
    fun setNeonBorder(v:Boolean){neonBorderEnabled=v;prefs.edit().putBoolean("neon_border",v).apply()}
    fun setSubtitleEnabled(v:Boolean){subtitleEnabled=v;prefs.edit().putBoolean("subtitle_enabled",v).apply()}
    fun setSubtitleText(v:String){subtitleText=v;prefs.edit().putString("subtitle_text",v).apply()}
    fun setTitleText(v:String){titleText=v;prefs.edit().putString("title_text",v).apply()}
    fun setLogoUri(v:String?){logoUri=v;prefs.edit().putString("logo_uri",v).apply()}
    fun setTitleSize(v:Int){titleSize=v.coerceIn(20,40);prefs.edit().putInt("title_size",titleSize).apply()}
    fun setTitleColorHex(v:String){titleColorHex=v;prefs.edit().putString("title_color",v).apply()}
    fun setVoiceSearch(v:Boolean){voiceSearchEnabled=v;prefs.edit().putBoolean("voice_search",v).apply()}
    fun setSearchHistory(v:Boolean){searchHistoryEnabled=v;prefs.edit().putBoolean("search_history",v).apply()}
    fun setRailEnabled(v:Boolean){railEnabled=v;prefs.edit().putBoolean("rail_enabled",v).apply()}
    fun toggleRailEditMode(){railEditMode=!railEditMode}
    fun setRailSlot(i:Int,s:RailSlot){if(i in railSlots.indices){railSlots=railSlots.toMutableList().also{it[i]=s};saveRailSlots()}}
    fun clearRailSlot(i:Int){setRailSlot(i,RailSlot())}
    fun openPhoneSetting(id:String){val s=availablePhoneSettings.firstOrNull{it.id==id}?:return;runCatching{context.startActivity(Intent(s.action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))};recordRecentItem("setting",id,s.name)}
    fun addPhoneSetting(id:String){val s=availablePhoneSettings.firstOrNull{it.id==id}?:return;if(phoneSettings.none{it.id==id}){phoneSettings+=s;savePhoneSettings()}}
    fun removePhoneSetting(id:String){phoneSettings=phoneSettings.filterNot{it.id==id};savePhoneSettings()}
    fun clearRecents(){recentApps=emptyList();recentWebsites=emptyList();recentItems=emptyList();saveRecentApps();saveRecentWebsites();saveRecentItems()}
    private fun recordRecentApp(info:AppInfo){if(!recentsEnabled)return;recentApps=(listOf(RecentApp(info.packageName,info.label))+recentApps.filterNot{it.packageName==info.packageName}).take(20);saveRecentApps();recordRecentItem("app",info.packageName,info.label)}
    private fun recordRecentWebsite(web:WebsiteShortcut){if(!recentsEnabled)return;recentWebsites=(listOf(RecentWebsite(web.id,web.name,web.url,web.imageUri))+recentWebsites.filterNot{it.id==web.id}).take(20);saveRecentWebsites();recordRecentItem("web",web.id.toString(),web.name)}
    private fun recordRecentItem(type:String,key:String,label:String){if(!recentsEnabled)return;recentItems=(listOf(RecentItem(type,key,label,System.currentTimeMillis()))+recentItems.filterNot{it.type==type&&it.key==key}).take(30);saveRecentItems()}
    private fun loadWebsites():List<WebsiteShortcut>{val raw=prefs.getString("websites",null)?:return listOf(WebsiteShortcut(1,"YouTube","https://youtube.com",true),WebsiteShortcut(2,"Google","https://google.com",true),WebsiteShortcut(3,"ChatGPT","https://chatgpt.com",true),WebsiteShortcut(4,"Netflix","https://netflix.com",true));return runCatching{val a=JSONArray(raw);List(a.length()){i->val o=a.getJSONObject(i);WebsiteShortcut(o.getLong("id"),o.getString("name"),o.getString("url"),o.optBoolean("favorite"),o.optString("imageUri").ifBlank{null},o.optString("browserPackage").ifBlank{null})}}.getOrDefault(emptyList())}
    private fun saveWebsites(){val a=JSONArray();websites.forEach{w->a.put(JSONObject().apply{put("id",w.id);put("name",w.name);put("url",w.url);put("favorite",w.favorite);put("imageUri",w.imageUri?:"");put("browserPackage",w.browserPackage?:"")})};prefs.edit().putString("websites",a.toString()).apply()}
    private fun loadRecentApps():List<RecentApp>=runCatching{val a=JSONArray(prefs.getString("recent_apps","[]"));List(a.length()){i->val o=a.getJSONObject(i);RecentApp(o.getString("packageName"),o.getString("label"))}}.getOrDefault(emptyList())
    private fun saveRecentApps(){val a=JSONArray();recentApps.forEach{v->a.put(JSONObject().apply{put("packageName",v.packageName);put("label",v.label)})};prefs.edit().putString("recent_apps",a.toString()).apply()}
    private fun loadRecentWebsites():List<RecentWebsite>=runCatching{val a=JSONArray(prefs.getString("recent_websites","[]"));List(a.length()){i->val o=a.getJSONObject(i);RecentWebsite(o.getLong("id"),o.getString("name"),o.getString("url"),o.optString("imageUri").ifBlank{null})}}.getOrDefault(emptyList())
    private fun saveRecentWebsites(){val a=JSONArray();recentWebsites.forEach{v->a.put(JSONObject().apply{put("id",v.id);put("name",v.name);put("url",v.url);put("imageUri",v.imageUri?:"")})};prefs.edit().putString("recent_websites",a.toString()).apply()}
    private fun loadRecentItems():List<RecentItem>=runCatching{val a=JSONArray(prefs.getString("recent_items","[]"));List(a.length()){i->val o=a.getJSONObject(i);RecentItem(o.getString("type"),o.getString("key"),o.getString("label"),o.optLong("timestamp",0L))}}.getOrDefault(emptyList())
    private fun saveRecentItems(){val a=JSONArray();recentItems.forEach{v->a.put(JSONObject().apply{put("type",v.type);put("key",v.key);put("label",v.label);put("timestamp",v.timestamp)})};prefs.edit().putString("recent_items",a.toString()).apply()}
    private fun loadSections():List<CustomSection>=runCatching{val a=JSONArray(prefs.getString("sections","[]"));List(a.length()){i->val o=a.getJSONObject(i);val ap=o.optJSONArray("apps")?:JSONArray();val w=o.optJSONArray("web")?:JSONArray();CustomSection(o.getLong("id"),o.getString("name"),o.optString("imageUri").ifBlank{null},List(ap.length()){j->ap.getString(j)},List(w.length()){j->w.getLong(j)})}}.getOrDefault(emptyList())
    private fun saveSections(){val a=JSONArray();customSections.forEach{s->val ap=JSONArray();s.appPackages.forEach(ap::put);val w=JSONArray();s.websiteIds.forEach(w::put);a.put(JSONObject().apply{put("id",s.id);put("name",s.name);put("imageUri",s.imageUri?:"");put("apps",ap);put("web",w)})};prefs.edit().putString("sections",a.toString()).apply()}
    private fun loadQuickLaunch():List<QuickLaunchEntry>=runCatching{val a=JSONArray(prefs.getString("quick_launch","[]"));List(a.length()){i->val o=a.getJSONObject(i);QuickLaunchEntry(o.getString("type"),o.getString("key"))}}.getOrDefault(emptyList())
    private fun saveQuickLaunch(){val a=JSONArray();quickLaunch.forEach{q->a.put(JSONObject().apply{put("type",q.type);put("key",q.key)})};prefs.edit().putString("quick_launch",a.toString()).apply()}
    private fun loadPhoneSettings():List<PhoneSettingShortcut>{val saved=prefs.getString("phone_settings",null)?:return availablePhoneSettings.take(3);return runCatching{val a=JSONArray(saved);List(a.length()){i->availablePhoneSettings.firstOrNull{it.id==a.getString(i)}}.filterNotNull()}.getOrDefault(availablePhoneSettings.take(3))}
    private fun savePhoneSettings(){val a=JSONArray();phoneSettings.forEach{a.put(it.id)};prefs.edit().putString("phone_settings",a.toString()).apply()}
    private fun loadRailSlots():List<RailSlot>{val saved=prefs.getString("rail_slots",null)?:return List(5){RailSlot()};return runCatching{val a=JSONArray(saved);List(5){i->if(i<a.length()){val o=a.getJSONObject(i);RailSlot(o.optString("type","empty"),o.optString("key",""))}else RailSlot()}}.getOrDefault(List(5){RailSlot()})}
    private fun saveRailSlots(){val a=JSONArray();railSlots.forEach{s->a.put(JSONObject().apply{put("type",s.type);put("key",s.key)})};prefs.edit().putString("rail_slots",a.toString()).apply()}
    private fun loadStringList(key:String):List<String>=runCatching{val a=JSONArray(prefs.getString(key,"[]"));List(a.length()){i->a.getString(i)}}.getOrDefault(emptyList())
    private fun saveStringList(key:String,values:List<String>){val a=JSONArray();values.forEach(a::put);prefs.edit().putString(key,a.toString()).apply()}
    private fun isPackageInstalled(p:String)=runCatching{pm.getApplicationInfo(p,0);true}.getOrDefault(false)
}
