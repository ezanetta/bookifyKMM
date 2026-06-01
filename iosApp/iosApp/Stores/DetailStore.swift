import ComposeApp
import Foundation

@MainActor
final class DetailStore: ObservableObject {
    let vm: DetailViewModel
    @Published private(set) var isWishlisted: Bool = false
    private var subscription: FlowSubscription?

    init(graph: IosAppGraph, bookKey: String) {
        let vm = graph.detailViewModel()
        self.vm = vm
        vm.initialize(bookKey: bookKey)
        self.isWishlisted = (vm.isWishlisted.value as? KotlinBoolean)?.boolValue ?? false
        subscription = FlowWatcherKt.watchDetailIsWishlisted(vm: vm) { [weak self] value in
            self?.isWishlisted = (value as? KotlinBoolean)?.boolValue ?? false
        }
    }

    deinit {
        subscription?.cancel()
    }

    func toggleWishlist() { vm.toggleWishlist() }
}
