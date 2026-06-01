import SwiftUI
import ComposeApp

struct BookifyRootView: View {
    @EnvironmentObject private var store: BookifyStore
    @EnvironmentObject private var settingsStore: SettingsStore

    var body: some View {
        let colors = BookifyColors.from(settingsStore.selectedTheme)
        let state = store.state

        mainContent(state: state, colors: colors)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
            .background(colors.bg.ignoresSafeArea())
            .safeAreaInset(edge: .bottom, spacing: 0) {
                BookifyBottomNavView(
                    selectedTab: state.tab,
                    wishlistCount: (state.wishlistedBooks as! [Book]).count,
                    onTabSelected: { store.selectTab($0) }
                )
            }
            .animation(.easeInOut(duration: 0.22), value: settingsStore.selectedTheme.name)
            .environment(\.bookifyColors, colors)
    }

    @ViewBuilder
    private func mainContent(state: BookifyUiState, colors: BookifyColors) -> some View {
        if let book = state.openBook {
            DetailView(book: book, onBack: { store.closeBook() })
                .transition(.move(edge: .bottom).combined(with: .opacity))
                .id(book.key)
        } else if state.openSettings {
            SettingsView(onBack: { store.closeSettings() })
                .transition(.move(edge: .bottom).combined(with: .opacity))
        } else if state.tab.name == AppTab.home.name {
            HomeView()
                .transition(.opacity)
        } else {
            WishlistView()
                .transition(.opacity)
        }
    }
}
