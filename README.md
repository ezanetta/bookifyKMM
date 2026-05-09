# Bookify — Kotlin Multiplatform Book Browser

A book discovery app built with **Kotlin Multiplatform** (KMM) and **Compose Multiplatform**, running natively on Android and iOS from a single shared codebase. The project was built as a portfolio piece to demonstrate cross-platform mobile architecture, clean code structure, and thorough unit testing.

---

## Demo

### Android

<video src="media/demo_android.mp4" width="320" controls></video>

### iOS

<video src="media/demo_ios.mp4" width="320" controls></video>

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

The project follows **Clean Architecture** with a strict separation into three layers. All shared logic lives in `composeApp/src/commonMain`, compiled for both platforms.

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
    └── presentation/       # Presentation layer
        ├── viewmodel/      # ViewModels with StateFlow-based state
        ├── screen/         # Composable screens
        ├── components/     # Reusable UI components
        └── theme/          # Colors, typography, density tokens
```

### Data Layer

- **DTOs** map directly to the OpenLibrary API JSON structure.
- **`BookMapper`** converts raw API data into clean domain models, handling missing fields and constructing cover image URLs.
- **Repository implementations** use Kotlin's `Result<T>` for explicit success/failure modeling. `BookRepositoryImpl` applies a per-genre in-memory cache; `WishlistRepositoryImpl` and `ThemeRepositoryImpl` read and write to platform-native local storage.

### Domain Layer

Pure Kotlin — no framework dependencies. Defines the `Book`, `Genre`, `AppTheme`, and `AppTab` models, and repository interfaces that the data layer implements and the presentation layer depends on.

### Presentation Layer

- **ViewModels** expose `StateFlow<UiState>` and consume domain repositories through interfaces, making them fully testable without Android or iOS dependencies.
- **`BookifyViewModel`** owns the top-level app state: current tab, selected genre, books per genre, wishlist, and loading/error conditions.
- **`DetailViewModel`** and **`SettingsViewModel`** handle isolated screen concerns.
- UI is built entirely with **Compose Multiplatform**, sharing 100% of screen and component code between platforms.

### Dependency Injection

[Koin](https://insert-koin.io/) is used for dependency injection across all layers. Platform-specific modules (Android/iOS) wire up platform implementations of local storage while the rest of the graph is shared.

---

## Tech Stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform 1.10 |
| Networking | Ktor 3.1 (OkHttp / Darwin engines) |
| Serialization | Kotlinx Serialization 1.8 |
| Image loading | Coil 3.2 |
| Dependency Injection | Koin 4.0 |
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

KMM's `expect`/`actual` mechanism is used only where necessary:

- **`getPlatform()`** — returns platform name/version (used for diagnostics).
- **`platformModule`** (Koin) — each platform provides its own `LocalStorage` implementation: `SharedPreferencesSettings` on Android, `NSUserDefaultsSettings` on iOS.
- **Network engine** — Ktor uses OkHttp on Android and the Darwin (URLSession) engine on iOS.

---

## Build & Run

### Android

```shell
./gradlew :composeApp:assembleDebug
```

Or run the `composeApp` configuration directly from Android Studio.

### iOS

Open `/iosApp/iosApp.xcodeproj` in Xcode and run on simulator or device. Requires macOS with Xcode installed.
