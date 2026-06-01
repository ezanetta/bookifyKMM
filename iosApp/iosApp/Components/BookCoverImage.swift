import SwiftUI

struct BookCoverImage: View {
    let coverUrl: String
    let title: String

    var body: some View {
        AsyncImage(url: URL(string: coverUrl.isEmpty ? "" : coverUrl)) { phase in
            switch phase {
            case .success(let image):
                image
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            default:
                Image("cover_placeholder")
                    .resizable()
                    .aspectRatio(contentMode: .fill)
            }
        }
        .clipped()
    }
}
