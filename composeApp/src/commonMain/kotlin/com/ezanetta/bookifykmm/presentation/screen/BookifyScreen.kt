package com.ezanetta.bookifykmm.presentation.screen

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ezanetta.bookifykmm.domain.model.AppTab
import com.ezanetta.bookifykmm.domain.model.Book
import com.ezanetta.bookifykmm.domain.model.Genre
import com.ezanetta.bookifykmm.presentation.components.BookifyBottomNav
import com.ezanetta.bookifykmm.presentation.components.BookifyHeader
import com.ezanetta.bookifykmm.presentation.components.GenreSwitcher
import com.ezanetta.bookifykmm.presentation.components.GridCard
import com.ezanetta.bookifykmm.presentation.theme.DmSansFamily
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyColors
import com.ezanetta.bookifykmm.presentation.theme.LocalBookifyDensity
import com.ezanetta.bookifykmm.presentation.theme.NewsreaderFamily
import com.ezanetta.bookifykmm.presentation.theme.toColors
import com.ezanetta.bookifykmm.di.AppGraph
import com.ezanetta.bookifykmm.presentation.viewmodel.BookifyViewModel
import com.ezanetta.bookifykmm.presentation.viewmodel.SettingsViewModel

@Composable
fun BookifyScreen(graph: AppGraph) {
    val viewModel: BookifyViewModel = viewModel { graph.bookifyViewModel() }
    val settingsViewModel: SettingsViewModel = viewModel { graph.settingsViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    val selectedTheme by settingsViewModel.selectedTheme.collectAsStateWithLifecycle()
    val colors = selectedTheme.toColors()
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    val genreGridStates = remember { Genre.entries.associateWith { LazyGridState() } }
    val wishlistGridState = rememberLazyGridState()

    val navTarget: NavTarget = when {
        state.openBook != null -> NavTarget.Detail(state.openBook!!)
        state.openSettings -> NavTarget.Settings
        else -> NavTarget.Tabs
    }

    CompositionLocalProvider(LocalBookifyColors provides colors) {
    Box(modifier = Modifier.fillMaxSize().background(colors.bg)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Spacer(Modifier.height(statusBarHeight))

            AnimatedContent(
                targetState = navTarget,
                transitionSpec = {
                    if (targetState is NavTarget.Detail || targetState is NavTarget.Settings) {
                        (slideInVertically { it / 14 } + fadeIn()) togetherWith fadeOut()
                    } else {
                        fadeIn() togetherWith fadeOut()
                    }
                },
                label = "screen",
                modifier = Modifier.weight(1f),
            ) { target ->
                when (target) {
                    is NavTarget.Detail -> DetailScreen(
                        book = target.book,
                        onBack = { viewModel.closeBook() },
                        graph = graph,
                    )
                    NavTarget.Settings -> SettingsScreen(
                        selectedTheme = selectedTheme,
                        onSelectTheme = { settingsViewModel.selectTheme(it) },
                        onBack = { viewModel.closeSettings() },
                    )
                    NavTarget.Tabs -> when (state.tab) {
                        AppTab.HOME -> HomeContent(viewModel = viewModel, genreGridStates = genreGridStates)
                        AppTab.WISHLIST -> WishlistContent(viewModel = viewModel, gridState = wishlistGridState)
                    }
                }
            }

            BookifyBottomNav(
                selectedTab = state.tab,
                wishlistCount = state.wishlist.size,
                onTabSelected = { viewModel.selectTab(it) },
            )
        }
    }
    } // CompositionLocalProvider
}

private sealed class NavTarget {
    data class Detail(val book: Book) : NavTarget()
    data object Settings : NavTarget()
    data object Tabs : NavTarget()
}

@Composable
private fun HomeContent(viewModel: BookifyViewModel, genreGridStates: Map<Genre, LazyGridState>) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Column(modifier = Modifier.fillMaxSize()) {
        BookifyHeader(
            eyebrow = "YOUR SHELF",
            title = "Bookify",
            onSettingsClick = { viewModel.openSettings() },
        )
        GenreSwitcher(
            selectedGenre = state.genre,
            onGenreSelect = { viewModel.selectGenre(it) },
        )
        Spacer(Modifier.height(14.dp))

        AnimatedContent(
            targetState = state.genre to state.loading,
            transitionSpec = {
                (slideInVertically { it / 12 } + fadeIn()) togetherWith fadeOut()
            },
            label = "genre",
            modifier = Modifier.weight(1f),
        ) { (genre, loading) ->
            when {
                loading && state.currentBooks.isEmpty() -> LoadingState(modifier = Modifier.fillMaxSize())
                state.error != null && state.currentBooks.isEmpty() -> ErrorState(
                    message = state.error!!,
                    onRetry = { viewModel.retryLoad() },
                    modifier = Modifier.fillMaxSize(),
                )
                else -> BooksGrid(
                    books = state.currentBooks,
                    gridState = genreGridStates[genre] ?: LazyGridState(),
                    onBookClick = { viewModel.openBook(it) },
                )
            }
        }
    }
}

@Composable
private fun WishlistContent(viewModel: BookifyViewModel, gridState: LazyGridState) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val wishlisted = state.wishlistedBooks
    val eyebrow = if (wishlisted.isNotEmpty()) "${wishlisted.size} saved" else "For later"

    Column(modifier = Modifier.fillMaxSize()) {
        BookifyHeader(
            eyebrow = eyebrow,
            title = "Wishlist",
            onSettingsClick = { viewModel.openSettings() },
        )
        Spacer(Modifier.height(6.dp))
        if (wishlisted.isEmpty()) {
            EmptyWishlistState(modifier = Modifier.fillMaxSize())
        } else {
            BooksGrid(books = wishlisted, gridState = gridState, onBookClick = { viewModel.openBook(it) })
        }
    }
}

@Composable
private fun BooksGrid(
    books: List<Book>,
    gridState: LazyGridState,
    onBookClick: (Book) -> Unit,
) {
    val d = LocalBookifyDensity.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = gridState,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
        horizontalArrangement = Arrangement.spacedBy(d.gap),
        verticalArrangement = Arrangement.spacedBy(d.gap + 8.dp),
        modifier = Modifier.fillMaxSize(),
    ) {
        items(books, key = { it.key }) { book ->
            GridCard(book = book, onClick = { onBookClick(book) })
        }
    }
}

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    val colors = LocalBookifyColors.current
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = colors.accent, trackColor = colors.accent.copy(alpha = 0.2f))
    }
}

@Composable
private fun ErrorState(message: String, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val colors = LocalBookifyColors.current
    val dmSans = DmSansFamily
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp),
    ) {
        Text(text = message, color = colors.muted, fontSize = 15.sp, fontFamily = dmSans, textAlign = TextAlign.Center)
        Spacer(Modifier.height(16.dp))
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(40.dp)
                .background(colors.accent, RoundedCornerShape(10.dp))
                .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }, onClick = onRetry)
                .padding(horizontal = 24.dp),
        ) {
            Text(text = "Retry", color = colors.accentInk, fontSize = 14.sp, fontWeight = FontWeight.Medium, fontFamily = dmSans)
        }
    }
}

@Composable
private fun EmptyWishlistState(modifier: Modifier = Modifier) {
    val colors = LocalBookifyColors.current
    val newsreader = NewsreaderFamily
    val dmSans = DmSansFamily
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.padding(32.dp),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(64.dp).background(Color(0x0A000000), CircleShape),
        ) {
            EmptyBookmarkCanvas(tint = colors.muted)
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = "Nothing saved yet",
            color = colors.ink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium,
            fontStyle = FontStyle.Italic,
            fontFamily = newsreader,
            letterSpacing = (-0.3).sp,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = "Tap the bookmark on any title to keep it here for later.",
            color = colors.muted,
            fontSize = 13.5.sp,
            fontFamily = dmSans,
            lineHeight = (13.5 * 1.5).sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.75f),
        )
    }
}

@Composable
private fun EmptyBookmarkCanvas(tint: Color) {
    androidx.compose.foundation.Canvas(modifier = Modifier.size(26.dp)) {
        val w = size.width; val h = size.height
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(w * 0.25f, h * 0.125f)
            lineTo(w * 0.75f, h * 0.125f)
            lineTo(w * 0.75f, h * 0.875f)
            lineTo(w * 0.5f, h * 0.71875f)
            lineTo(w * 0.25f, h * 0.875f)
            close()
        }
        drawPath(
            path, color = tint,
            style = androidx.compose.ui.graphics.drawscope.Stroke(
                width = 1.5.dp.toPx(),
                cap = androidx.compose.ui.graphics.StrokeCap.Round,
                join = androidx.compose.ui.graphics.StrokeJoin.Round,
            ),
        )
    }
}
