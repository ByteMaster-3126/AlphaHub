# Alpha Hub — reconstruction / renovation starter

This project is a native Android / Jetpack Compose recreation of the Alpha Hub UI discussed in the supplied screen recording and design references.

## What is implemented

### Home
- Alpha Imperium gold logo in the header.
- Neon blue/purple animated background.
- Search field with microphone action.
- Installed Apps section with independent **+ Add**.
- Recent Apps section directly below Installed Apps.
- Recent Websites section as its own section.
- Websites / favorites section with independent **+ Add**.
- Custom sections are rendered on Home after they are created.
- Bottom navigation: Home, Tools, Apps, Shortcuts.

### Tools
- Convenient Tools page with:
  - Screen Translation
  - Screenshot
  - QR Scanner
  - Web Search
  - App Lock
  - Notes
- Add Tool entry point.

### Add Tool
- Custom Section tab.
- Pick a section image from the Android system picker (Downloads / Gallery / other document providers).
- Section name.
- Select installed apps and website shortcuts.
- Quick Launch tab.

### Installed Apps
- Dynamically loads launcher apps from PackageManager.
- All / System / User tabs.
- Real app icons.
- Tap app to launch it.
- Small **+** on each app to add it to Quick Launch.

### Custom Shortcut (URL)
- URL field.
- Display name field.
- Built-in icon choices.
- Pick a custom icon image from the phone.
- Live preview.
- Save to Websites / Recent Websites.

### Quick Launch
- Separate Apps and Shortcuts tabs.
- Launch saved apps and websites.
- Add tiles for continuing configuration.

### More Features
- Smart Search
- App Lock
- Theme Customization
- Auto Clean
- Recent Apps
- Floating Badge
- Backup & Restore
- Gesture Support
- Entry to Neo Animated Background

### Neo Animated Background
- Pick a background image from the phone using the system picker.
- Animation on/off.
- Animation speed.
- Glow / brightness control.
- Apply & Save.
- The selected image is persisted with SharedPreferences.

### Settings
- Animations
- Compact mode
- Panel opacity
- Recent Apps & Websites
- Floating Badge toggle
- Overlay-permission entry point
- Clear recent history

## Important scope note

The supplied APK was inspected as a reference implementation. Its codebase contains a substantially larger QuickLaunch feature set (search providers, overlay service, recents repository, pinned shortcuts, voice search, settings providers, calculator, clipboard, contacts, files, torch, unit conversion, etc.). This reconstruction deliberately focuses first on the Alpha Hub pages and interactions requested in the recording/design references so the UI and information architecture can be iterated cleanly.

Advanced tool internals such as full QR scanning, system-wide screenshot capture, a real app-lock service, cache cleaning, and a system-wide floating launcher are left as the next implementation layer rather than pretending they are complete.

## Build

Open the folder in Android Studio. Use an Android Gradle Plugin / Kotlin setup compatible with the versions declared in `build.gradle.kts`, allow Gradle to resolve dependencies, then build the `app` module.

Minimum SDK: 26  
Target SDK: 35

## Design constants

The design uses a deep navy/black base with cyan, electric-blue, purple and magenta accents; rounded glass cards; thin neon borders; and the Alpha Imperium gold logo as the brand mark.

## Key files

- `app/src/main/java/com/alphaimperium/alphahub/MainActivity.kt` — UI/navigation
- `app/src/main/java/com/alphaimperium/alphahub/HubViewModel.kt` — persistence + app/web data
- `app/src/main/java/com/alphaimperium/alphahub/Models.kt` — state models
- `app/src/main/java/com/alphaimperium/alphahub/ui/theme/Theme.kt` — Compose theme
- `app/src/main/res/drawable/alpha_logo.png` — cropped Alpha Imperium logo reference
