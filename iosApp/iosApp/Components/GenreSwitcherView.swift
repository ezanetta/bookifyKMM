import SwiftUI
import ComposeApp

private let allGenres: [Genre] = [.fantasy, .scienceFiction, .thriller, .horror]

struct GenreSwitcherView: View {
    let selectedGenre: Genre
    let onSelect: (Genre) -> Void
    @Environment(\.bookifyColors) private var colors
    @Namespace private var pillNS

    var body: some View {
        HStack(spacing: 0) {
            ForEach(allGenres, id: \.name) { genre in
                ZStack {
                    if genre.name == selectedGenre.name {
                        RoundedRectangle(cornerRadius: 999)
                            .fill(colors.accent)
                            .shadow(color: colors.accent.opacity(0.2), radius: 2, x: 0, y: 1)
                            .matchedGeometryEffect(id: "pill", in: pillNS)
                    }
                    Text(genre.displayName)
                        .font(BookifyFont.dmSans(13, weight: .medium))
                        .foregroundColor(
                            genre.name == selectedGenre.name
                                ? colors.accentInk
                                : colors.ink.opacity(0.7)
                        )
                        .lineLimit(1)
                }
                .frame(maxWidth: .infinity)
                .frame(height: 36)
                .contentShape(Rectangle())
                .onTapGesture { onSelect(genre) }
            }
        }
        .padding(4)
        .background(Color.black.opacity(0.04))
        .clipShape(Capsule())
        .overlay(Capsule().stroke(Color.black.opacity(0.05), lineWidth: 0.5))
        .padding(.horizontal, 16)
        .animation(.spring(response: 0.3, dampingFraction: 0.75), value: selectedGenre.name)
    }
}
