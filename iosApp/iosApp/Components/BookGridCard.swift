import SwiftUI
import ComposeApp

struct BookGridCard: View {
    let book: Book
    let onClick: () -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Button(action: onClick) {
            VStack(alignment: .leading, spacing: 0) {
                BookCoverImage(coverUrl: book.coverUrl, title: book.title)
                    .frame(maxWidth: .infinity)
                    .aspectRatio(2.0 / 3.0, contentMode: .fit)
                    .clipped()
                    .clipShape(RoundedRectangle(cornerRadius: 3))

                Spacer().frame(height: 10)

                VStack(alignment: .leading, spacing: 3) {
                    Text(book.title)
                        .font(BookifyFont.newsreader(17, weight: .medium))
                        .foregroundColor(colors.ink)
                        .lineLimit(2)
                        .fixedSize(horizontal: false, vertical: true)

                    Text(book.author)
                        .font(BookifyFont.dmSans(13))
                        .foregroundColor(colors.muted)

                    Spacer().frame(height: 5)

                    TagsRow(subjects: book.subjects as! [String], maxTags: 2)
                }
                .padding(.horizontal, 2)
            }
        }
        .buttonStyle(.plain)
    }
}

struct TagsRow: View {
    let subjects: [String]
    let maxTags: Int
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        let displayed = subjects.prefix(maxTags)
        let overflow = subjects.count - maxTags

        FlowLayout(spacing: 6) {
            ForEach(Array(displayed.enumerated()), id: \.offset) { _, subject in
                TagView(text: subject, maxChars: 16)
            }
            if overflow > 0 {
                Text("+\(overflow)")
                    .font(BookifyFont.dmSans(11.5, weight: .medium))
                    .foregroundColor(colors.muted.opacity(0.7))
            }
        }
    }
}

// Simple left-to-right wrapping layout
struct FlowLayout: Layout {
    var spacing: CGFloat = 6

    func sizeThatFits(proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) -> CGSize {
        let maxWidth = proposal.width ?? .infinity
        var x: CGFloat = 0
        var y: CGFloat = 0
        var rowHeight: CGFloat = 0
        var totalHeight: CGFloat = 0

        for view in subviews {
            let size = view.sizeThatFits(.unspecified)
            if x + size.width > maxWidth && x > 0 {
                y += rowHeight + spacing
                totalHeight = y
                x = 0
                rowHeight = 0
            }
            rowHeight = max(rowHeight, size.height)
            x += size.width + spacing
        }
        totalHeight += rowHeight
        return CGSize(width: maxWidth, height: totalHeight)
    }

    func placeSubviews(in bounds: CGRect, proposal: ProposedViewSize, subviews: Subviews, cache: inout ()) {
        let maxWidth = bounds.width
        var x: CGFloat = bounds.minX
        var y: CGFloat = bounds.minY
        var rowHeight: CGFloat = 0

        for view in subviews {
            let size = view.sizeThatFits(.unspecified)
            if x + size.width > bounds.maxX && x > bounds.minX {
                y += rowHeight + spacing
                x = bounds.minX
                rowHeight = 0
            }
            view.place(at: CGPoint(x: x, y: y), proposal: .unspecified)
            rowHeight = max(rowHeight, size.height)
            x += size.width + spacing
        }
    }
}
