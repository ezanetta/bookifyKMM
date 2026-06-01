import SwiftUI
import ComposeApp

struct DetailView: View {
    let book: Book
    let onBack: () -> Void
    @StateObject private var detailStore: DetailStore
    @Environment(\.bookifyColors) private var colors

    init(book: Book, onBack: @escaping () -> Void) {
        self.book = book
        self.onBack = onBack
        _detailStore = StateObject(
            wrappedValue: DetailStore(graph: AppContainer.shared.graph, bookKey: book.key)
        )
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                topBar
                coverSection
                titleBlock
                tagsSection
                wishlistButton
                Spacer().frame(height: 40)
            }
        }
        .background(colors.bg)
    }

    private var topBar: some View {
        HStack {
            Button(action: onBack) {
                HStack(spacing: 6) {
                    ChevronLeftShape()
                        .stroke(colors.ink, style: StrokeStyle(lineWidth: 1.6, lineCap: .round, lineJoin: .round))
                        .frame(width: 16, height: 16)
                    Text("Shelf")
                        .font(BookifyFont.dmSans(14, weight: .medium))
                        .foregroundColor(colors.ink)
                }
                .padding(.horizontal, 4)
                .padding(.vertical, 8)
            }
            .buttonStyle(.plain)

            Spacer()

            Button(action: { detailStore.toggleWishlist() }) {
                BookmarkIndicatorShape(filled: detailStore.isWishlisted, accentColor: colors.accent, inkColor: colors.ink)
                    .frame(width: 18, height: 18)
                    .padding(11)
            }
            .buttonStyle(.plain)
        }
        .padding(.leading, 16)
        .padding(.trailing, 8)
        .padding(.top, 8)
        .padding(.bottom, 12)
    }

    private var coverSection: some View {
        HStack {
            Spacer()
            BookCoverImage(coverUrl: book.coverUrl, title: book.title)
                .frame(width: 210, height: 315)
                .clipShape(RoundedRectangle(cornerRadius: 4))
                .rotation3DEffect(.degrees(-3), axis: (x: 0, y: 1, z: 0), anchor: .leading)
                .shadow(color: Color.black.opacity(0.16), radius: 8, x: 0, y: 4)
            Spacer()
        }
        .padding(.horizontal, 20)
        .padding(.vertical, 12)
    }

    private var titleBlock: some View {
        VStack(spacing: 8) {
            Text(book.title)
                .font(BookifyFont.newsreader(26, weight: .medium, italic: true))
                .foregroundColor(colors.ink)
                .kerning(-0.4)
                .lineSpacing(2)
                .multilineTextAlignment(.center)
            Text(book.author)
                .font(BookifyFont.dmSans(14, weight: .medium))
                .foregroundColor(colors.muted)
        }
        .padding(.horizontal, 24)
    }

    private var tagsSection: some View {
        let subjects = book.subjects as! [String]
        return FlowLayout(spacing: 6) {
            ForEach(Array(subjects.enumerated()), id: \.offset) { _, subject in
                TagView(text: subject)
            }
        }
        .frame(maxWidth: .infinity)
        .padding(.horizontal, 20)
        .padding(.top, 18)
    }

    private var wishlistButton: some View {
        Button(action: { detailStore.toggleWishlist() }) {
            HStack(spacing: 8) {
                if detailStore.isWishlisted {
                    CheckShape()
                        .stroke(colors.ink, style: StrokeStyle(lineWidth: 1.8, lineCap: .round, lineJoin: .round))
                        .frame(width: 15, height: 15)
                    Text("In your wishlist")
                        .font(BookifyFont.dmSans(15, weight: .semibold))
                        .foregroundColor(colors.ink)
                        .kerning(0.2)
                } else {
                    BookmarkOutlineButtonShape()
                        .stroke(colors.accentInk, style: StrokeStyle(lineWidth: 1.4, lineCap: .round, lineJoin: .round))
                        .frame(width: 15, height: 15)
                    Text("Add to wishlist")
                        .font(BookifyFont.dmSans(15, weight: .semibold))
                        .foregroundColor(colors.accentInk)
                        .kerning(0.2)
                }
            }
            .frame(maxWidth: .infinity)
            .frame(height: 52)
            .background(
                detailStore.isWishlisted
                    ? Color.clear
                    : colors.accent
            )
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(
                        detailStore.isWishlisted ? Color.black.opacity(0.14) : Color.clear,
                        lineWidth: 0.5
                    )
            )
            .shadow(
                color: detailStore.isWishlisted ? .clear : colors.accent.opacity(0.2),
                radius: 4
            )
        }
        .buttonStyle(.plain)
        .padding(.horizontal, 20)
        .padding(.top, 32)
    }
}

private struct ChevronLeftShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.625, y: h * 0.1875))
        path.addLine(to: CGPoint(x: w * 0.3125, y: h * 0.5))
        path.addLine(to: CGPoint(x: w * 0.625, y: h * 0.8125))
        return path
    }
}

private struct CheckShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.1875, y: h * 0.5))
        path.addLine(to: CGPoint(x: w * 0.4375, y: h * 0.71875))
        path.addLine(to: CGPoint(x: w * 0.8125, y: h * 0.28125))
        return path
    }
}

private struct BookmarkOutlineButtonShape: Shape {
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

private struct BookmarkIndicatorShape: View {
    let filled: Bool
    let accentColor: Color
    let inkColor: Color

    var body: some View {
        Canvas { ctx, size in
            let w = size.width, h = size.height
            var path = Path()
            path.move(to: CGPoint(x: w * 0.222, y: h * 0.111))
            path.addLine(to: CGPoint(x: w * 0.778, y: h * 0.111))
            path.addLine(to: CGPoint(x: w * 0.778, y: h * 0.889))
            path.addLine(to: CGPoint(x: w * 0.5, y: h * 0.722))
            path.addLine(to: CGPoint(x: w * 0.222, y: h * 0.889))
            path.closeSubpath()

            if filled {
                ctx.fill(path, with: .color(accentColor))
            }
            ctx.stroke(
                path,
                with: .color(filled ? accentColor : inkColor),
                style: StrokeStyle(lineWidth: 1.4, lineCap: .round, lineJoin: .round)
            )
        }
    }
}
