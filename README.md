# Bookify — Kotlin Multiplatform Book Browser

A book discovery app built with **Kotlin Multiplatform** (KMM), using **Jetpack Compose** on Android and **SwiftUI** on iOS. Business logic, networking, and ViewModels live in a single shared Kotlin codebase; each platform renders the UI natively. The project was built as a portfolio piece to demonstrate cross-platform mobile architecture, clean code structure, and thorough unit testing.

---

## Demo

| Android | iOS |
|:---:|:---:|
| <img src="media/demo_android.gif" width="300"/> | <img src="media/demo_ios.gif" width="300"/> |

---

## Features

- Browse books by genre (Fantasy, Science Fiction, Thriller, Horror)
- Book detail screen with cover, author, and subject tags
- Wishlist — add and remove books, persisted across sessions
- Theme selection (Library, Bone, Sage, Ink) — preference saved locally
- Per-genre in-memory cache to avoid redundant network calls
- Error states with retry support

---

## Architecture

The project follows **Clean Architecture** with a strict separation into three layers. All shared logic (data, domain, ViewModels) lives in `composeApp/src/commonMain`. Platform UI lives separately: Compose screens in `androidMain`, SwiftUI screens in `iosApp`.

```
composeApp/src/commonMain/
└── com.ezanetta.bookifykmm/
    ├── data/               # Data layer
    │   ├── dto/            # API response models (serializable)
    │   ├── mapper/         # DTO → Domain model conversion
    │   ├── network/        # Ktor API service interface & implementation
    │   ├── local/          # Local storage abstraction (Multiplatform Settings)
    │   └── repository/     # Repository implementations
    ├── domain/             # Domain layer
    │   ├── model/          # Core entities: Book, Genre, AppTheme, AppTab
    │   └── repository/     # Repository interfaces (contracts)
    └── presentation/
        └── viewmodel/      # ViewModels with StateFlow-based state

composeApp/src/androidMain/
└── com.ezanetta.bookifykmm/
    └── presentation/       # Android UI (Jetpack Compose)
        ├── screen/         # Composable screens
        ├── components/     # Reusable UI components
        └── theme/          # Colors, typography, density tokens

composeApp/src/iosMain/
└── com.ezanetta.bookifykmm/
    └── di/                 # AppContainer (exposes IosAppGraph to Swift)
                            # FlowWatcher (callback-based StateFlow helpers)

iosApp/iosApp/
├── Stores/                 # @Observable wrappers around KMM ViewModels
├── Screens/                # SwiftUI screens
├── Components/             # Reusable SwiftUI components
└── Theme/                  # Colors and typography (BookifyTheme)
```

### Data Layer

- **DTOs** map directly to the OpenLibrary API JSON structure.
- **`BookMapper`** converts raw API data into clean domain models, handling missing fields and constructing cover image URLs.
- **Repository implementations** use Kotlin's `Result<T>` for explicit success/failure modeling. `BookRepositoryImpl` applies a per-genre in-memory cache; `WishlistRepositoryImpl` and `ThemeRepositoryImpl` read and write to platform-native local storage.

### Domain Layer

Pure Kotlin — no framework dependencies. Defines the `Book`, `Genre`, `AppTheme`, and `AppTab` models, and repository interfaces that the data layer implements and the presentation layer depends on.

### Presentation Layer

**ViewModels** (`commonMain`) expose `StateFlow<UiState>` and consume domain repositories through interfaces, making them fully testable without Android or iOS dependencies. `BookifyViewModel` owns the top-level app state; `DetailViewModel` and `SettingsViewModel` handle isolated screen concerns.

**Android UI** (`androidMain`) is built with Jetpack Compose. Screens and components consume the shared ViewModels directly via the DI graph.

**iOS UI** (`iosApp`) is built entirely in SwiftUI. Each screen is backed by a Swift `@Observable` Store that holds a reference to the corresponding KMM ViewModel and bridges its `StateFlow` to SwiftUI state using `FlowWatcher` helpers.

### Dependency Injection

[Metro 1.0.0](https://zacsweers.github.io/metro/latest/) is used for dependency injection — a compile-time KMP DI framework that validates the entire dependency graph at **build time** via a Kotlin compiler plugin. Wiring errors surface as compile errors instead of runtime crashes.

The graph is structured around a common `AppGraph` interface (exposing ViewModel factory functions) with platform-specific `@DependencyGraph` implementations in `androidMain` and `iosMain`. Platform-specific bindings (e.g. `LocalStorage`) are contributed automatically via `@ContributesTo(AppScope::class)` binding containers in each platform source set, replacing the old `expect`/`actual` module pattern.

On iOS, `AppContainer.shared.graph` exposes the `IosAppGraph` to Swift, giving SwiftUI Stores access to all KMM ViewModels.

---

## Tech Stack

| Concern | Library |
|---|---|
| Android UI | Jetpack Compose |
| iOS UI | SwiftUI |
| Networking | Ktor 3.1 (OkHttp / Darwin engines) |
| Serialization | Kotlinx Serialization 1.8 |
| Image loading | Coil 3.2 (Android) / AsyncImage (iOS) |
| Dependency Injection | Metro 1.0 |
| Local storage | Multiplatform Settings 1.3 |
| Async | Kotlin Coroutines 1.10 + StateFlow |
| Book data | OpenLibrary.org (public API, no auth) |

---

## Unit Tests

The project has a dedicated `commonTest` source set with **8 test classes** covering all layers. Tests run on both platforms from shared code.

| Test class | What it covers |
|---|---|
| `BookRepositoryImplTest` | API fetching, per-genre caching, error propagation |
| `BookMapperTest` | DTO-to-domain mapping, cover URL construction, null handling |
| `BookifyViewModelTest` | App-wide state transitions, tab/genre switching, wishlist sync |
| `DetailViewModelTest` | Wishlist toggle, optimistic UI state |
| `SettingsViewModelTest` | Theme selection and persistence |
| `BookListViewModelTest` | Genre loading, loading/error state lifecycle |
| `WishlistRepositoryTest` | Add, remove, and persist wishlist entries |
| `ThemeRepositoryTest` | Read and write theme preference |

**Testing tools:** `kotlin.test`, `kotlinx-coroutines-test`, [Turbine](https://github.com/cashapp/turbine) for Flow assertions, and `multiplatform-settings-test` for in-memory storage fakes.

---

## Platform-Specific Code

### KMM `expect`/`actual`

Used only where necessary:

- **`getPlatform()`** — returns platform name/version (used for diagnostics).
- **`LocalStorage` binding** — each platform contributes its own `LocalStorage` implementation: `SharedPreferencesSettings` on Android, `NSUserDefaultsSettings` on iOS.
- **Network engine** — Ktor uses OkHttp on Android and the Darwin (URLSession) engine on iOS.

### SwiftUI ↔ Kotlin Bridge

The `iosMain` source set exposes two bridge utilities to Swift:

- **`AppContainer`** — singleton that initialises the Metro DI graph and exposes `IosAppGraph` to Swift via `AppContainer.shared.graph`.
- **`FlowWatcher`** — typed top-level functions (`watchBookifyState`, `watchDetailIsWishlisted`, `watchSettingsTheme`) that subscribe to a KMM `StateFlow` and deliver updates via a Swift callback. Called from Swift as `FlowWatcherKt.watchX(...)`.

Each SwiftUI screen is backed by a Swift `@Observable` Store that holds the KMM ViewModel, calls the appropriate `FlowWatcher` on appear, and drives SwiftUI state from the emitted values.

---

## Build & Run

### Android

```shell
./gradlew :composeApp:assembleDebug
```

Or run the `composeApp` configuration directly from Android Studio.

### iOS

Open `/iosApp/iosApp.xcodeproj` in Xcode and run on simulator or device. Requires macOS with Xcode installed.
