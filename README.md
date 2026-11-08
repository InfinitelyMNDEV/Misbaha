# Misbaha · المسبحة

An Arabic-first electronic tasbeeh for Android, built with Kotlin, Jetpack Compose Material 3, Room, DataStore, and Navigation Compose.

![Home screen](home.png)

## Features

- Built-in adhkar in the required order: سبحان الله، الحمد لله، الله أكبر، لا إله إلا الله، أستغفر الله، اللهم صل على محمد وآل محمد، تسبيح الزهراء (عليها السلام)
- Limitless standard counter with a permanent total and a today count that resets at local midnight
- Dedicated Zahra counter with three phases of 33, a progress ring, phase and full-cycle feedback, and persisted in-progress state
- Custom adhkar with add, edit, and delete support, empty and duplicate validation, and long-text handling
- Settings for theme (Light, Dark, System), font size (Small, Default, Large, Extra large), and language (Arabic, English)
- Immediate locale switching with RTL-aware layouts and localized numerals
- Room persistence with every tap saved immediately, plus DataStore-backed settings
- Light and dark palettes with a single green accent, bundled Tajawal Arabic font, adaptive launcher icon with monochrome variant, and SplashScreen API support
- Unit tests for counter limits, midnight reset, and Zahra progression

## Requirements

- Windows, macOS, or Linux
- Android Studio with support for Android Gradle Plugin 9.5.1
- JDK 17 or newer
- Android SDK Platform 37 and Build Tools 36.0.0
- An Android device or emulator running Android 8.0 (API 26) or newer

The project uses Gradle 9.6.0 through the included wrapper. A separate Gradle installation is not required.

## Open the project

1. Open Android Studio.
2. Choose **File → Open** and select the project root directory.
3. Allow Android Studio to sync the project and download dependencies.
4. Confirm that the SDK location is configured. Android Studio usually creates `local.properties` automatically. If needed, create it in the project root:

```properties
sdk.dir=C\:\\Users\\<user>\\AppData\\Local\\Android\\Sdk
```

Forward slashes also work:

```properties
sdk.dir=C:/Users/<user>/AppData/Local/Android/Sdk
```

`local.properties` is machine-specific and is ignored by Git.

## Build and run

From Android Studio, select the `app` configuration and press **Run**.

From a terminal in the project root on Windows:

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\gradlew.bat assembleDebug
```

On macOS or Linux:

```bash
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew assembleDebug
```

The debug APK is written to:

```text
app/build/outputs/apk/debug/app-debug.apk
```

Install it on a connected device or running emulator:

```powershell
adb install -r app\build\outputs\apk\debug\app-debug.apk
```

## Tests and lint

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug
```

The unit tests cover:

- Unbounded growth of the standard total count
- Today-count reset when the stored date is before the local date
- Zahra phase advancement at 33
- Zahra cycle completion and wrap-around to phase zero
- Persisted in-progress Zahra phase and count

## Release APK

### Option 1: Debug-signed release build

If `keystore.properties` is absent, the release build falls back to the debug signing key so the APK remains installable for testing:

```powershell
.\gradlew.bat assembleRelease
```

Output:

```text
app/build/outputs/apk/release/app-release.apk
```

This fallback is convenient for local verification but is not suitable for distribution.

### Option 2: Signed release build

1. Create a keystore with `keytool`:

```powershell
keytool -genkeypair -v -keystore misbaha-release.jks -alias misbaha -keyalg RSA -keysize 4096 -validity 10000
```

2. Create `keystore.properties` in the project root:

```properties
storeFile=misbaha-release.jks
storePassword=YOUR_STORE_PASSWORD
keyAlias=misbaha
keyPassword=YOUR_KEY_PASSWORD
```

The `storeFile` path is resolved relative to the project root.

3. Build the signed release APK:

```powershell
.\gradlew.bat assembleRelease
```

4. Verify the signature:

```powershell
apksigner verify --verbose app\build\outputs\apk\release\app-release.apk
```

Keep `keystore.properties`, `*.jks`, and `*.keystore` private. They are already listed in `.gitignore`.

The release build currently has minification and resource shrinking disabled. If you enable them, keep the existing `proguard-rules.pro` file and re-test Room, DataStore, and Compose behavior.

## Project structure

```text
Misbaha/
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/misbaha/app/
│       │   │   ├── AppViewModelProvider.kt
│       │   │   ├── MainActivity.kt
│       │   │   ├── MisbahaApplication.kt
│       │   │   ├── data/
│       │   │   │   ├── local/
│       │   │   │   │   ├── MisbahaDatabase.kt
│       │   │   │   │   ├── ZikrConverters.kt
│       │   │   │   │   ├── ZikrDao.kt
│       │   │   │   │   └── ZikrEntity.kt
│       │   │   │   ├── preferences/
│       │   │   │   │   ├── LocaleController.kt
│       │   │   │   │   └── SettingsRepository.kt
│       │   │   │   └── repository/
│       │   │   │       └── ZikrRepository.kt
│       │   │   ├── di/
│       │   │   │   └── AppContainer.kt
│       │   │   ├── domain/
│       │   │   │   ├── counter/
│       │   │   │   │   └── CounterRules.kt
│       │   │   │   ├── model/
│       │   │   │   │   ├── AppSettings.kt
│       │   │   │   │   ├── Zikr.kt
│       │   │   │   │   └── ZikrType.kt
│       │   │   │   ├── time/
│       │   │   │   │   └── AppClock.kt
│       │   │   │   └── validation/
│       │   │   │       └── ZikrTextValidator.kt
│       │   │   └── ui/
│       │   │       ├── components/
│       │   │       │   ├── ConfirmDialog.kt
│       │   │       │   ├── CounterCircle.kt
│       │   │       │   ├── CounterStats.kt
│       │   │       │   ├── MisbahaTopBar.kt
│       │   │       │   ├── SegmentedSelector.kt
│       │   │       │   ├── ZikrCard.kt
│       │   │       │   └── ZikrFormDialog.kt
│       │   │       ├── counter/
│       │   │       │   ├── CounterScreen.kt
│       │   │       │   └── CounterViewModel.kt
│       │   │       ├── home/
│       │   │       │   ├── HomeDrawer.kt
│       │   │       │   ├── HomeScreen.kt
│       │   │       │   └── HomeViewModel.kt
│       │   │       ├── navigation/
│       │   │       │   └── MisbahaNavHost.kt
│       │   │       ├── settings/
│       │   │       │   ├── SettingsScreen.kt
│       │   │       │   └── SettingsViewModel.kt
│       │   │       ├── theme/
│       │   │       │   ├── Color.kt
│       │   │       │   ├── Dimens.kt
│       │   │       │   ├── Theme.kt
│       │   │       │   └── Type.kt
│       │   │       └── util/
│       │   │           ├── LastUsedFormatter.kt
│       │   │           ├── SystemBars.kt
│       │   │           └── TextFormatting.kt
│       │   └── res/
│       │       ├── drawable/
│       │       ├── font/
│       │       ├── mipmap-anydpi-v26/
│       │       ├── values/
│       │       ├── values-ar/
│       │       ├── values-night/
│       │       └── xml/
│       └── test/java/com/misbaha/app/domain/counter/
│           └── CounterRulesTest.kt
├── gradle/
│   ├── libs.versions.toml
│   └── wrapper/
├── licenses/
│   └── OFL-Tajawal.txt
├── build.gradle.kts
├── gradle.properties
├── gradlew
├── gradlew.bat
├── settings.gradle.kts
└── README.md
```

## Architecture

The app follows MVVM with unidirectional data flow and manual dependency injection.

- **UI:** Compose screens collect `StateFlow` state from ViewModels and send user actions back as function calls.
- **ViewModels:** `HomeViewModel`, `CounterViewModel`, and `SettingsViewModel` expose immutable UI state.
- **Domain:** `CounterRules` contains the pure counting logic, including midnight handling and Zahra phase transitions.
- **Data:** `ZikrRepository` owns Room transactions. `SettingsRepository` owns DataStore preferences.
- **DI:** `AppContainer` is created by `MisbahaApplication` and supplies repositories, the database, and the clock. `AppViewModelProvider` builds ViewModels from that container.

Room stores both built-in and custom adhkar in a single table. Standard adhkar use `totalCount` and `todayCount`; the Zahra row uses `totalCount` for completed cycles, `todayCount` for cycles completed today, and separate phase fields for in-progress state. This keeps the home list ordering and updates uniform.

Settings are stored in Preferences DataStore. The selected language is applied through `AppCompatDelegate.setApplicationLocales`, with `AppLocalesMetadataHolderService` enabled so the locale is stored automatically. Arabic is the default on first launch.

## Counting behavior

- Every tap updates the UI immediately and is persisted through a single-writer coroutine channel, so rapid taps cannot overwrite each other.
- Standard counters have no upper limit.
- The today count is compared against the stored local date. When the date changes, the visible today count starts again at zero, while the total remains unchanged.
- Zahra counts 33 taps per phase across three phases. Completing the third phase adds one full cycle to the total and today count, then returns to phase zero.
- Reset clears the total, today count, and any in-progress Zahra phase after confirmation.

## Fonts

The app bundles the Tajawal typeface in Regular, Medium, and Bold weights. Tajawal is distributed under the SIL Open Font License 1.1. The license text is included at `licenses/OFL-Tajawal.txt`.

## Localization

English resources are the base values, and Arabic resources live in `values-ar`. On first launch the app selects Arabic. Changing the language in Settings updates the interface immediately and persists across launches. All layouts use start and end alignment so both RTL and LTR render correctly.

## Backup

Backup and data extraction rules preserve the Room database and settings. Counter totals, today counts, custom adhkar, and preferences survive device backup and restore where the platform supports it.
