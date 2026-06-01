import SwiftUI
import ComposeApp

struct HomeView: View {
    @EnvironmentObject private var store: BookifyStore
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        let state = store.state
        VStack(spacing: 0) {
            BookifyHeaderView(
                eyebrow: "YOUR SHELF",
                title: "Bookify",
                onSettingsClick: { store.openSettings() }
            )
            GenreSwitcherView(
                selectedGenre: state.genre,
                onSelect: { store.selectGenre($0) }
            )
            Spacer().frame(height: 14)

            contentArea(state: state)
                .frame(maxWidth: .infinity, maxHeight: .infinity)
        }
    }

    @ViewBuilder
    private func contentArea(state: BookifyUiState) -> some View {
        let books = state.currentBooks as! [Book]
        if state.loading && books.isEmpty {
            LoadingView()
        } else if let error = state.error, books.isEmpty {
            ErrorView(message: error, onRetry: { store.retryLoad() })
        } else {
            BooksGridView(books: books, onBookClick: { store.openBook($0) })
        }
    }
}

struct BooksGridView: View {
    let books: [Book]
    let onBookClick: (Book) -> Void

    private let columns = [
        GridItem(.flexible(), spacing: 16),
        GridItem(.flexible(), spacing: 16),
    ]

    var body: some View {
        ScrollView {
            LazyVGrid(columns: columns, spacing: 24) {
                ForEach(books, id: \.key) { book in
                    BookGridCard(book: book, onClick: { onBookClick(book) })
                }
            }
            .padding(.horizontal, 16)
        }
    }
}

struct LoadingView: View {
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        ProgressView()
            .tint(colors.accent)
            .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}

struct ErrorView: View {
    let message: String
    let onRetry: () -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        VStack(spacing: 16) {
            Text(message)
                .font(BookifyFont.dmSans(15))
                .foregroundColor(colors.muted)
                .multilineTextAlignment(.center)
            Button(action: onRetry) {
                Text("Retry")
                    .font(BookifyFont.dmSans(14, weight: .medium))
                    .foregroundColor(colors.accentInk)
                    .padding(.horizontal, 24)
                    .frame(height: 40)
                    .background(colors.accent)
                    .clipShape(RoundedRectangle(cornerRadius: 10))
            }
            .buttonStyle(.plain)
        }
        .padding(32)
        .frame(maxWidth: .infinity, maxHeight: .infinity)
    }
}
