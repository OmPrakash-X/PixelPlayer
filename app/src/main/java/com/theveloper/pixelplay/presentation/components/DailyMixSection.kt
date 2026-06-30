package com.theveloper.pixelplay.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.zIndex
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.media3.common.util.UnstableApi
import com.theveloper.pixelplay.R
import com.theveloper.pixelplay.data.model.Song
import com.theveloper.pixelplay.presentation.components.resolveNavBarOccupiedHeight
import com.theveloper.pixelplay.presentation.components.subcomps.EnhancedSongListItem
import com.theveloper.pixelplay.presentation.viewmodel.PlayerViewModel
import com.theveloper.pixelplay.presentation.viewmodel.PlaylistViewModel
import com.theveloper.pixelplay.utils.shapes.RoundedStarShape
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import androidx.compose.ui.res.stringResource
import racra.compose.smooth_corner_rect_library.AbsoluteSmoothCornerShape


// 2) DailyMixSection y DailyMixCard quedan igual de ligeras...
@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun DailyMixSection(
    songs: ImmutableList<Song>,
    playerViewModel: PlayerViewModel,
    onClickOpen: () -> Unit = {},
    onNavigateToAlbum: (Song) -> Unit = {},
    onNavigateToArtist: (Song) -> Unit = {},
    onNavigateToGenre: (Song) -> Unit = {},
) {
    val playlistViewModel: PlaylistViewModel = hiltViewModel()
    val favoriteSongIds by playerViewModel.favoriteSongIds.collectAsStateWithLifecycle()
    val selectedSongForInfo by playerViewModel.selectedSongForInfo.collectAsStateWithLifecycle()
    val playlistUiState by playlistViewModel.uiState.collectAsStateWithLifecycle()
    val navBarCompactMode by playerViewModel.navBarCompactMode.collectAsStateWithLifecycle()
    val systemNavBarInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val bottomBarHeightDp = resolveNavBarOccupiedHeight(systemNavBarInset, navBarCompactMode)
    var showSongInfoSheet by remember { mutableStateOf(false) }
    var showPlaylistBottomSheet by remember { mutableStateOf(false) }
    val dailyMixQueueName = stringResource(R.string.home_daily_mix_queue_name)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        DailyMixCard(
            songs = songs,
            playerViewModel = playerViewModel,
            onClickOpen = onClickOpen,
            onMoreOptionsClick = { song ->
                playerViewModel.selectSongForInfo(song)
                showSongInfoSheet = true
            }
        )
    }

    if (showSongInfoSheet && selectedSongForInfo != null) {
        val song = selectedSongForInfo!!
        SongInfoBottomSheet(
            song = song,
            isFavorite = favoriteSongIds.contains(song.id),
            onToggleFavorite = { playerViewModel.toggleFavoriteSpecificSong(song) },
            onDismiss = {
                showSongInfoSheet = false
                showPlaylistBottomSheet = false
            },
            onPlaySong = {
                playerViewModel.showAndPlaySong(
                    song = song,
                    contextSongs = songs,
                    queueName = dailyMixQueueName,
                    isVoluntaryPlay = false
                )
            },
            onAddToQueue = {
                playerViewModel.addSongToQueue(song)
            },
            onAddNextToQueue = {
                playerViewModel.addSongNextToQueue(song)
            },
            onAddToPlayList = {
                showPlaylistBottomSheet = true
            },
            onDeleteFromDevice = playerViewModel::deleteFromDevice,
            onNavigateToAlbum = {
                onNavigateToAlbum(song)
                showSongInfoSheet = false
            },
            onNavigateToArtist = {
                onNavigateToArtist(song)
                showSongInfoSheet = false
            },
            onNavigateToGenre = {
                onNavigateToGenre(song)
                showSongInfoSheet = false
            },
            onEditSong = { newTitle, newArtist, newAlbum, newAlbumArtist, newComposer, newGenre, newLyrics, newTrackNumber, newDiscNumber, replayGainTrackGainDb, replayGainAlbumGainDb, coverArtUpdate ->
                playerViewModel.editSongMetadata(
                    song,
                    newTitle,
                    newArtist,
                    newAlbum,
                    newAlbumArtist,
                    newComposer,
                    newGenre,
                    newLyrics,
                    newTrackNumber,
                    newDiscNumber,
                    replayGainTrackGainDb,
                    replayGainAlbumGainDb,
                    coverArtUpdate
                )
            },
            removeFromListTrigger = {}
        )

        if (showPlaylistBottomSheet) {
            PlaylistBottomSheet(
                playlistUiState = playlistUiState,
                songs = listOf(song),
                onDismiss = { showPlaylistBottomSheet = false },
                bottomBarHeight = bottomBarHeightDp,
                playerViewModel = playerViewModel,
            )
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun DailyMixCard(
    songs: ImmutableList<Song>,
    onClickOpen: () -> Unit,
    playerViewModel: PlayerViewModel,
    onMoreOptionsClick: (Song) -> Unit
) {
    val headerSongs = songs.take(3).toImmutableList()
    val visibleSongs = songs.take(4).toImmutableList()
    val cornerRadius = 24.dp
    val shape = AbsoluteSmoothCornerShape(
        cornerRadiusBR = cornerRadius,
        smoothnessAsPercentTL = 60,
        cornerRadiusTR = cornerRadius,
        smoothnessAsPercentTR = 60,
        cornerRadiusBL = cornerRadius,
        smoothnessAsPercentBL = 60,
        cornerRadiusTL = cornerRadius,
        smoothnessAsPercentBR = 60
    )
    Card(
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow.copy(alpha = 0.6f)),
        elevation = CardDefaults.elevatedCardElevation(0.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), shape)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            DailyMixHeader(thumbnails = headerSongs)
            DailyMixSongList(
                songs = visibleSongs,
                playbackQueue = songs,
                playerViewModel = playerViewModel,
                onMoreOptionsClick = onMoreOptionsClick
            )
            Spacer(modifier = Modifier.height(10.dp))
            ViewAllDailyMixButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .padding(bottom = 12.dp),
                onClickOpen = {
                    onClickOpen()
                },
            )
        }
    }
}

@Composable
fun DailyMixHeader(thumbnails: ImmutableList<Song>) {
    val titleStyle = rememberDailyMixTitleStyle()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.28f),
                        MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.14f),
                        Color.Transparent
                    )
                )
            ),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Absolute.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(100.dp),
                    modifier = Modifier.padding(bottom = 6.dp)
                ) {
                    Text(
                        text = "AI CURATED MIX",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.home_daily_mix_title),
                    style = titleStyle,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    modifier = Modifier.padding(start = 1.dp),
                    text = stringResource(R.string.home_daily_mix_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Normal,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .width(96.dp)
                    .height(96.dp),
                contentAlignment = Alignment.Center
            ) {
                thumbnails.forEachIndexed { index, song ->
                    val (rotationAngle, scaleFactor, translationX) = when (index) {
                        0 -> Triple(0f, 1.0f, 0.dp) // Front center
                        1 -> Triple(12f, 0.88f, 18.dp) // Back right
                        2 -> Triple(-12f, 0.88f, (-18.dp)) // Back left
                        else -> Triple(0f, 1f, 0.dp)
                    }
                    val zIndexVal = when (index) {
                        0 -> 3f
                        1 -> 2f
                        2 -> 1f
                        else -> 0f
                    }
                    val shape = AbsoluteSmoothCornerShape(12.dp, 60)
                    Box(
                        modifier = Modifier
                            .size(58.dp)
                            .zIndex(zIndexVal)
                            .graphicsLayer {
                                rotationZ = rotationAngle
                                scaleX = scaleFactor
                                scaleY = scaleFactor
                                this.translationX = translationX.toPx()
                            }
                            .shadow(6.dp, shape)
                            .border(1.5.dp, MaterialTheme.colorScheme.surface, shape)
                            .clip(shape)
                    ) {
                        SmartImage(
                            model = song.albumArtUriString,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
private fun DailyMixSongList(
    songs: ImmutableList<Song>,
    playbackQueue: ImmutableList<Song>,
    playerViewModel: PlayerViewModel,
    onMoreOptionsClick: (Song) -> Unit
) {
    val dailyMixQueueName = stringResource(R.string.home_daily_mix_queue_name)
    val stablePlayerState by playerViewModel.stablePlayerState.collectAsStateWithLifecycle()
    val itemContainerColor = Color.Transparent // Float over card background instead

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(RoundedCornerShape(16.dp)),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        songs.forEach { song ->
            EnhancedSongListItem(
                song = song,
                isCurrentSong = stablePlayerState.currentSong?.id == song.id,
                isPlaying = stablePlayerState.isPlaying && stablePlayerState.currentSong?.id == song.id,
                containerColorOverride = itemContainerColor,
                onMoreOptionsClick = onMoreOptionsClick,
                customShape = RoundedCornerShape(12.dp),
                showAlbumArt = true,
                albumArtSize = 42.dp,
                onClick = {
                    playerViewModel.showAndPlaySong(
                        song = song,
                        contextSongs = playbackQueue,
                        queueName = dailyMixQueueName,
                        isVoluntaryPlay = false
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun ViewAllDailyMixButton(
    modifier: Modifier = Modifier,
    onClickOpen: () -> Unit
) {
    val buttonShape = AbsoluteSmoothCornerShape(14.dp, 60)
    FilledTonalButton(
        modifier = modifier
            .border(1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f), buttonShape),
        onClick = {
            onClickOpen()
        },
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.35f),
            contentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = buttonShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.home_daily_mix_action_see_all),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Icon(
                painter = painterResource(R.drawable.rounded_arrow_forward_24),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
private fun rememberDailyMixTitleStyle(): TextStyle {
    return remember {
        TextStyle(
            fontFamily = FontFamily(
                Font(
                    resId = R.font.gflex_variable,
                    variationSettings = FontVariation.Settings(
                        FontVariation.weight(630),
                        FontVariation.width(136f),
                        FontVariation.grade(40),
                        FontVariation.Setting("ROND", 100f),
                        FontVariation.Setting("XTRA", 520f),
                        FontVariation.Setting("YOPQ", 90f),
                        FontVariation.Setting("YTLC", 505f)
                    )
                )
            ),
            fontWeight = FontWeight(630),
            fontSize = 20.sp,
            lineHeight = 22.sp,
            letterSpacing = (-0.35).sp
        )
    }
}
