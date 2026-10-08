package com.alphaimperium.alphahub

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.input.pointer.pointerInput
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.speech.RecognizerIntent
import android.view.ViewGroup
import android.view.Gravity
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BatteryStd
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Note
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Web
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.alphaimperium.alphahub.ui.theme.AlphaHubTheme

private val Bg = Color(0xFF02050D)
private val Surface1 = Color(0xCC071325)
private val Surface2 = Color(0xE70A1C37)
private val Cyan = Color(0xFF00E5FF)
private val Blue = Color(0xFF159CFF)
private val Purple = Color(0xFF8B5CFF)
private val Magenta = Color(0xFFB339FF)
private val Muted = Color(0xFF9EB0D3)

class MainActivity : ComponentActivity() {
    private var overlayRequested = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawableResource(android.R.color.transparent)
        if (!Settings.canDrawOverlays(this)) {
            requestOverlayPermission()
        } else {
            launchFloatingLauncher()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!overlayRequested && Settings.canDrawOverlays(this)) launchFloatingLauncher()
    }

    private fun requestOverlayPermission() {
        overlayRequested = true
        Toast.makeText(this, "Enable Alpha Hub display-over-other-apps permission", Toast.LENGTH_LONG).show()
        runCatching {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")))
        }
    }

    private fun launchFloatingLauncher() {
        runCatching {
            val intent = Intent(this, AlphaHubFloatingService::class.java)
            if (Build.VERSION.SDK_INT >= 26) startForegroundService(intent) else startService(intent)
        }
        finish()
    }
}

@Composable
private fun AlphaHubApp(
    recognitionLauncher: androidx.activity.result.ActivityResultLauncher<Intent>?,
    vm: HubViewModel = viewModel(),
    floating: Boolean = false
) {
    var screen by remember { mutableStateOf(HubScreen.HOME) }
    var search by remember { mutableStateOf("") }
    Box(Modifier.fillMaxSize().background(Bg)) {
        NeoBackground(vm)
        Scaffold(containerColor = Color.Transparent) { padding ->
            Column(Modifier.fillMaxSize().padding(padding).statusBarsPadding()) {
                val topLevel = screen in setOf(HubScreen.HOME, HubScreen.TOOLS, HubScreen.APPS, HubScreen.SHORTCUTS)
                if (screen == HubScreen.HOME) HomeHeader(vm, { screen = HubScreen.SETTINGS }, { screen = HubScreen.TOOLS })
                Box(Modifier.fillMaxWidth().weight(1f)) {
                    AnimatedContent(targetState = screen, label = "page") { current ->
                        when (current) {
                            HubScreen.HOME -> HomeScreen(vm, search, { search = it }, { screen = it }, { text ->
                                recognitionLauncher?.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                                    putExtra(RecognizerIntent.EXTRA_PROMPT, text)
                                })
                            })
                            HubScreen.TOOLS -> ToolsScreen(vm, { screen = HubScreen.HOME }, { screen = HubScreen.ADD_TOOL }, { screen = HubScreen.MORE_FEATURES })
                            HubScreen.APPS -> InstalledAppsScreen(vm, { screen = HubScreen.QUICK_LAUNCH }, { screen = HubScreen.HOME })
                            HubScreen.SHORTCUTS -> ShortcutsScreen(vm, { screen = HubScreen.CUSTOM_SHORTCUT }, { screen = HubScreen.QUICK_LAUNCH }, { screen = HubScreen.HOME })
                            HubScreen.INSTALLED_APPS -> InstalledAppsScreen(vm, { screen = HubScreen.QUICK_LAUNCH }, { screen = HubScreen.HOME })
                            HubScreen.ADD_TOOL -> AddToolScreen(vm, { screen = HubScreen.HOME }, { screen = HubScreen.INSTALLED_APPS }, { screen = HubScreen.QUICK_LAUNCH }, { screen = HubScreen.CUSTOM_SHORTCUT })
                            HubScreen.CUSTOM_SHORTCUT -> CustomShortcutScreen(vm, { screen = HubScreen.SHORTCUTS })
                            HubScreen.QUICK_LAUNCH -> QuickLaunchScreen(vm, { screen = HubScreen.HOME }, { screen = HubScreen.INSTALLED_APPS }, { screen = HubScreen.CUSTOM_SHORTCUT })
                            HubScreen.MORE_FEATURES -> MoreFeaturesScreen(vm, { screen = HubScreen.BACKGROUND }, { screen = HubScreen.SETTINGS })
                            HubScreen.BACKGROUND -> BackgroundScreen(vm, { screen = HubScreen.MORE_FEATURES })
                            HubScreen.SETTINGS -> SettingsScreen(vm, { screen = HubScreen.HOME })
                        }
                    }
                }
                if (topLevel) BottomNavigation(screen) { screen = it }
            }
        }
    }
}

@Composable
private fun NeoBackground(vm: HubViewModel) {
    val image = rememberImageBitmap(vm.backgroundUri)
    val transition = rememberInfiniteTransition(label = "neo")
    val shift by transition.animateFloat(
        0.98f, 1.06f,
        infiniteRepeatable(tween(durationMillis = (2200 - vm.backgroundSpeed * 12).coerceIn(900, 2200), easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "shift"
    )
    Box(
        Modifier.fillMaxSize().graphicsLayer {
            scaleX = if (vm.backgroundAnimation) shift else 1f
            scaleY = if (vm.backgroundAnimation) shift else 1f
        }
    ) {
        if (image != null) {
            Image(image, null, Modifier.fillMaxSize().alpha(vm.backgroundBrightness / 100f), contentScale = ContentScale.Crop)
        } else {
            Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF14285A), Color(0xFF0A0D28), Bg), radius = 900f)))
            Canvas(Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height
                val p1 = Path().apply {
                    moveTo(-w * .1f, h * .30f)
                    cubicTo(w * .16f, h * (.10f + (shift - 1f) * .8f), w * .34f, h * .48f, w * .58f, h * .24f)
                    cubicTo(w * .80f, h * .02f, w * .92f, h * .16f, w * 1.12f, h * .08f)
                }
                val p2 = Path().apply {
                    moveTo(-w * .1f, h * .76f)
                    cubicTo(w * .15f, h * .92f, w * .42f, h * .58f, w * .64f, h * .76f)
                    cubicTo(w * .84f, h * .94f, w * .92f, h * .62f, w * 1.12f, h * .70f)
                }
                drawPath(p1, brush = Brush.linearGradient(listOf(Cyan, Blue, Purple)), style = Stroke(width = 7f, cap = StrokeCap.Round), alpha = .55f)
                drawPath(p2, brush = Brush.linearGradient(listOf(Purple, Magenta, Blue)), style = Stroke(width = 9f, cap = StrokeCap.Round), alpha = .45f)
            }
            Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color.Transparent, Color(0x332F80FF), Color(0x443F16A5), Color.Transparent))))
        }
    }
}

@Composable
private fun HomeHeader(vm: HubViewModel, onSettings: () -> Unit, onCrown: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
        RoundedCornerShape(24.dp),
        color = if (vm.glassEnabled) Color(0x99071125) else Color.Transparent,
        border = if (vm.neonBorderEnabled) BorderStroke(1.dp, BorderBrush()) else null
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            val customLogo = rememberImageBitmap(vm.logoUri)
            if (customLogo != null) Image(customLogo, null, Modifier.size(74.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            else Image(painterResource(com.alphaimperium.alphahub.R.drawable.alpha_logo), null, Modifier.size(74.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(vm.titleText,fontSize=vm.titleSize.sp,fontWeight=FontWeight.Bold,color=runCatching{Color(android.graphics.Color.parseColor("#"+vm.titleColorHex))}.getOrDefault(Cyan),modifier=if(vm.titleGlow)Modifier.graphicsLayer{shadowElevation=10f}else Modifier)
                if(vm.subtitleEnabled) Text(vm.subtitleText,color=Cyan,fontSize=14.sp,fontWeight=FontWeight.SemiBold)
            }
            RoundIcon(Icons.Default.Star,onCrown);Spacer(Modifier.width(8.dp));RoundIcon(Icons.Default.Settings,onSettings)
        }
    }
}

@Composable
private fun RoundIcon(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = Color(0x330A1631),
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(Blue, Purple))),
        modifier = Modifier.size(48.dp)
    ) {
        IconButton(onClick = onClick) { Icon(icon, null, tint = Color(0xFFB9B2FF)) }
    }
}

@Composable
private fun SearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    onVoice: () -> Unit,
    placeholder: String = "Search apps, tools, or websites..."
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        singleLine = true,
        placeholder = { Text(placeholder, color = Muted) },
        leadingIcon = { Icon(Icons.Default.Search, null, tint = Cyan) },
        trailingIcon = { IconButton(onClick = onVoice) { Icon(Icons.Default.Mic, null, tint = Color(0xFFB98BFF)) } },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0x9909162A),
            unfocusedContainerColor = Color(0x9909162A),
            focusedIndicatorColor = Cyan,
            unfocusedIndicatorColor = Purple,
            cursorColor = Cyan,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White
        ),
        shape = RoundedCornerShape(28.dp)
    )
}

@Composable
private fun HomeScreen(vm:HubViewModel,search:String,onSearch:(String)->Unit,navigate:(HubScreen)->Unit,onVoice:(String)->Unit,showRail:Boolean=true){
    var railEditor by remember{mutableStateOf<Int?>(null)}
    var showPhoneSettings by remember{mutableStateOf(false)}
    val apps=vm.apps.filter{it.label.contains(search,true)}
    val webs=vm.websites.filter{it.name.contains(search,true)||it.url.contains(search,true)}
    val settings=vm.availablePhoneSettings.filter{it.name.contains(search,true)}
    Row(Modifier.fillMaxSize()){
        if(showRail && vm.railEnabled) ActionRail(vm,navigate){railEditor=it}
        Box(Modifier.fillMaxSize().weight(1f)){
            LazyColumn(Modifier.fillMaxSize(),contentPadding=PaddingValues(bottom=20.dp),verticalArrangement=Arrangement.spacedBy(12.dp)){
                item{SearchBar(search,onSearch,{if(vm.voiceSearchEnabled)onVoice("Search Alpha Hub")},"Universal Search")}
                if(search.isNotBlank()){
                    item{SectionCard("SEARCH RESULTS",Icons.Default.Search,null,{}){
                        apps.take(8).forEach{SearchResultRow(it.label,"App",Icons.Default.GridView){vm.launchApp(it)}}
                        webs.take(8).forEach{SearchResultRow(it.name,"Website",Icons.Default.Language){vm.openWeb(it)}}
                        settings.take(8).forEach{SearchResultRow(it.name,"Phone Setting",Icons.Default.Settings){vm.openPhoneSetting(it.id)}}
                        val tools=listOf("Screen Translation","Screenshot","QR Scanner","Web Search","Notes")
                        tools.filter{it.contains(search,true)}.forEach{SearchResultRow(it,"Tool",Icons.Default.Build){navigate(HubScreen.TOOLS)}}
                        if(apps.isEmpty()&&webs.isEmpty()&&settings.isEmpty()&&tools.none{it.contains(search,true)})EmptyInline("No matching Alpha Hub result")
                    }}
                }else{
                    item{SectionCard("FAVORITE APPS",Icons.Default.Star,"+ Add",{navigate(HubScreen.APPS)}){
                        val favorites=vm.favoriteApps();if(favorites.isEmpty())EmptyInline("Tap + Add to choose installed apps")else FavoriteAppGrid(favorites.take(8),vm);ViewAllButton{navigate(HubScreen.APPS)}
                    }}
                    item{SectionCard("FAVORITE WEBSITES",Icons.Default.Language,"+ Add",{navigate(HubScreen.CUSTOM_SHORTCUT)}){
                        val favorites=vm.favoriteWebsites().take(4);if(favorites.isEmpty())EmptyInline("Add a website and mark it as a favorite")else WebsiteStrip(favorites,vm,true);ViewAllButton{navigate(HubScreen.SHORTCUTS)}
                    }}
                    item{SectionCard("RECENT",Icons.Default.History,"Clear",{vm.clearRecents()}){
                        val items=vm.recentItems.take(8);if(items.isEmpty())EmptyInline("Apps and websites opened through Alpha Hub appear here")else RecentCombinedGrid(items,vm);if(vm.recentItems.size>8)ViewAllButton{navigate(HubScreen.SHORTCUTS)}
                    }}
                    item{SectionCard("PHONE SETTINGS",Icons.Default.Tune,"+ Add",{showPhoneSettings=true}){PhoneSettingsStrip(vm.phoneSettings,vm)}}
                    vm.customSections.forEach{section->item(key="section-"+section.id){SectionCard(section.name,Icons.Default.Build,null,{}){
                        section.imageUri?.let{rememberImageBitmap(it)?.let{b->Image(b,null,Modifier.size(52.dp).clip(RoundedCornerShape(14.dp)),contentScale=ContentScale.Crop);Spacer(Modifier.height(8.dp))}}
                        val sa=section.appPackages.mapNotNull{p->vm.apps.firstOrNull{it.packageName==p}};if(sa.isNotEmpty())AppStrip(sa,vm)
                        val sw=section.websiteIds.mapNotNull{id->vm.websites.firstOrNull{it.id==id}};if(sw.isNotEmpty())WebsiteStrip(sw,vm,true)
                    }}}
                }
            }
        }
    }
    railEditor?.let{RailEditorDialog(vm,it){railEditor=null}}
    if(showPhoneSettings)PhoneSettingsPickerDialog(vm){showPhoneSettings=false}
}

@Composable
private fun SearchResultRow(title:String,type:String,icon:androidx.compose.ui.graphics.vector.ImageVector,onClick:()->Unit){
 Surface(Modifier.fillMaxWidth().padding(vertical=3.dp).clickable{onClick()},RoundedCornerShape(14.dp),color=Surface2,border=BorderStroke(1.dp,Color(0xFF1D4F9C))){Row(Modifier.padding(10.dp),verticalAlignment=Alignment.CenterVertically){Icon(icon,null,tint=Cyan,modifier=Modifier.size(28.dp));Spacer(Modifier.width(10.dp));Column(Modifier.weight(1f)){Text(title,fontWeight=FontWeight.SemiBold);Text(type,color=Muted,fontSize=10.sp)}}}
}
@Composable
private fun FavoriteAppGrid(apps:List<AppInfo>,vm:HubViewModel){
 LazyVerticalGrid(columns=GridCells.Fixed(4),modifier=Modifier.height(if(apps.size>4)174.dp else 94.dp),userScrollEnabled=false,horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){items(apps.take(8)){AppTile(it,vm)}}
}
@Composable
private fun RecentCombinedGrid(items:List<RecentItem>,vm:HubViewModel){
 LazyVerticalGrid(columns=GridCells.Fixed(4),modifier=Modifier.height(174.dp),userScrollEnabled=false,horizontalArrangement=Arrangement.spacedBy(8.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){
  items(items.take(8)){item->
   when(item.type){
    "app"->vm.apps.firstOrNull{it.packageName==item.key}?.let{AppTile(it,vm)}
    "web"->vm.websites.firstOrNull{it.id.toString()==item.key}?.let{WebsiteTile(it,vm,false)}
    else->Surface(Modifier.width(92.dp).clickable{vm.openPhoneSetting(item.key)},RoundedCornerShape(16.dp),color=Surface2,border=BorderStroke(1.dp,Blue)){Column(Modifier.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(Icons.Default.Settings,null,tint=Cyan,modifier=Modifier.size(42.dp));Text(item.label.take(12),fontSize=11.sp,maxLines=1)}}
   }
  }
 }
}
@Composable
private fun PhoneSettingsStrip(settings:List<PhoneSettingShortcut>,vm:HubViewModel){
 Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(10.dp)){settings.forEach{s->Surface(Modifier.width(112.dp).clickable{vm.openPhoneSetting(s.id)},RoundedCornerShape(16.dp),color=Surface2,border=BorderStroke(1.dp,Color(0xFF1D62B4))){Column(Modifier.padding(10.dp),horizontalAlignment=Alignment.CenterHorizontally){Icon(phoneSettingIcon(s.iconKey),null,tint=Cyan,modifier=Modifier.size(38.dp));Spacer(Modifier.height(4.dp));Text(s.name,fontSize=11.sp,maxLines=1)}}}}
}
@Composable
private fun phoneSettingIcon(key:String):androidx.compose.ui.graphics.vector.ImageVector=when(key){
 "wifi"->Icons.Default.WifiTethering;"bluetooth"->Icons.Default.Bluetooth;"mobile"->Icons.Default.Speed;"location"->Icons.Default.Language;"battery"->Icons.Default.BatteryStd;"display"->Icons.Default.Image;else->Icons.Default.Settings
}
@Composable
private fun ActionRail(vm:HubViewModel,navigate:(HubScreen)->Unit,onEditSlot:(Int)->Unit){
 Surface(Modifier.width(88.dp).fillMaxHeight().padding(start=8.dp,top=8.dp,bottom=8.dp),RoundedCornerShape(30.dp),color=Color(0xD9040C1B),border=BorderStroke(1.dp,Color.White)){
  Column(Modifier.fillMaxSize().padding(vertical=12.dp),horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(10.dp)){
   RailAction(Icons.Default.Build,"Tools"){navigate(HubScreen.TOOLS)};RailAction(Icons.Default.Translate,"Screen\ntranslation"){navigate(HubScreen.TOOLS)}
   vm.railSlots.forEachIndexed{index,slot->RailSlotView(slot,vm,index,onEditSlot)};Spacer(Modifier.weight(1f))
   RailAction(Icons.Default.Edit,if(vm.railEditMode)"Done" else "Edit"){vm.toggleRailEditMode()};RailAction(Icons.Default.ArrowBack,"Back"){navigate(HubScreen.HOME)}
  }
 }
}
@Composable
private fun RailAction(icon:androidx.compose.ui.graphics.vector.ImageVector,label:String,onClick:()->Unit){
 Column(Modifier.width(72.dp).clip(RoundedCornerShape(18.dp)).clickable{onClick()}.padding(vertical=6.dp),horizontalAlignment=Alignment.CenterHorizontally){Surface(Modifier.size(52.dp),RoundedCornerShape(16.dp),color=Color(0xCC0A2143),border=BorderStroke(1.dp,Blue)){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Icon(icon,null,tint=Cyan,modifier=Modifier.size(28.dp))}};Text(label,color=Color(0xFFD3E1FF),fontSize=10.sp,maxLines=2)}
}
@Composable
private fun RailSlotView(slot:RailSlot,vm:HubViewModel,index:Int,onEditSlot:(Int)->Unit){
 val label=when(slot.type){"app"->vm.apps.firstOrNull{it.packageName==slot.key}?.label?:"App";"web"->vm.websites.firstOrNull{it.id.toString()==slot.key}?.name?:"Website";"setting"->vm.availablePhoneSettings.firstOrNull{it.id==slot.key}?.name?:"Setting";else->"Slot "+(index+1)}
 val icon=when(slot.type){"app"->Icons.Default.GridView;"web"->Icons.Default.Language;"setting"->Icons.Default.Settings;else->Icons.Default.Add}
 Column(Modifier.width(72.dp).clickable{if(vm.railEditMode||slot.type=="empty")onEditSlot(index)else when(slot.type){"app"->vm.launchApp(slot.key);"web"->vm.websites.firstOrNull{it.id.toString()==slot.key}?.let(vm::openWeb);"setting"->vm.openPhoneSetting(slot.key)}}.padding(vertical=3.dp),horizontalAlignment=Alignment.CenterHorizontally){
  Surface(Modifier.size(52.dp),RoundedCornerShape(16.dp),color=if(slot.type=="empty")Color(0x6610274C)else Color(0xCC0A2143),border=BorderStroke(1.dp,if(slot.type=="empty")Color(0xFF214F8D)else Cyan)){Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center){Icon(icon,null,tint=if(slot.type=="empty")Muted else Cyan,modifier=Modifier.size(26.dp))}};Text(label.take(10),color=Muted,fontSize=9.sp,maxLines=1)
 }
}
@Composable
private fun RailEditorDialog(vm:HubViewModel,index:Int,onDismiss:()->Unit){
 AlertDialog(onDismissRequest=onDismiss,title={Text("Customize Rail Slot "+(index+1))},text={LazyColumn(Modifier.height(420.dp)){
  item{TextButton(onClick={vm.clearRailSlot(index);onDismiss()}){Text("Empty slot")}};item{Text("Apps",color=Cyan,fontWeight=FontWeight.Bold)}
  items(vm.apps.take(10)){a->TextButton(onClick={vm.setRailSlot(index,RailSlot("app",a.packageName));onDismiss()},modifier=Modifier.fillMaxWidth()){Text(a.label,modifier=Modifier.fillMaxWidth())}}
  item{Text("Websites",color=Cyan,fontWeight=FontWeight.Bold)}
  items(vm.websites.take(10)){w->TextButton(onClick={vm.setRailSlot(index,RailSlot("web",w.id.toString()));onDismiss()},modifier=Modifier.fillMaxWidth()){Text(w.name,modifier=Modifier.fillMaxWidth())}}
  item{Text("Phone Settings",color=Cyan,fontWeight=FontWeight.Bold)}
  items(vm.availablePhoneSettings.take(10)){s->TextButton(onClick={vm.setRailSlot(index,RailSlot("setting",s.id));onDismiss()},modifier=Modifier.fillMaxWidth()){Text(s.name,modifier=Modifier.fillMaxWidth())}}
 }},confirmButton={TextButton(onClick=onDismiss){Text("Done")}})
}
@Composable
private fun PhoneSettingsPickerDialog(vm:HubViewModel,onDismiss:()->Unit){
 AlertDialog(onDismissRequest=onDismiss,title={Text("Phone Settings on Home")},text={LazyColumn(Modifier.height(420.dp)){items(vm.availablePhoneSettings){s->val selected=vm.phoneSettings.any{it.id==s.id};Row(Modifier.fillMaxWidth().clickable{if(selected)vm.removePhoneSetting(s.id)else vm.addPhoneSetting(s.id)}.padding(vertical=8.dp),verticalAlignment=Alignment.CenterVertically){Icon(phoneSettingIcon(s.iconKey),null,tint=Cyan);Spacer(Modifier.width(10.dp));Text(s.name,Modifier.weight(1f));Text(if(selected)"Added" else "Add",color=if(selected)Cyan else Muted,fontSize=12.sp)}}}},confirmButton={TextButton(onClick=onDismiss){Text("Done")}})
}

@Composable
private fun SectionCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    addText: String?,
    onAdd: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp),
        shape = RoundedCornerShape(24.dp),
        color = Surface1,
        border = BorderStroke(1.dp, Brush.linearGradient(listOf(Blue, Color(0xFF3C56D9), Purple))),
        shadowElevation = 12.dp
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = Cyan, modifier = Modifier.size(25.dp))
                Spacer(Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.Bold, fontSize = 17.sp, modifier = Modifier.weight(1f))
                if (addText != null) {
                    TextButton(onClick = onAdd) { Text(addText, color = Purple, fontWeight = FontWeight.Bold) }
                }
            }
            Spacer(Modifier.height(8.dp))
            content()
        }
    }
}

@Composable
private fun AppStrip(apps: List<AppInfo>, vm: HubViewModel) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        apps.forEach { app -> AppTile(app, vm) }
    }
}

@Composable
private fun RecentAppsStrip(apps: List<RecentApp>, vm: HubViewModel) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        apps.forEach { recent ->
            vm.apps.firstOrNull { it.packageName == recent.packageName }?.let { AppTile(it, vm) }
        }
    }
}

@Composable
private fun AppTile(app: AppInfo, vm: HubViewModel) {
    val context = LocalContext.current
    val bitmap = remember(app.packageName) {
        runCatching { context.packageManager.getApplicationIcon(app.packageName).toBitmap(96, 96).asImageBitmap() }.getOrNull()
    }
    Surface(
        modifier = Modifier.width(92.dp).clickable { vm.launchApp(app) },
        shape = RoundedCornerShape(16.dp),
        color = Surface2,
        border = BorderStroke(1.dp, Color(0xFF1D62B4))
    ) {
        Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            if (bitmap != null) Image(bitmap, null, Modifier.size(42.dp).clip(RoundedCornerShape(11.dp)))
            else Box(Modifier.size(42.dp).background(Blue, RoundedCornerShape(11.dp)))
            Spacer(Modifier.height(5.dp))
            Text(app.label.take(14), fontSize = 11.sp, maxLines = 1)
        }
    }
}

@Composable
private fun WebsiteStrip(webs: List<WebsiteShortcut>, vm: HubViewModel, showFavorites: Boolean) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        webs.forEach { web -> WebsiteTile(web, vm, showFavorites) }
    }
}

@Composable
private fun WebsiteTile(web: WebsiteShortcut, vm: HubViewModel, showFavorite: Boolean) {
    Surface(
        modifier = Modifier.width(102.dp),
        shape = RoundedCornerShape(18.dp),
        color = Surface2,
        border = BorderStroke(1.dp, Color(0xFF234E96))
    ) {
        Box(Modifier.clickable { vm.openWeb(web) }) {
            Column(Modifier.padding(9.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.TopEnd) {
                    WebsiteIcon(web, Modifier.size(46.dp))
                    if (showFavorite) {
                        IconButton(onClick = { vm.toggleFavorite(web.id) }, modifier = Modifier.size(28.dp)) {
                            Icon(if (web.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = Color(0xFFFFC928), modifier = Modifier.size(18.dp))
                        }
                    }
                }
                Spacer(Modifier.height(5.dp))
                Text(web.name.take(14), fontSize = 11.sp, maxLines = 1)
            }
        }
    }
}

@Composable
private fun WebsiteIcon(web: WebsiteShortcut, modifier: Modifier) {
    val image = rememberImageBitmap(web.imageUri)
    if (image != null) {
        Image(image, null, modifier.clip(RoundedCornerShape(12.dp)), contentScale = ContentScale.Crop)
    } else {
        val icon = when (web.name.lowercase()) {
            "youtube" -> Icons.Default.Bolt
            "google" -> Icons.Default.Search
            "chatgpt" -> Icons.Default.AutoAwesome
            "netflix" -> Icons.Default.LiveTv
            else -> Icons.Default.Language
        }
        Surface(modifier = modifier, shape = RoundedCornerShape(12.dp), color = Color(0xFF0D284A), border = BorderStroke(1.dp, Blue)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Cyan, modifier = Modifier.size(28.dp)) }
        }
    }
}

@Composable
private fun RecentWebStrip(webs: List<RecentWebsite>, vm: HubViewModel) {
    Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        webs.forEach { recent ->
            val web = vm.websites.firstOrNull { it.id == recent.id } ?: WebsiteShortcut(recent.id, recent.name, recent.url, false, recent.imageUri)
            WebsiteTile(web, vm, false)
        }
    }
}

@Composable
private fun ViewAllButton(onClick: () -> Unit) {
    OutlinedButton(onClick = onClick, border = BorderStroke(1.dp, Color(0xFF2559A7)), modifier = Modifier.padding(top = 8.dp)) { Text("View all", color = Cyan) }
}

@Composable
private fun EmptyInline(text: String) { Text(text, color = Muted, fontSize = 12.sp, modifier = Modifier.padding(8.dp)) }

@Composable
private fun PageHeader(title: String, subtitle: String? = null, onBack: () -> Unit, action: (@Composable () -> Unit)? = null) {
    Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = Cyan) }
        Column(Modifier.weight(1f)) {
            Text(title, fontSize = 21.sp, fontWeight = FontWeight.Bold)
            if (subtitle != null) Text(subtitle, fontSize = 12.sp, color = Cyan)
        }
        action?.invoke()
    }
}

@Composable
private fun ToolsScreen(vm: HubViewModel, onBack: () -> Unit, onAdd: () -> Unit, onFeatures: () -> Unit) {
    val tools = listOf(
        HubTool("translate", "Screen Translation", "Translate text on screen", "translate"),
        HubTool("screenshot", "Screenshot", "Capture your screen", "screenshot"),
        HubTool("qr", "QR Scanner", "Scan QR codes", "qr"),
        HubTool("search", "Web Search", "Search anything", "search"),
        HubTool("lock", "App Lock", "Protect your apps", "lock"),
        HubTool("notes", "Notes", "Quick notes", "notes")
    )
    Column(Modifier.fillMaxSize()) {
        PageHeader("Convenient Tools", "Quick tools for everyday use", onBack, action = { RoundSmallAdd(onAdd) })
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tools) { tool -> ToolRow(tool) }
            item {
                PrimaryGradientButton("＋ Add Tool", onAdd)
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onFeatures, modifier = Modifier.fillMaxWidth()) { Text("Open More Features", color = Cyan) }
            }
        }
    }
}

@Composable
private fun ToolRow(tool: HubTool) {
    val icon = when (tool.iconKey) {
        "translate" -> Icons.Default.Translate
        "screenshot" -> Icons.Default.Image
        "qr" -> Icons.Default.QrCodeScanner
        "search" -> Icons.Default.Search
        "lock" -> Icons.Default.Lock
        else -> Icons.Default.Note
    }
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), color = Surface2, border = BorderStroke(1.dp, Color(0xFF1D4C8E))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(46.dp).background(Brush.linearGradient(listOf(Purple, Blue)), RoundedCornerShape(14.dp)), contentAlignment = Alignment.Center) {
                Icon(icon, null, tint = Color.White)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(tool.name, fontWeight = FontWeight.SemiBold); Text(tool.description, fontSize = 11.sp, color = Muted) }
            Icon(Icons.Default.ArrowForward, null, tint = Color(0xFF8BB3FF))
        }
    }
}

@Composable
private fun AddToolScreen(vm: HubViewModel, onBack: () -> Unit, onInstalled: () -> Unit, onQuick: () -> Unit, onShortcut: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("My Tools") }
    var selectedImage by remember { mutableStateOf<String?>(null) }
    var selectedApps by remember { mutableStateOf(setOf<String>()) }
    var selectedWebs by remember { mutableStateOf(setOf<Long>()) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selectedImage = uri?.toString() }

    Column(Modifier.fillMaxSize()) {
        PageHeader("Add Tool", onBack = onBack, action = { RoundSmallAdd { } })
        TabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
            Tab(tab == 0, { tab = 0 }, text = { Text("Custom Section") })
            Tab(tab == 1, { tab = 1 }, text = { Text("Quick Launch") })
        }
        if (tab == 0) {
            LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item {
                    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
                        Column(Modifier.padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Section Image", fontWeight = FontWeight.Bold, modifier = Modifier.align(Alignment.Start))
                            Spacer(Modifier.height(8.dp))
                            if (selectedImage != null) {
                                rememberImageBitmap(selectedImage)?.let { Image(it, null, Modifier.size(92.dp).clip(RoundedCornerShape(18.dp)), contentScale = ContentScale.Crop) }
                            } else Box(Modifier.size(92.dp).background(Color(0xFF0B2343), RoundedCornerShape(18.dp)), contentAlignment = Alignment.Center) { Icon(Icons.Default.Image, null, tint = Cyan, modifier = Modifier.size(42.dp)) }
                            Spacer(Modifier.height(8.dp))
                            OutlinedButton(onClick = { imagePicker.launch("image/*") }) { Text("Change Image") }
                        }
                    }
                }
                item {
                    Text("Section Name", fontWeight = FontWeight.SemiBold)
                    TextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), leadingIcon = { Icon(Icons.Default.Edit, null) }, shape = RoundedCornerShape(14.dp), colors = fieldColors())
                }
                item {
                    Text("Select Apps / Shortcuts", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(6.dp))
                    Text("Apps", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    vm.apps.take(12).forEach { app ->
                        PickerRow(app.label, selectedApps.contains(app.packageName), {
                            selectedApps = if (selectedApps.contains(app.packageName)) selectedApps - app.packageName else selectedApps + app.packageName
                        })
                    }
                    Spacer(Modifier.height(6.dp))
                    Text("Website shortcuts", color = Cyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    vm.websites.forEach { web ->
                        PickerRow(web.name, selectedWebs.contains(web.id), {
                            selectedWebs = if (selectedWebs.contains(web.id)) selectedWebs - web.id else selectedWebs + web.id
                        })
                    }
                }
                item { PrimaryGradientButton("Save", { vm.addSection(name, selectedImage, selectedApps.toList(), selectedWebs.toList()); onBack() }) }
            }
        } else {
            QuickLaunchScreen(vm, onBack, onInstalled, onShortcut)
        }
    }
}

@Composable
private fun PickerRow(title: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().padding(vertical = 3.dp).clickable { onClick() },
        RoundedCornerShape(14.dp),
        color = if (selected) Color(0xAA12406E) else Color(0x990A1930),
        border = BorderStroke(1.dp, if (selected) Cyan else Color(0xFF1C467F))
    ) {
        Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(12.dp).clip(CircleShape).background(if (selected) Cyan else Color.Transparent).then(Modifier.padding(1.dp)).border(1.dp, if (selected) Cyan else Color(0xFF496791), CircleShape))
            Spacer(Modifier.width(10.dp))
            Text(title, modifier = Modifier.weight(1f), fontSize = 12.sp)
            Text(if (selected) "Selected" else "Add", color = if (selected) Cyan else Muted, fontSize = 11.sp)
        }
    }
}

@Composable
private fun SelectableRow(title: String, desc: String, onClick: () -> Unit, selected: Boolean, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Surface(Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onClick() }, RoundedCornerShape(16.dp), color = if (selected) Color(0xAA12355B) else Surface2, border = BorderStroke(1.dp, Color(0xFF1B4D93))) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Cyan, modifier = Modifier.size(32.dp))
            Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(desc, color = Muted, fontSize = 11.sp) }
            Icon(Icons.Default.ArrowForward, null, tint = Color.White)
        }
    }
}

@Composable
private fun InstalledAppsScreen(vm: HubViewModel, onQuick: () -> Unit, onBack: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }
    val list = vm.apps.filter {
        (tab == 0 || (tab == 1 && it.isSystem) || (tab == 2 && !it.isSystem)) && it.label.contains(query, true)
    }
    Column(Modifier.fillMaxSize()) {
        PageHeader("Installed Apps", onBack = onBack, action = { IconButton(onClick = { query = if (query.isBlank()) " " else "" }) { Icon(Icons.Default.Search, null) } })
        SearchBar(query, { query = it }, {})
        TabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
            listOf("All Apps", "System", "User").forEachIndexed { index, title -> Tab(tab == index, { tab = index }, text = { Text(title, fontSize = 12.sp) }) }
        }
        LazyVerticalGrid(columns = GridCells.Fixed(3), modifier = Modifier.weight(1f).padding(horizontal = 12.dp), contentPadding = PaddingValues(vertical = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(list) { app -> AppTileLarge(app, vm) }
        }
        PrimaryGradientButton("＋ Add to Quick Launch", onQuick, Modifier.padding(horizontal = 14.dp, vertical = 10.dp))
    }
}

@Composable
private fun AppTileLarge(app: AppInfo, vm: HubViewModel, showAdd: Boolean = true) {
    val context = LocalContext.current
    val icon = remember(app.packageName) { runCatching { context.packageManager.getApplicationIcon(app.packageName).toBitmap(128, 128).asImageBitmap() }.getOrNull() }
    Surface(Modifier.fillMaxWidth().height(112.dp), RoundedCornerShape(16.dp), color = Surface2, border = BorderStroke(1.dp, Color(0xFF1D4B8C))) {
        Box(Modifier.fillMaxSize()) {
            Column(Modifier.fillMaxSize().clickable { vm.launchApp(app) }.padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                if (icon != null) Image(icon, null, Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))) else Box(Modifier.size(48.dp).background(Blue, RoundedCornerShape(12.dp)))
                Spacer(Modifier.height(5.dp)); Text(app.label.take(15), fontSize = 11.sp, maxLines = 1)
            }
            if (showAdd) {
                Surface(Modifier.align(Alignment.TopEnd).padding(5.dp).size(26.dp).clickable { vm.addQuickLaunchApp(app.packageName) }, CircleShape, color = Color(0xCC071B32), border = BorderStroke(1.dp, Cyan)) {
                    Icon(Icons.Default.Add, null, tint = Cyan, modifier = Modifier.padding(5.dp))
                }
            }
        }
    }
}

@Composable
private fun CustomShortcutScreen(vm: HubViewModel, onBack: () -> Unit) {
    var url by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<String?>(null) }
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> imageUri = uri?.toString() }
    Column(Modifier.fillMaxSize()) {
        PageHeader("Custom Shortcut (URL)", onBack = onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Surface(Modifier.fillMaxWidth(), RoundedCornerShape(20.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Enter URL", fontWeight = FontWeight.SemiBold)
                        TextField(url, { url = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("https://example.com") }, leadingIcon = { Icon(Icons.Default.Language, null) }, shape = RoundedCornerShape(14.dp), colors = fieldColors())
                        Spacer(Modifier.height(10.dp))
                        Text("Display Name", fontWeight = FontWeight.SemiBold)
                        TextField(name, { name = it }, modifier = Modifier.fillMaxWidth(), placeholder = { Text("Example") }, leadingIcon = { Icon(Icons.Default.Edit, null) }, shape = RoundedCornerShape(14.dp), colors = fieldColors())
                        Spacer(Modifier.height(10.dp))
                        Text("Icon", fontWeight = FontWeight.SemiBold)
                        Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(Icons.Default.Language, Icons.Default.Search, Icons.Default.Link, Icons.Default.Favorite, Icons.Default.Star, Icons.Default.Lock).forEach { icon ->
                                Surface(Modifier.size(42.dp), CircleShape, color = Color(0xFF0B2140), border = BorderStroke(1.dp, Purple)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(icon, null, tint = Cyan) } }
                            }
                            Surface(Modifier.size(42.dp).clickable { imagePicker.launch("image/*") }, CircleShape, color = Color(0xFF0B2140), border = BorderStroke(1.dp, Cyan)) { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Icon(Icons.Default.Download, null, tint = Cyan) } }
                        }
                        Spacer(Modifier.height(12.dp))
                        Surface(Modifier.fillMaxWidth().height(135.dp), RoundedCornerShape(18.dp), color = Color(0xFF071A33), border = BorderStroke(1.dp, Color(0xFF254F90))) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                if (imageUri != null) rememberImageBitmap(imageUri)?.let { Image(it, null, Modifier.size(62.dp).clip(RoundedCornerShape(14.dp)), contentScale = ContentScale.Crop) }
                                else Icon(Icons.Default.Language, null, tint = Cyan, modifier = Modifier.size(52.dp))
                                Column(Modifier.align(Alignment.BottomCenter).padding(bottom = 10.dp), horizontalAlignment = Alignment.CenterHorizontally) { Text(if (name.isBlank()) "Example" else name, fontWeight = FontWeight.Bold); Text(if (url.isBlank()) "https://example.com" else url, fontSize = 11.sp, color = Muted) }
                            }
                        }
                    }
                }
            }
            item { PrimaryGradientButton("Save", { vm.addWebsite(name.ifBlank { "Website" }, url.ifBlank { "https://example.com" }, imageUri); onBack() }) }
        }
    }
}

@Composable
private fun QuickLaunchScreen(vm: HubViewModel, onBack: () -> Unit, onAddApp: () -> Unit, onAddWeb: () -> Unit) {
    var tab by remember { mutableStateOf(0) }
    Column(Modifier.fillMaxSize()) {
        PageHeader("Quick Launch", "Your favorite apps & shortcuts", onBack)
        TabRow(selectedTabIndex = tab, containerColor = Color.Transparent) {
            Tab(tab == 0, { tab = 0 }, text = { Text("Apps") })
            Tab(tab == 1, { tab = 1 }, text = { Text("Shortcuts") })
        }
        if (tab == 0) {
            LazyVerticalGrid(columns = GridCells.Fixed(3), Modifier.weight(1f).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.quickLaunch.filter { it.type == "app" }) { entry -> vm.apps.firstOrNull { it.packageName == entry.key }?.let { AppTileLarge(it, vm) } }
                item { AddTile(onAddApp) }
            }
        } else {
            LazyVerticalGrid(columns = GridCells.Fixed(3), Modifier.weight(1f).padding(12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(vm.quickLaunch.filter { it.type == "web" }) { entry -> vm.websites.firstOrNull { it.id.toString() == entry.key }?.let { WebsiteGridTile(it, vm) } }
                item { AddTile(onAddWeb) }
            }
        }
    }
}

@Composable
private fun WebsiteGridTile(web: WebsiteShortcut, vm: HubViewModel) {
    Surface(Modifier.fillMaxWidth().height(112.dp).clickable { vm.openWeb(web) }, RoundedCornerShape(16.dp), color = Surface2, border = BorderStroke(1.dp, Color(0xFF1D4B8C))) {
        Column(Modifier.fillMaxSize().padding(8.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) { WebsiteIcon(web, Modifier.size(48.dp)); Spacer(Modifier.height(5.dp)); Text(web.name.take(15), fontSize = 11.sp) }
    }
}

@Composable
private fun AddTile(onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().height(112.dp).clickable { onClick() }, RoundedCornerShape(16.dp), color = Color.Transparent, border = BorderStroke(1.dp, Color(0xFF2A69B9))) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Column(horizontalAlignment = Alignment.CenterHorizontally) { Icon(Icons.Default.Add, null, tint = Cyan, modifier = Modifier.size(34.dp)); Text("Add", color = Cyan) } }
    }
}

@Composable
private fun MoreFeaturesScreen(vm: HubViewModel, onBackground: () -> Unit, onSettings: () -> Unit) {
    val rows = listOf(
        Triple(Icons.Default.Search, "Smart Search", "Find apps, tools & shortcuts instantly"),
        Triple(Icons.Default.Lock, "App Lock", "Protect your apps with password"),
        Triple(Icons.Default.AutoAwesome, "Theme Customization", "Change colors, style & background"),
        Triple(Icons.Default.CleaningServices, "Auto Clean", "Clear cache & junk files"),
        Triple(Icons.Default.History, "Recent Apps", "Quickly reopen your recent apps"),
        Triple(Icons.Default.WifiTethering, "Floating Badge", "See notifications at a glance"),
        Triple(Icons.Default.Save, "Backup & Restore", "Save your settings and data"),
        Triple(Icons.Default.TouchApp, "Gesture Support", "Use gestures for faster access")
    )
    Column(Modifier.fillMaxSize()) {
        PageHeader("More Features", onBack = onSettings, action = { RoundSmallAdd(onBackground) })
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(rows) { row ->
                Surface(Modifier.fillMaxWidth(), RoundedCornerShape(16.dp), color = Surface2, border = BorderStroke(1.dp, Color(0xFF1C4D93))) {
                    Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(42.dp).background(Brush.linearGradient(listOf(Purple, Blue)), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Icon(row.first, null, tint = Color.White) }
                        Spacer(Modifier.width(10.dp)); Column(Modifier.weight(1f)) { Text(row.second, fontWeight = FontWeight.SemiBold); Text(row.third, color = Muted, fontSize = 11.sp) }
                        Icon(Icons.Default.ArrowForward, null, tint = Color(0xFF8BB3FF))
                    }
                }
            }
        }
    }
}

@Composable
private fun BackgroundScreen(vm: HubViewModel, onBack: () -> Unit) {
    var selected by remember(vm.backgroundUri) { mutableStateOf(vm.backgroundUri) }
    var animation by remember { mutableStateOf(vm.backgroundAnimation) }
    var speed by remember { mutableStateOf(vm.backgroundSpeed.toFloat()) }
    var brightness by remember { mutableStateOf(vm.backgroundBrightness.toFloat()) }
    val localPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> selected = uri?.toString() }

    Column(Modifier.fillMaxSize()) {
        PageHeader("Neo Animated Background", "Dynamic, smooth and modern", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Surface(Modifier.fillMaxWidth().height(220.dp), RoundedCornerShape(22.dp), color = Surface1, border = BorderStroke(1.dp, BorderBrush())) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        val image = rememberImageBitmap(selected)
                        if (image != null) Image(image, null, Modifier.fillMaxSize().clip(RoundedCornerShape(22.dp)), contentScale = ContentScale.Crop) else Box(Modifier.fillMaxSize().background(Brush.linearGradient(listOf(Color(0xFF0D1D52), Color(0xFF5A12D6), Color(0xFF102B85))), RoundedCornerShape(22.dp)))
                        Surface(Modifier.align(Alignment.BottomEnd).padding(12.dp).clickable { localPicker.launch("image/*") }, RoundedCornerShape(14.dp), color = Color(0xB20B1830), border = BorderStroke(1.dp, Cyan)) { Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) { Icon(Icons.Default.Download, null, tint = Cyan); Spacer(Modifier.width(6.dp)); Text("Choose from phone") } }
                    }
                }
            }
            item {
                Text("Choose a background", fontWeight = FontWeight.Bold)
                Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("A", "B", "C", "D", "E").forEach { key ->
                        Box(Modifier.size(62.dp).clip(RoundedCornerShape(12.dp)).background(Brush.linearGradient(listOf(Color(0xFF122E84), Color(0xFF8217A7)))), contentAlignment = Alignment.Center) { Text(key, color = Color.White, fontWeight = FontWeight.Bold) }
                    }
                }
            }
            item {
                Surface(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
                    Column(Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) { Text("Animation", Modifier.weight(1f), fontWeight = FontWeight.SemiBold); Switch(animation, { animation = it }) }
                        Text("Speed", color = Muted, fontSize = 12.sp); Slider(speed, { speed = it }, valueRange = 10f..100f)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text("10%", color = Muted, fontSize = 11.sp); Text("${speed.toInt()}%", color = Cyan, fontSize = 11.sp); Text("100%", color = Muted, fontSize = 11.sp) }
                        Spacer(Modifier.height(8.dp)); Text("Glow / brightness", color = Muted, fontSize = 12.sp); Slider(brightness, { brightness = it }, valueRange = 20f..100f)
                    }
                }
            }
            item { PrimaryGradientButton("Apply & Save", { vm.setBackground(selected, animation, speed.toInt(), brightness.toInt()); onBack() }) }
        }
    }
}

@Composable
private fun SettingsScreen(vm: HubViewModel, onBack: () -> Unit) {
    val context = LocalContext.current
    Column(Modifier.fillMaxSize()) {
        PageHeader("Settings", "Alpha Hub preferences", onBack)
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { SettingsSwitch("Animations", "Enable UI and background motion", vm.animationsEnabled, vm::setAnimations) }
            item { SettingsSwitch("Compact mode", "Use denser cards and rows", vm.compactMode, vm::setCompact) }
            item {
                Surface(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
        Column(Modifier.padding(14.dp)) { Text("Panel opacity", fontWeight = FontWeight.SemiBold); Slider(value = vm.panelOpacity.toFloat(), onValueChange = { value -> vm.setOpacity(value.toInt()) }, valueRange = 60f..100f); Text("${vm.panelOpacity}%", color = Cyan, fontSize = 12.sp) }
                }
            }
            item { SettingsSwitch("Recent Apps & Websites", "Keep separate recent sections on Home", vm.recentsEnabled, vm::setRecents) }
            item { SettingsSwitch("Floating Badge", "Allow a floating quick-launch trigger", vm.bubbleEnabled, vm::setBubble) }
            item {
                ActionSettingsRow("Neo Animated Background", "Pick an image from Downloads or Gallery", Icons.Default.Image) {
                    context.startActivity(Intent(context, MainActivity::class.java))
                }
            }
            item {
                ActionSettingsRow("Overlay permission", "Required only for future floating features", Icons.Default.VerifiedUser) {
                    runCatching { context.startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))) }
                }
            }
            item {
                OutlinedButton(onClick = vm::clearRecents, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Color(0xFF7C4DFF))) { Icon(Icons.Default.Delete, null); Spacer(Modifier.width(6.dp)); Text("Clear Recent History") }
            }
        }
    }
}

@Composable
private fun SettingsSwitch(title: String, desc: String, checked: Boolean, onChecked: (Boolean) -> Unit) {
    Surface(Modifier.fillMaxWidth(), RoundedCornerShape(18.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(desc, fontSize = 11.sp, color = Muted) }
            Switch(checked, onCheckedChange = onChecked)
        }
    }
}

@Composable
private fun ActionSettingsRow(title: String, desc: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(Modifier.fillMaxWidth().clickable(onClick = onClick), RoundedCornerShape(18.dp), color = Surface1, border = BorderStroke(1.dp, Color(0xFF224B91))) {
        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = Cyan, modifier = Modifier.size(30.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.SemiBold); Text(desc, fontSize = 11.sp, color = Muted) }; Icon(Icons.Default.ArrowForward, null, tint = Cyan)
        }
    }
}

@Composable
private fun ShortcutsScreen(vm: HubViewModel, onAdd: () -> Unit, onQuick: () -> Unit, onBack: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        PageHeader("Shortcuts", "Web favorites and quick links", onBack = onBack, action = { RoundSmallAdd(onAdd) })
        LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item { PrimaryGradientButton("＋ Add Website Shortcut", onAdd, Modifier.padding(horizontal = 14.dp)) }
            item { PrimaryGradientButton("Open Quick Launch", onQuick, Modifier.padding(horizontal = 14.dp), purple = true) }
            items(vm.websites) { web ->
                Surface(Modifier.fillMaxWidth().padding(horizontal = 14.dp), RoundedCornerShape(18.dp), color = Surface2, border = BorderStroke(1.dp, Color(0xFF1D4C8E))) {
                    Row(Modifier.padding(10.dp), verticalAlignment = Alignment.CenterVertically) {
                        WebsiteIcon(web, Modifier.size(52.dp)); Spacer(Modifier.width(12.dp)); Column(Modifier.weight(1f)) { Text(web.name, fontWeight = FontWeight.SemiBold); Text(web.url, color = Muted, fontSize = 11.sp) }
                        IconButton(onClick = { vm.toggleFavorite(web.id) }) { Icon(if (web.favorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder, null, tint = Color(0xFFFFC928)) }
                        IconButton(onClick = { vm.removeWebsite(web.id) }) { Icon(Icons.Default.Delete, null, tint = Color(0xFFFF6C93)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun BottomNavigation(current: HubScreen, onSelect: (HubScreen) -> Unit) {
    val items = listOf(
        Triple(HubScreen.HOME, Icons.Default.Home, "Home"),
        Triple(HubScreen.TOOLS, Icons.Default.Build, "Tools"),
        Triple(HubScreen.APPS, Icons.Default.GridView, "Apps"),
        Triple(HubScreen.SHORTCUTS, Icons.Default.Language, "Shortcuts")
    )
    Surface(Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 8.dp), RoundedCornerShape(28.dp), color = Color(0xD9030B19), border = BorderStroke(1.dp, Brush.linearGradient(listOf(Blue, Purple)))) {
        Row(Modifier.fillMaxWidth().padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly) {
            items.forEach { (screen, icon, label) ->
                val selected = current == screen
                Column(Modifier.weight(1f).clip(RoundedCornerShape(18.dp)).clickable { onSelect(screen) }.padding(vertical = 6.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(icon, null, tint = if (selected) Cyan else Color(0xFF8A98C8), modifier = Modifier.size(26.dp))
                    Text(label, color = if (selected) Cyan else Color(0xFF8A98C8), fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal)
                    if (selected) Box(Modifier.padding(top = 3.dp).width(56.dp).height(3.dp).clip(CircleShape).background(Cyan))
                }
            }
        }
    }
}

@Composable
private fun RoundSmallAdd(onClick: () -> Unit) {
    Surface(Modifier.size(42.dp), CircleShape, color = Color(0x33102644), border = BorderStroke(1.dp, Cyan)) { IconButton(onClick = onClick) { Icon(Icons.Default.Add, null, tint = Cyan) } }
}

@Composable
private fun PrimaryGradientButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, purple: Boolean = false) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = if (purple) Purple else Blue, contentColor = Color.White)
    ) { Text(text, fontWeight = FontWeight.Bold) }
}

@Composable
private fun fieldColors() = TextFieldDefaults.colors(
    focusedContainerColor = Color(0xFF081A31),
    unfocusedContainerColor = Color(0xFF081A31),
    focusedIndicatorColor = Cyan,
    unfocusedIndicatorColor = Color(0xFF234E96),
    focusedTextColor = Color.White,
    unfocusedTextColor = Color.White,
    cursorColor = Cyan
)

@Composable
private fun rememberImageBitmap(uriString: String?): ImageBitmap? {
    val context = LocalContext.current
    return produceState<ImageBitmap?>(initialValue = null, uriString) {
        value = uriString?.let { uri ->
            runCatching {
                context.contentResolver.openInputStream(Uri.parse(uri))?.use { stream -> BitmapFactory.decodeStream(stream)?.asImageBitmap() }
            }.getOrNull()
        }
    }.value
}

private fun BorderBrush(): Brush = Brush.linearGradient(listOf(Blue, Purple))


class AlphaHubFloatingService : Service(), LifecycleOwner {
    private val serviceLifecycle = LifecycleRegistry(this)
    override val lifecycle: Lifecycle get() = serviceLifecycle
    private lateinit var windowManager: WindowManager
    private lateinit var composeView: ComposeView
    private lateinit var params: WindowManager.LayoutParams
    private val vm by lazy { HubViewModel(application) }
    private var expandedState = androidx.compose.runtime.mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()
        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)
        createNotificationChannel()
        val notification = android.app.Notification.Builder(this, "alpha_hub_launcher")
            .setContentTitle("Alpha Hub")
            .setContentText("Floating launcher is active")
            .setSmallIcon(R.drawable.alpha_logo)
            .setOngoing(true)
            .build()
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1001, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE)
        } else {
            startForeground(1001, notification)
        }

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val dm = resources.displayMetrics
        val railWidth = (82 * dm.density).toInt()
        val railHeight = (164 * dm.density).toInt()
        params = WindowManager.LayoutParams(
            railWidth,
            railHeight,
            if (Build.VERSION.SDK_INT >= 26)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
            android.graphics.PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.START or Gravity.CENTER_VERTICAL
            x = 0
            y = 0
        }

        composeView = ComposeView(this)
        composeView.setViewTreeLifecycleOwner(this)
        composeView.setContent {
            AlphaHubTheme {
                val expanded = expandedState.value
                LaunchedEffect(expanded) {
                    updateWindowWidth(expanded)
                }
                FloatingLauncherOverlay(
                    vm = vm,
                    expanded = expanded,
                    onToggle = { expandedState.value = it }
                )
            }
        }
        windowManager.addView(composeView, params)
    }

    private fun updateWindowWidth(expanded: Boolean) {
        if (!::composeView.isInitialized) return
        val dm = resources.displayMetrics
        val width = if (expanded) (dm.widthPixels * 0.94f).toInt() else (82 * dm.density).toInt()
        val height = if (expanded) WindowManager.LayoutParams.MATCH_PARENT else (164 * dm.density).toInt()
        params.width = width
        params.height = height
        params.gravity = Gravity.START or Gravity.CENTER_VERTICAL
        runCatching { windowManager.updateViewLayout(composeView, params) }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(
                NotificationChannel(
                    "alpha_hub_launcher",
                    "Alpha Hub Floating Launcher",
                    NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    override fun onDestroy() {
        if (::composeView.isInitialized) runCatching { windowManager.removeView(composeView) }
        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

@Composable
private fun FloatingLauncherOverlay(
    vm: HubViewModel,
    expanded: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Box(Modifier.fillMaxSize()) {
        if (expanded) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color(0x24000000))
                    .clickable { onToggle(false) }
            )
        }

        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            FloatingRail(vm, expanded, onToggle)
            if (expanded) {
                AnimatedVisibility(
                    visible = true,
                    enter = slideInHorizontally(
                        initialOffsetX = { -it },
                        animationSpec = tween(380, easing = FastOutSlowInEasing)
                    ) + fadeIn(tween(380)),
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    ) + fadeOut(tween(300))
                ) {
                    Surface(
                        Modifier
                            .fillMaxHeight(0.82f)
                            .fillMaxWidth()
                            .padding(start = 6.dp, end = 8.dp),
                        shape = RoundedCornerShape(26.dp),
                        color = Color(0xCC101217),
                        border = BorderStroke(1.dp, Brush.linearGradient(listOf(Color(0xFF4E8CFF), Color(0xFF7D42FF)))),
                        shadowElevation = 18.dp
                    ) {
                        AlphaHubAppContent(vm)
                    }
                }
            }
        }
    }
}

@Composable
private fun FloatingRail(vm: HubViewModel, expanded: Boolean, onToggle: (Boolean) -> Unit) {
    Surface(
        Modifier
            .fillMaxSize()
            .padding(start = 6.dp, end = 2.dp)
            .pointerInput(expanded) {
                detectHorizontalDragGestures { _, dragAmount ->
                    if (!expanded && dragAmount > 12f) onToggle(true)
                    if (expanded && dragAmount < -12f) onToggle(false)
                }
            }
            .clickable { onToggle(!expanded) },
        shape = RoundedCornerShape(22.dp),
        color = Color(0xE6151A22),
        border = BorderStroke(1.dp, Color(0xFFB8C7D9)),
        shadowElevation = 14.dp
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    Modifier.width(54.dp).height(92.dp),
                    RoundedCornerShape(18.dp),
                    color = Color(0xCC10315A),
                    border = BorderStroke(1.dp, Brush.linearGradient(listOf(Cyan, Purple)))
                ) {
                    Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Icon(Icons.Default.Translate, null, tint = Cyan, modifier = Modifier.size(25.dp))
                        Box(Modifier.width(30.dp).height(2.dp).background(Color(0xFF6B8BC0)))
                        Icon(
                            if (expanded) Icons.Default.ArrowBack else Icons.Default.ArrowForward,
                            null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
                if (!expanded) {
                    Text("Alpha", color = Color(0xFFD7E5FF), fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
