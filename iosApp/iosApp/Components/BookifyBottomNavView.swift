import SwiftUI
import ComposeApp

struct BookifyBottomNavView: View {
    let selectedTab: AppTab
    let wishlistCount: Int
    let onTabSelected: (AppTab) -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        let bgColor = colors.isDark
            ? Color(rgb: 0x1C1916).opacity(0.85)
            : Color.white.opacity(0.7)
        let borderColor = colors.isDark
            ? Color.white.opacity(0.08)
            : Color.black.opacity(0.06)

        VStack(spacing: 0) {
            Rectangle()
                .fill(borderColor)
                .frame(height: 0.5)

            HStack(spacing: 0) {
                NavTabItem(
                    label: "Home",
                    isActive: selectedTab.name == AppTab.home.name,
                    badge: 0,
                    icon: { active in HomeIconShape(active: active) },
                    onTap: { onTabSelected(.home) }
                )
                NavTabItem(
                    label: "Wishlist",
                    isActive: selectedTab.name == AppTab.wishlist.name,
                    badge: wishlistCount,
                    icon: { active in BookmarkIconShape(active: active) },
                    onTap: { onTabSelected(.wishlist) }
                )
            }
            .padding(.top, 8)
        }
        .background(bgColor.ignoresSafeArea(edges: .bottom))
    }
}

private struct NavTabItem<Icon: View>: View {
    let label: String
    let isActive: Bool
    let badge: Int
    let icon: (Bool) -> Icon
    let onTap: () -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Button(action: onTap) {
            VStack(spacing: 3) {
                ZStack(alignment: .topTrailing) {
                    icon(isActive)
                        .frame(width: 22, height: 22)
                        .foregroundColor(isActive ? colors.accent : colors.muted)

                    if badge > 0 {
                        Text("\(badge)")
                            .font(BookifyFont.dmSans(10, weight: .semibold))
                            .foregroundColor(colors.accentInk)
                            .padding(.horizontal, badge > 9 ? 4 : 3)
                            .padding(.vertical, 2)
                            .background(colors.accent)
                            .clipShape(Capsule())
                            .offset(x: 9, y: -3)
                    }
                }
                Text(label)
                    .font(BookifyFont.dmSans(10.5, weight: isActive ? .semibold : .medium))
                    .foregroundColor(isActive ? colors.accent : colors.muted)
            }
            .padding(.horizontal, 4)
            .padding(.vertical, 6)
            .frame(maxWidth: .infinity)
        }
        .buttonStyle(.plain)
    }
}

private struct HomeIconShape: View {
    let active: Bool
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Canvas { ctx, size in
            let w = size.width, h = size.height
            var path = Path()
            path.move(to: CGPoint(x: w * 0.136, y: h * 0.432))
            path.addLine(to: CGPoint(x: w * 0.5, y: h * 0.136))
            path.addLine(to: CGPoint(x: w * 0.864, y: h * 0.432))
            path.addLine(to: CGPoint(x: w * 0.864, y: h * 0.864))
            path.addCurve(
                to: CGPoint(x: w * 0.818, y: h * 0.909),
                control1: CGPoint(x: w * 0.864, y: h * 0.864),
                control2: CGPoint(x: w * 0.864, y: h * 0.909)
            )
            path.addLine(to: CGPoint(x: w * 0.636, y: h * 0.909))
            path.addLine(to: CGPoint(x: w * 0.636, y: h * 0.636))
            path.addLine(to: CGPoint(x: w * 0.364, y: h * 0.636))
            path.addLine(to: CGPoint(x: w * 0.364, y: h * 0.909))
            path.addLine(to: CGPoint(x: w * 0.182, y: h * 0.909))
            path.addCurve(
                to: CGPoint(x: w * 0.136, y: h * 0.864),
                control1: CGPoint(x: w * 0.136, y: h * 0.909),
                control2: CGPoint(x: w * 0.136, y: h * 0.909)
            )
            path.closeSubpath()

            let tint = active ? colors.accent : colors.muted
            if active {
                ctx.fill(path, with: .color(tint.opacity(0.12)))
            }
            ctx.stroke(path, with: .color(tint),
                       style: StrokeStyle(lineWidth: active ? 1.8 : 1.5,
                                         lineCap: .round, lineJoin: .round))
        }
    }
}

private struct BookmarkIconShape: View {
    let active: Bool
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Canvas { ctx, size in
            let w = size.width, h = size.height
            var path = Path()
            path.move(to: CGPoint(x: w * 0.227, y: h * 0.136))
            path.addLine(to: CGPoint(x: w * 0.773, y: h * 0.136))
            path.addLine(to: CGPoint(x: w * 0.773, y: h * 0.909))
            path.addLine(to: CGPoint(x: w * 0.5, y: h * 0.75))
            path.addLine(to: CGPoint(x: w * 0.227, y: h * 0.909))
            path.closeSubpath()

            let tint = active ? colors.accent : colors.muted
            if active {
                ctx.fill(path, with: .color(tint.opacity(0.12)))
            }
            ctx.stroke(path, with: .color(tint),
                       style: StrokeStyle(lineWidth: active ? 1.8 : 1.5,
                                         lineCap: .round, lineJoin: .round))
        }
    }
}
