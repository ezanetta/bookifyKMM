import SwiftUI

struct BookifyHeaderView: View {
    var eyebrow: String = "YOUR SHELF"
    var title: String = "Bookify"
    var onSettingsClick: () -> Void = {}
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        HStack(alignment: .bottom) {
            VStack(alignment: .leading, spacing: 2) {
                Text(eyebrow.uppercased())
                    .font(BookifyFont.dmSans(11, weight: .medium))
                    .foregroundColor(colors.muted)
                    .kerning(1.6)
                Text(title)
                    .font(BookifyFont.newsreader(34, weight: .medium, italic: true))
                    .foregroundColor(colors.ink)
                    .kerning(-0.8)
                    .lineSpacing(0)
            }
            Spacer()
            GearButton(onClick: onSettingsClick)
        }
        .padding(.horizontal, 20)
        .padding(.top, 8)
        .padding(.bottom, 16)
    }
}

private struct GearButton: View {
    let onClick: () -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Button(action: onClick) {
            GearShape()
                .stroke(colors.ink, lineWidth: 1.4)
                .frame(width: 16, height: 16)
                .padding(9)
                .background(colors.surface)
                .clipShape(Circle())
                .overlay(
                    Circle().stroke(
                        colors.isDark ? Color.white.opacity(0.08) : Color.black.opacity(0.08),
                        lineWidth: 0.5
                    )
                )
        }
        .buttonStyle(.plain)
    }
}

private struct GearShape: Shape {
    func path(in rect: CGRect) -> Path {
        let cx = rect.midX, cy = rect.midY
        let outerR = rect.width * 0.46
        let innerR = rect.width * 0.31
        let teeth = 8
        let total = teeth * 2
        var path = Path()
        for i in 0..<total {
            let angle = Double(i) / Double(total) * 2 * .pi - .pi / 2
            let radius = i % 2 == 0 ? outerR : innerR
            let x = cx + radius * cos(angle)
            let y = cy + radius * sin(angle)
            if i == 0 { path.move(to: CGPoint(x: x, y: y)) }
            else { path.addLine(to: CGPoint(x: x, y: y)) }
        }
        path.closeSubpath()
        let holeR = rect.width * 0.14
        path.addEllipse(in: CGRect(
            x: cx - holeR, y: cy - holeR,
            width: holeR * 2, height: holeR * 2
        ))
        return path
    }
}
