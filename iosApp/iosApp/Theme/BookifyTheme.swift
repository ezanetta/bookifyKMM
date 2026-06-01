import SwiftUI
import ComposeApp

struct BookifyColors {
    let bg: Color
    let surface: Color
    let ink: Color
    let muted: Color
    let accent: Color
    let accentInk: Color
    let isDark: Bool
}

extension BookifyColors {
    static let library = BookifyColors(
        bg: Color(rgb: 0xF4ECDB), surface: Color(rgb: 0xFBF6E9),
        ink: Color(rgb: 0x1F1812), muted: Color(rgb: 0x6F6051),
        accent: Color(rgb: 0x9C3E26), accentInk: Color(rgb: 0xFBF6E9), isDark: false
    )
    static let bone = BookifyColors(
        bg: Color(rgb: 0xF5F1EA), surface: Color(rgb: 0xFFFFFF),
        ink: Color(rgb: 0x1A1715), muted: Color(rgb: 0x76706A),
        accent: Color(rgb: 0x7A5A26), accentInk: Color(rgb: 0xFFFFFF), isDark: false
    )
    static let sage = BookifyColors(
        bg: Color(rgb: 0xE8E7D8), surface: Color(rgb: 0xF4F3E8),
        ink: Color(rgb: 0x1D2118), muted: Color(rgb: 0x646B58),
        accent: Color(rgb: 0x3A5A3A), accentInk: Color(rgb: 0xF4F3E8), isDark: false
    )
    static let ink = BookifyColors(
        bg: Color(rgb: 0x13110E), surface: Color(rgb: 0x1C1916),
        ink: Color(rgb: 0xF0E6CF), muted: Color(rgb: 0x9B8F7A),
        accent: Color(rgb: 0xD39A44), accentInk: Color(rgb: 0x13110E), isDark: true
    )

    static func from(_ theme: AppTheme) -> BookifyColors {
        switch theme.name {
        case "LIBRARY": return .library
        case "BONE": return .bone
        case "SAGE": return .sage
        case "INK": return .ink
        default: return .sage
        }
    }
}

private struct BookifyColorsKey: EnvironmentKey {
    static let defaultValue = BookifyColors.sage
}

extension EnvironmentValues {
    var bookifyColors: BookifyColors {
        get { self[BookifyColorsKey.self] }
        set { self[BookifyColorsKey.self] = newValue }
    }
}

extension Color {
    init(rgb: UInt32) {
        self.init(
            red: Double((rgb >> 16) & 0xFF) / 255.0,
            green: Double((rgb >> 8) & 0xFF) / 255.0,
            blue: Double(rgb & 0xFF) / 255.0
        )
    }
}

// MARK: - Fonts
enum BookifyFont {
    static func dmSans(_ size: CGFloat, weight: Font.Weight = .regular) -> Font {
        switch weight {
        case .bold:     return .custom("DMSans-Bold", size: size)
        case .semibold: return .custom("DMSans-SemiBold", size: size)
        case .medium:   return .custom("DMSans-Medium", size: size)
        default:        return .custom("DMSans-Regular", size: size)
        }
    }

    static func newsreader(_ size: CGFloat, weight: Font.Weight = .regular, italic: Bool = false) -> Font {
        switch (weight, italic) {
        case (.medium, true):  return .custom("Newsreader16pt16pt-MediumItalic", size: size)
        case (.medium, false): return .custom("Newsreader16pt16pt-Medium", size: size)
        case (_, true):        return .custom("Newsreader16pt16pt-Italic", size: size)
        default:               return .custom("Newsreader16pt16pt-Regular", size: size)
        }
    }
}
