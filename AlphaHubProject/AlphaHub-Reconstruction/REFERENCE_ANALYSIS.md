# Reference analysis notes

## Supplied screen recording

The recording shows the decision trail for the final Alpha Hub layout:

1. Remove the promotional “Make Your Life Easier” card.
2. Put Recent Apps directly below Installed Apps.
3. Keep Recent Websites as a separate section.
4. Keep independent section headers and Add buttons.
5. Preserve the dark blue/purple neon background and bottom navigation.
6. Replace the temporary letter “A” branding with the supplied Alpha Imperium gold bull logo.
7. Allow phone-based image selection instead of requiring hard-coded file paths.
8. Custom Section images should be chosen from Downloads / Gallery / the Android system picker.
9. Website shortcut icons can use images selected from the phone.
10. Neo Animated Background can use an imported image and expose animation speed / glow controls.

## Supplied APK

Static inspection of the APK shows a native Jetpack Compose application under the `com.profapps.quicklaunch` namespace and a QuickLaunch architecture containing repositories, search providers, an overlay service, floating-bubble service, pinned shortcuts, settings, voice search, and app indexing.

Notable provider classes visible in the APK include AppsProvider, CalculatorProvider, ClipboardProvider, ContactsProvider, FileSearchProvider, QuickInfoProvider, SettingsProvider, TorchProvider, UnitConversionProvider, and WebSearchProvider.

The reconstruction project uses a new application id (`com.alphaimperium.alphahub`) so it is a clean source recreation rather than claiming to be a cryptographically signed update of the supplied APK.
