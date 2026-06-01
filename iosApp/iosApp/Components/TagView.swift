import SwiftUI

struct TagView: View {
    let text: String
    var maxChars: Int = Int.max
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        let display = text.count > maxChars
            ? String(text.prefix(maxChars - 1)).trimmingCharacters(in: .whitespaces) + "…"
            : text
        Text(display)
            .font(BookifyFont.dmSans(11.5, weight: .medium))
            .foregroundColor(colors.muted)
            .lineLimit(1)
            .padding(.horizontal, 10)
            .padding(.vertical, 4)
            .background(Color.black.opacity(0.04))
            .clipShape(Capsule())
            .overlay(Capsule().stroke(Color.black.opacity(0.04), lineWidth: 0.5))
    }
}
