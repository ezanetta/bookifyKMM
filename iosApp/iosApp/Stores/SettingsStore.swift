import ComposeApp
import Foundation

@MainActor
final class SettingsStore: ObservableObject {
    let vm: SettingsViewModel
    @Published private(set) var selectedTheme: AppTheme
    private var subscription: FlowSubscription?

    init(graph: IosAppGraph) {
        let vm = graph.settingsViewModel()
        self.vm = vm
        self.selectedTheme = vm.selectedTheme.value as! AppTheme
        subscription = FlowWatcherKt.watchSettingsTheme(vm: vm) { [weak self] theme in
            self?.selectedTheme = theme
        }
    }

    deinit {
        subscription?.cancel()
    }

    func selectTheme(_ theme: AppTheme) { vm.selectTheme(theme: theme) }
}
