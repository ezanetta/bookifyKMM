import SwiftUI
import ComposeApp

struct WishlistView: View {
    @EnvironmentObject private var store: BookifyStore
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        let wishlisted = store.state.wishlistedBooks as! [Book]
        let eyebrow = wishlisted.isEmpty ? "For later" : "\(wishlisted.count) saved"

        VStack(spacing: 0) {
            BookifyHeaderView(
                eyebrow: eyebrow,
                title: "Wishlist",
                onSettingsClick: { store.openSettings() }
            )
            Spacer().frame(height: 6)

            if wishlisted.isEmpty {
                EmptyWishlistView()
            } else {
                BooksGridView(books: wishlisted, onBookClick: { store.openBook($0) })
            }
        }
    }
}

private struct EmptyWishlistView: View {
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        VStack(spacing: 18) {
            ZStack {
                Circle()
                    .fill(Color.black.opacity(0.04))
                    .frame(width: 64, height: 64)
                BookmarkOutlineShape()
                    .stroke(colors.muted, style: StrokeStyle(lineWidth: 1.5, lineCap: .round, lineJoin: .round))
                    .frame(width: 26, height: 26)
            }
            Text("Nothing saved yet")
                .font(BookifyFont.newsreader(22, weight: .medium, italic: true))
                .foregroundColor(colors.ink)
                .kerning(-0.3)
            Text("Tap the bookmark on any title to keep it here for later.")
                .font(BookifyFont.dmSans(13.5))
                .foregroundColor(colors.muted)
                .multilineTextAlignment(.center)
                .lineSpacing(6)
                .frame(maxWidth: 220)
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .padding(32)
    }
}

private struct BookmarkOutlineShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.25, y: h * 0.125))
        path.addLine(to: CGPoint(x: w * 0.75, y: h * 0.125))
        path.addLine(to: CGPoint(x: w * 0.75, y: h * 0.875))
        path.addLine(to: CGPoint(x: w * 0.5, y: h * 0.71875))
        path.addLine(to: CGPoint(x: w * 0.25, y: h * 0.875))
        path.closeSubpath()
        return path
    }
}
