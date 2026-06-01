import SwiftUI
import ComposeApp

private struct ThemeEntry {
    let label: String
    let themeName: String  // matches AppTheme.name (e.g. "SAGE")
    let colors: BookifyColors
    let appTheme: () -> AppTheme
}

private let allThemes: [ThemeEntry] = [
    ThemeEntry(label: "Library", themeName: "LIBRARY", colors: .library, appTheme: { AppTheme.library }),
    ThemeEntry(label: "Bone",    themeName: "BONE",    colors: .bone,    appTheme: { AppTheme.bone }),
    ThemeEntry(label: "Sage",    themeName: "SAGE",    colors: .sage,    appTheme: { AppTheme.sage }),
    ThemeEntry(label: "Ink",     themeName: "INK",     colors: .ink,     appTheme: { AppTheme.ink }),
]

struct SettingsView: View {
    let onBack: () -> Void
    @EnvironmentObject private var settingsStore: SettingsStore
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        VStack(alignment: .leading, spacing: 0) {
            backBar
            headingBlock
            Spacer().frame(height: 28)
            appearanceSection
            Spacer()
        }
        .background(colors.bg)
    }

    private var backBar: some View {
        Button(action: onBack) {
            HStack(spacing: 6) {
                ChevronShape()
                    .stroke(colors.ink, style: StrokeStyle(lineWidth: 1.4, lineCap: .round, lineJoin: .round))
                    .frame(width: 16, height: 16)
                Text("Back")
                    .font(BookifyFont.dmSans(14, weight: .medium))
                    .foregroundColor(colors.ink)
            }
            .padding(.horizontal, 4)
            .padding(.vertical, 8)
        }
        .buttonStyle(.plain)
        .padding(.leading, 16)
        .padding(.top, 8)
        .padding(.bottom, 12)
    }

    private var headingBlock: some View {
        VStack(alignment: .leading, spacing: 0) {
            Text("PREFERENCES")
                .font(BookifyFont.dmSans(11, weight: .medium))
                .foregroundColor(colors.muted)
                .kerning(1.6)
            Text("Settings")
                .font(BookifyFont.newsreader(34, weight: .medium, italic: true))
                .foregroundColor(colors.ink)
                .kerning(-0.8)
        }
        .padding(.horizontal, 20)
    }

    private var appearanceSection: some View {
        VStack(alignment: .leading, spacing: 10) {
            Text("APPEARANCE")
                .font(BookifyFont.dmSans(11, weight: .medium))
                .foregroundColor(colors.muted)
                .kerning(1.6)

            VStack(spacing: 0) {
                ForEach(Array(allThemes.enumerated()), id: \.offset) { index, entry in
                    ThemeOptionRow(
                        label: entry.label,
                        themeColors: entry.colors,
                        isSelected: settingsStore.selectedTheme.name == entry.themeName,
                        onTap: { settingsStore.selectTheme(entry.appTheme()) }
                    )
                    if index < allThemes.count - 1 {
                        Rectangle()
                            .fill(colors.isDark ? Color.white.opacity(0.06) : Color.black.opacity(0.06))
                            .frame(height: 0.5)
                            .padding(.leading, 58)
                    }
                }
            }
            .background(colors.surface)
            .clipShape(RoundedRectangle(cornerRadius: 14))
            .overlay(
                RoundedRectangle(cornerRadius: 14)
                    .stroke(
                        colors.isDark ? Color.white.opacity(0.08) : Color.black.opacity(0.04),
                        lineWidth: 0.5
                    )
            )
        }
        .padding(.horizontal, 20)
    }
}

private struct ThemeOptionRow: View {
    let label: String
    let themeColors: BookifyColors
    let isSelected: Bool
    let onTap: () -> Void
    @Environment(\.bookifyColors) private var colors

    var body: some View {
        Button(action: onTap) {
            HStack(spacing: 14) {
                ZStack {
                    Circle()
                        .fill(themeColors.bg)
                        .frame(width: 32, height: 32)
                        .overlay(Circle().stroke(
                            colors.isDark ? Color.white.opacity(0.12) : Color.black.opacity(0.1),
                            lineWidth: 0.5
                        ))
                    Circle()
                        .fill(themeColors.accent)
                        .frame(width: 18, height: 18)
                }
                Text(label)
                    .font(BookifyFont.dmSans(15, weight: .medium))
                    .foregroundColor(colors.ink)
                    .frame(maxWidth: .infinity, alignment: .leading)
                if isSelected {
                    CheckmarkShape()
                        .stroke(colors.accent, style: StrokeStyle(lineWidth: 1.6, lineCap: .round, lineJoin: .round))
                        .frame(width: 16, height: 16)
                }
            }
            .padding(.horizontal, 16)
            .padding(.vertical, 14)
            .contentShape(Rectangle())
        }
        .buttonStyle(.plain)
    }
}

private struct ChevronShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.625, y: h * 0.2))
        path.addLine(to: CGPoint(x: w * 0.25, y: h * 0.5))
        path.addLine(to: CGPoint(x: w * 0.625, y: h * 0.8))
        return path
    }
}

private struct CheckmarkShape: Shape {
    func path(in rect: CGRect) -> Path {
        let w = rect.width, h = rect.height
        var path = Path()
        path.move(to: CGPoint(x: w * 0.15, y: h * 0.5))
        path.addLine(to: CGPoint(x: w * 0.42, y: h * 0.76))
        path.addLine(to: CGPoint(x: w * 0.85, y: h * 0.26))
        return path
    }
}
