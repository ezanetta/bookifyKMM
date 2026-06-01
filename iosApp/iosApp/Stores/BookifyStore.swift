import ComposeApp
import Foundation

@MainActor
final class BookifyStore: ObservableObject {
    let vm: BookifyViewModel
    @Published private(set) var state: BookifyUiState
    private var subscription: FlowSubscription?

    init(graph: IosAppGraph) {
        let vm = graph.bookifyViewModel()
        self.vm = vm
        self.state = vm.state.value as! BookifyUiState
        subscription = FlowWatcherKt.watchBookifyState(vm: vm) { [weak self] newState in
            self?.state = newState
        }
    }

    deinit {
        subscription?.cancel()
    }

    func selectGenre(_ genre: Genre) { vm.selectGenre(genre: genre) }
    func openBook(_ book: Book) { vm.openBook(book: book) }
    func closeBook() { vm.closeBook() }
    func selectTab(_ tab: AppTab) { vm.selectTab(tab: tab) }
    func openSettings() { vm.openSettings() }
    func closeSettings() { vm.closeSettings() }
    func retryLoad() { vm.retryLoad() }
}
