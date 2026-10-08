package com.alphaimperium.alphahub

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo
import android.graphics.Color as AndroidColor
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.os.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.setViewTreeLifecycleOwner
import com.alphaimperium.alphahub.ui.theme.AlphaHubTheme

/**
 * Robust floating launcher service.
 *
 * The collapsed handle is a native Android view and is created before any
 * Compose content. This prevents a Compose/lifecycle failure from hiding the
 * launcher trigger on first startup.
 */
class AlphaHubFloatingServiceV2 : Service(), LifecycleOwner {

    companion object {
        private const val TAG = "AlphaHubFloating"
        private const val ACTION_STOP = "com.alphaimperium.alphahub.STOP_FLOATING"
    }

    private val serviceLifecycle = LifecycleRegistry(this)
    override val lifecycle: Lifecycle
        get() = serviceLifecycle

    private lateinit var windowManager: WindowManager
    private lateinit var root: FrameLayout
    private lateinit var params: WindowManager.LayoutParams
    private var composeView: ComposeView? = null
    private val expandedState = mutableStateOf(false)

    override fun onCreate() {
        super.onCreate()

        try {
            serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
            serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_START)

            createNotificationChannel()

            val notification = android.app.Notification.Builder(
                this,
                "alpha_hub_launcher"
            )
                .setContentTitle("Alpha Hub")
                .setContentText("Floating launcher is active")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setOngoing(true)
                .build()

            if (Build.VERSION.SDK_INT >= 34) {
                startForeground(
                    1002,
                    notification,
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
                )
            } else {
                startForeground(1002, notification)
            }

            windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

            val dm = resources.displayMetrics
            val width = (82 * dm.density).toInt()
            val height = (164 * dm.density).toInt()

            params = WindowManager.LayoutParams(
                width,
                height,
                if (Build.VERSION.SDK_INT >= 26) {
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                } else {
                    WindowManager.LayoutParams.TYPE_PHONE
                },
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                android.graphics.PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.START or Gravity.CENTER_VERTICAL
                x = 0
                y = 0
            }

            // IMPORTANT: create the native handle first.
            root = FrameLayout(this)
            root.addView(createNativeHandle())
            windowManager.addView(root, params)
        } catch (t: Throwable) {
            Log.e(TAG, "Floating launcher startup failed", t)
            stopSelf()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            Log.i(TAG, "Stop requested from notification")
            stopSelf()
            return START_NOT_STICKY
        }
        Log.d(TAG, "Service start command received")
        if (::root.isInitialized && root.childCount == 0 && !expandedState.value) {
            runCatching { root.addView(createNativeHandle()) }
                .onFailure { Log.e(TAG, "Could not restore native handle", it) }
        }
        return START_NOT_STICKY
    }

    private fun createNativeHandle(): View {
        val handle = TextView(this).apply {
            text = "›\nALPHA"
            setTextColor(AndroidColor.WHITE)
            textSize = 12f
            gravity = Gravity.CENTER
            setPadding(0, 8, 0, 8)
            contentDescription = "Open Alpha Hub floating launcher"
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    AndroidColor.rgb(12, 38, 76),
                    AndroidColor.rgb(33, 25, 74)
                )
            ).apply {
                cornerRadius = 30f
                setStroke(2, AndroidColor.rgb(91, 145, 255))
            }
            isClickable = true
            isFocusable = true
            setOnClickListener {
                Log.d(TAG, "Native handle click")
                expandLauncher()
            }
            setOnTouchListener { _, event ->
                when (event.actionMasked) {
                    MotionEvent.ACTION_DOWN -> {
                        Log.d(TAG, "Native handle touch DOWN")
                        false
                    }
                    MotionEvent.ACTION_UP -> {
                        Log.d(TAG, "Native handle touch UP -> expand")
                        performClick()
                        true
                    }
                    else -> false
                }
            }
        }

        return FrameLayout(this).apply {
            addView(
                handle,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }
    }

    private fun expandLauncher() {
        if (expandedState.value) return
        Log.i(TAG, "Expanding floating launcher")
        expandedState.value = true

        val dm = resources.displayMetrics
        params.width = (dm.widthPixels * 0.94f).toInt()
        params.height = WindowManager.LayoutParams.MATCH_PARENT
        params.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        runCatching {
            windowManager.updateViewLayout(root, params)

            // Remove the native handle and create Compose only now.
            root.removeAllViews()

            val view = ComposeView(this)
            view.setViewTreeLifecycleOwner(this)
            composeView = view

            view.setContent {
                AlphaHubTheme {
                    FloatingLauncherOverlay(
                        vm = HubViewModel(application),
                        expanded = true,
                        onToggle = { open ->
                            if (open) expandedState.value = true
                            else collapseLauncher()
                        }
                    )
                }
            }

            root.addView(
                view,
                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            )
        }.onFailure {
            Log.e(TAG, "Floating launcher Compose expansion failed", it)
            collapseLauncher()
        }
    }

    private fun collapseLauncher() {
        Log.i(TAG, "Collapsing floating launcher")
        expandedState.value = false

        composeView?.let { view ->
            runCatching { root.removeView(view) }
        }
        composeView = null

        val dm = resources.displayMetrics
        params.width = (82 * dm.density).toInt()
        params.height = (164 * dm.density).toInt()
        params.gravity = Gravity.START or Gravity.CENTER_VERTICAL

        runCatching {
            windowManager.updateViewLayout(root, params)
            root.removeAllViews()
            root.addView(createNativeHandle())
        }.onFailure {
            Log.e(TAG, "Failed to restore native handle after collapse", it)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager

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
        composeView?.let { runCatching { root.removeView(it) } }
        if (::root.isInitialized) {
            runCatching { windowManager.removeView(root) }
        }

        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        serviceLifecycle.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)

        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
