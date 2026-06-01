import SwiftUI
import CoreText
import ComposeApp

@main
struct iOSApp: App {
    @StateObject private var bookifyStore: BookifyStore
    @StateObject private var settingsStore: SettingsStore

    init() {
        let graph = AppContainer.shared.graph
        _bookifyStore = StateObject(wrappedValue: BookifyStore(graph: graph))
        _settingsStore = StateObject(wrappedValue: SettingsStore(graph: graph))
        Self.registerFonts()
    }

    private static func registerFonts() {
        let files = [
            "dm_sans_regular", "dm_sans_medium", "dm_sans_semibold", "dm_sans_bold",
            "newsreader_regular", "newsreader_italic", "newsreader_medium", "newsreader_mediumitalic"
        ]
        for name in files {
            guard let url = Bundle.main.url(forResource: name, withExtension: "ttf") else { continue }
            CTFontManagerRegisterFontsForURL(url as CFURL, .process, nil)
        }
    }

    var body: some Scene {
        WindowGroup {
            BookifyRootView()
                .environmentObject(bookifyStore)
                .environmentObject(settingsStore)
        }
    }
}
