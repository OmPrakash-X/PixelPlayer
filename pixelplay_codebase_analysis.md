# PixelPlay Codebase & Architecture Analysis

Welcome! This document provides a file-by-file analysis of the **PixelPlay** repository. PixelPlay is a feature-rich, high-performance offline and cloud-streaming music player for Android and Wear OS, built using Jetpack Compose, ExoPlayer/Media3, Room, DataStore, and Dagger/Hilt.

---

## 1. High-Level Architecture Overview

PixelPlay follows clean architecture principles structured into modules and layers:

- **UI & Presentation Layer**: Uses Jetpack Compose. State is managed by Jetpack ViewModels and specialized state holders. Theme overrides dynamic Material 3 color palettes based on album art colors.
- **Service & Playback Layer**: Powered by Media3 `MediaSession` and `MediaController`. Background playback is coordinated by `MusicService.kt`, using a custom `DualPlayerEngine.kt` to handle crossfading, audio offload, and high-res audio processing.
- **Data & Synchronization Layer**: Local database is implemented via Room (`PixelPlayDatabase.kt`), while key-value settings are persisted using DataStore repositories. Background synchronizations are handled by WorkManager.
- **Streaming Provider Integration**: Supports syncing files and folders from Google Drive, Jellyfin, Navidrome, Netease, QQ Music, and Telegram.

---

## 2. Multi-Module Project Structure

The project is split into four Gradle modules:
1. **`:shared`**: Houses data models, command structures, payloads, and capabilities shared between the Phone app (`:app`) and Wear OS app (`:wear`).
2. **`:app`**: The primary Android application containing the phone and tablet UI, playback services, and streaming repositories.
3. **`:wear`**: The Wear OS application providing a wrist-friendly interface for remote controls, volume changes, and file transfers.
4. **`:baselineprofile`**: Contains baseline profile configurations to optimize startup times and frame rendering speeds.

---

## 3. Shared Module (`:shared`)

Located in `shared/src/main/java/com/theveloper/pixelplay/shared`:

- [WearBrowseRequest.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearBrowseRequest.kt): Represents requests from the watch to browse the phone's music library.
- [WearBrowseResponse.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearBrowseResponse.kt): Contains results matching browse requests.
- [WearCapabilities.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearCapabilities.kt): Simple constants identifying wear/phone features.
- [WearDataPaths.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearDataPaths.kt): String constants specifying Wearable Data Layer API paths for messages and sync nodes.
- [WearFavoriteSyncPayload.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearFavoriteSyncPayload.kt): Data structure to sync favorited status between wear and phone.
- [WearIntents.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearIntents.kt): Intent actions shared between wear and mobile platforms.
- [WearLibraryItem.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearLibraryItem.kt): Represents a music track/album/playlist in wear-compatible model mapping.
- [WearLibraryState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearLibraryState.kt): Emits status of the phone library to the watch.
- [WearLyrics.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearLyrics.kt): Models lyrics display segments for the watch screen.
- [WearPlaybackCommand.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearPlaybackCommand.kt): Commands sent from wear to trigger pause, resume, skip, seek, shuffle, or repeat.
- [WearPlaybackResult.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearPlaybackResult.kt): Success/failure wrapper for playback remote command executions.
- [WearPlayerState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearPlayerState.kt): Holds active track details (title, artist, duration, progress, state) for the watch UI.
- [WearThemePalette.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearThemePalette.kt): Bundles colors dynamically extracted from album art on mobile and synced to theme the watch UI.
- [WearTransferMetadata.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearTransferMetadata.kt): File transfer packet details (size, name, hash).
- [WearTransferProgress.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearTransferProgress.kt): Reports status updates during phone-to-watch music file uploads.
- [WearTransferRequest.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearTransferRequest.kt): Triggers phone-to-watch downloads.
- [WearVolumeCommand.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearVolumeCommand.kt): Models wear requests to increment, decrement, or set absolute playback volume.
- [WearVolumeState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/shared/src/main/java/com/theveloper/pixelplay/shared/WearVolumeState.kt): Syncs phone volume states to watch controls.

---

## 4. Main Application Module (`:app`)

Located in `app/src/main/java/com/theveloper/pixelplay`:

### 4.1. Root Module Files
- [MainActivity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/MainActivity.kt): The primary entry point. Coordinates full screen transitions, edges, immersive status/nav bars, splash screens, setup gates, and shortcut intents.
- [ExternalPlayerActivity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ExternalPlayerActivity.kt): Lightweight controller invoked when playing individual files from external apps (e.g. file manager share intents).
- [MainActivityIntentContract.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/MainActivityIntentContract.kt): Intent contracts mapping incoming shortcut and tile actions.
- [PixelPlayApplication.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/PixelPlayApplication.kt): Initializer for Hilt, Timber logging, crash handlers, dynamic theme engines, and notification channels.
- [ReleaseTree.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ReleaseTree.kt): Custom Timber logger tree filtering out debug entries in production releases.

---

### 4.2. UI Themes (`ui/theme`)
Controls visual styles, palettes, and tokens:
- [Color.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Color.kt): Defines the static theme hex color codes (e.g., PixelPlayPurpleDark, Pink, Orange).
- [ColorRoles.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/ColorRoles.kt): Maps extracted album art colors into standard semantic material color roles (Primary, Container, Surface, etc.).
- [GenreColors.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/GenreColors.kt): Pre-configured gradients matching common musical genres.
- [Shape.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Shape.kt): Material 3 standard rounded shapes mapping.
- [ShapeCache.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/ShapeCache.kt): Cached smooth corner polygons to save draw cycles.
- [Theme.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Theme.kt): Combines colors, shapes, and typography. Implements dynamic system palettes and injects them via `CompositionLocalProvider`.
- [Type.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Type.kt): Typography configuration using `GoogleSansRounded` and `Montserrat` Google fonts.

---

### 4.3. Navigation (`presentation/navigation`)
Responsible for routing, screen bounds, and page transitions:
- [AppNavigation.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/navigation/AppNavigation.kt): Maps `Screen` routes to Compose screens. Handles custom entry/exit anims.
- [MainRootRoutes.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/navigation/MainRootRoutes.kt): Quick mapping checks for core tabs (Home, Search, Library).
- [NavControllerExtensions.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/navigation/NavControllerExtensions.kt): Safe wrapper utilities preventing double-navigation actions.
- [Screen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/navigation/Screen.kt): Sealed class representing all application screens and route builder structures.
- [Transitions.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/navigation/Transitions.kt): Implements transition animations (shared-axis, slide and fade, predictive back collapse).

---

### 4.4. UI Screens (`presentation/screens`)
These construct individual screen interfaces:
- [AboutScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/AboutScreen.kt): Displays app info, license, easter egg hooks, developer credits.
- [AccountsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/AccountsScreen.kt): Configuration portal for cloud and network integrations (Navidrome, QQMusic, Jellyfin, Netease).
- [AiUsageComponents.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/AiUsageComponents.kt): Displays statistics and summaries for Gemini-based smart playlists.
- [AlbumDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/AlbumDetailScreen.kt): Shows list of album tracks, dynamic headers matching artwork, and multi-select.
- [ArtistDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/ArtistDetailScreen.kt): Rich profile screens with headers, bio descriptions, top tracks, and discography.
- [ArtistSettingsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/ArtistSettingsScreen.kt): Settings relating to artist bio fetching and fallback parsing properties.
- [CreatePlaylistScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/CreatePlaylistScreen.kt): Rich panel to construct custom collections with manual track ordering and filter items.
- [DailyMixScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/DailyMixScreen.kt): Personalized generated mix screens containing daily auto-refreshed queues.
- [DelimiterConfigScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/DelimiterConfigScreen.kt): Setup configurations for artists name splitters (e.g. `feat.`, `&`, `/`).
- [DeviceCapabilitiesScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/DeviceCapabilitiesScreen.kt): Displays hardware capabilities (supported sample rates, decoders, offload support) and diagnostic log exports.
- [EasterEggScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/EasterEggScreen.kt): A fun retro game / visualization hidden within the about screen.
- [EditTransitionScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/EditTransitionScreen.kt): Visual interface to configure transition types (crossfade length, cut, fade in/out) per track pairs.
- [EqualizerScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/EqualizerScreen.kt): Core EQ screen with visual preset sliders, preamp adjustments, and custom profile configurations.
- [ExperimentalSettingsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/ExperimentalSettingsScreen.kt): Config toggle interface for bleeding-edge audio offloads, parallel indexing, and hardware decoders.
- [FolderExplorerScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/FolderExplorerScreen.kt): Hierarchical view of folders matching local storage structure.
- [GenreDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/GenreDetailScreen.kt): Displays tracks classified under a specific genre.
- [HomeScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/HomeScreen.kt): Main dashboard displaying recent additions, daily mixes, smart cues, and floating sidebar launchers.
- [LibraryEmptyState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibraryEmptyState.kt): Styled UI shown when no music files are indexed.
- [LibraryMediaTabs.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibraryMediaTabs.kt): Renders library main categories (Albums, Artists, Playlists, Genres).
- [LibraryPlaybackAwareSongItem.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibraryPlaybackAwareSongItem.kt): Styled song rows changing color/indicators when currently playing.
- [LibraryScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibraryScreen.kt): The core center showing index tabs, favorites, and storage quick filters.
- [LibrarySongsAndFavoritesTabs.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibrarySongsAndFavoritesTabs.kt): Secondary tabs layout for quick switching inside the Library.
- [LibrarySongsTab.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibrarySongsTab.kt): Display configurations for items list inside the Songs tab.
- [MashupScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/MashupScreen.kt): Custom experimental DJ room to cross-mix tracks.
- [NavBarCornerRadiusScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/NavBarCornerRadiusScreen.kt): Setting layout to adjust navigation bar rounded bounds.
- [PaletteStyleSettingsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/PaletteStyleSettingsScreen.kt): Settings UI to adjust dynamic palette modes (e.g. Muted, Vibrant, Expressive).
- [PlaylistDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/PlaylistDetailScreen.kt): Details layout for local playlists (artwork collage headers, custom descriptions, and drag reorder).
- [QuickFillScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/QuickFillScreen.kt): Admin quick builder to automatically populate playlist slots based on tags.
- [RecentlyPlayedScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/RecentlyPlayedScreen.kt): Chronological grid listing recently listened songs.
- [SearchScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/SearchScreen.kt): Floating active search header, search history tag chips, and instant results mapping.
- [SettingsCategoryScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/SettingsCategoryScreen.kt): Grouped view mapping setting subsets (Audio, Customization, Storage, Advanced).
- [SettingsComponents.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/SettingsComponents.kt): Common shared layouts for settings (Switch, Slider, Card, Selector items).
- [SettingsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/SettingsScreen.kt): Root Settings menu layout.
- [SetupScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/SetupScreen.kt): Onboarding wizard that runs on first launch to request storage permissions and network credentials.
- [StatsScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/StatsScreen.kt): Detailed graphs, top tracks/artists, listening habits, and dynamic time charts.
- [TabAnimation.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/TabAnimation.kt): Custom transition math for slide-indicators in navigation tabs.
- [WordDelimiterConfigScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/WordDelimiterConfigScreen.kt): Secondary delimiters config screen targeting word exclusion lists in artist tag scans.

---

### 4.5. UI Components (`presentation/components`)
Reusable visual widgets, cards, dialogs, and sheets:
- [AiPlaylistSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AiPlaylistSheet.kt): Bottom sheet containing options to trigger the Gemini playlist generator.
- [AlbumArtCollage.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AlbumArtCollage.kt): Grid layout assembling four thumbnail artworks into one widget.
- [AlbumCarouselSelection.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AlbumCarouselSelection.kt): Rounded horizontal slider widget for albums.
- [AlbumMultiSelectionOptionSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AlbumMultiSelectionOptionSheet.kt): Batch action bottom sheet for albums.
- [AllFilesAccessDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AllFilesAccessDialog.kt): Dialog prompt explaining why Android 11+ broad storage permissions are needed.
- [AppRebrandDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AppRebrandDialog.kt): Pop-up informing the user about transitions from legacy name schemes.
- [AppSidebarDrawer.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AppSidebarDrawer.kt): Implements the sliding sidebar navigation drawer.
- [BackupModuleSelectionDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/BackupModuleSelectionDialog.kt): Pop-up to select which preferences/databases are written into backups.
- [Beta05CleanInstallDisclaimerDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/Beta05CleanInstallDisclaimerDialog.kt): Informative dialog for beta testers.
- [BetaInfoBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/BetaInfoBottomSheet.kt): Secondary panel displaying test channel parameters.
- [CastBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/CastBottomSheet.kt): High-fidelity widget containing device lists, volume sliders, connection status, and transfer buttons for Chromecast.
- [ChangelogBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ChangelogBottomSheet.kt): Bottom sheet displaying the latest feature updates.
- [CollagePatterns.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/CollagePatterns.kt): Layout algorithms detailing how to arrange image grids inside playlist covers.
- [CollapsibleCommonTopBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/CollapsibleCommonTopBar.kt): Customized App Bar that shrinks on scrolling down details screens.
- [CrashReportDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/CrashReportDialog.kt): Dialog displayed on launch if the application crashed during the previous session, displaying crash diagnostics.
- [CustomPresetsSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/CustomPresetsSheet.kt): Bottom sheet managing EQ presets.
- [DailyMixMenu.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/DailyMixMenu.kt): Pop-up options targeting generated collections.
- [DailyMixSection.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/DailyMixSection.kt): Horizontal list panel displaying user mixes on the home dashboard.
- [DismissUndoBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/DismissUndoBar.kt): A floating snackbar enabling rapid cancellations of song deletions.
- [EditMultipleSongsSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/EditMultipleSongsSheet.kt): Dialog sheet for batch tag editing.
- [EditSongSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/EditSongSheet.kt): Pop-up tag editor (title, artist, album, genre, track number).
- [ExpressiveOfflineState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ExpressiveOfflineState.kt): UI widgets indicating network status.
- [ExpressiveScrollBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ExpressiveScrollBar.kt): Custom scrollbar component showing scroll indicators and active floating headers.
- [ExpressiveScrollBarLabelResolvers.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ExpressiveScrollBarLabelResolvers.kt): Computes scrollbar popup character headers (e.g. alphabet indexes A-Z).
- [ExpressiveScrollBarMetrics.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ExpressiveScrollBarMetrics.kt): Measures drag positions to align scroll offsets.
- [ExpressiveTopBarContent.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ExpressiveTopBarContent.kt): Sub-layout inside custom page headers.
- [FileExplorerBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/FileExplorerBottomSheet.kt): Bottom sheet displaying folders and files.
- [GenreMultiSelectionOptionSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/GenreMultiSelectionOptionSheet.kt): Batch action bottom sheet for genres.
- [GenreSortBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/GenreSortBottomSheet.kt): Sorting options for the genres tab.
- [GradientTopBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/GradientTopBar.kt): A premium translucent toolbar fading into bottom views.
- [HomeOptionsBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/HomeOptionsBottomSheet.kt): Context options for the home dashboard.
- [ImageCropView.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ImageCropView.kt): UI component enabling square crops of custom local images for playlist covers.
- [LibrarySortBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/LibrarySortBottomSheet.kt): Selection panel to sort library lists (A-Z, Date, Track count, Size).
- [LyricsFloatingToolbar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/LyricsFloatingToolbar.kt): Floating quick-controls showing search and editing tools when reading lyrics.
- [LyricsSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/LyricsSheet.kt): Full screen dynamic karaoke-style scrolling lyrics layer with synced timing.
- [LyricsSyncControls.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/LyricsSyncControls.kt): Controls to adjust lyrics sync offset timings.
- [MarqueeText.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/MarqueeText.kt): Horizontal scrolling text component for titles that exceed space limits.
- [MultiSelectionBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/MultiSelectionBottomSheet.kt): Batch tools sheet displayed when multi-selecting tracks.
- [NoInternetComponents.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/NoInternetComponents.kt): Offline error placeholders.
- [OptimizedAlbumArt.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/OptimizedAlbumArt.kt): Coil-backed image loading box managing caching and color role extractions.
- [PermissionIconCollage.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PermissionIconCollage.kt): Decorative widget used on the setup permissions screen.
- [PlayStoreAnnouncementDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlayStoreAnnouncementDialog.kt): Update announcement modal.
- [PlayerInternalNavigationBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlayerInternalNavigationBar.kt): Custom bottom navigation bar hosting Home, Search, and Library.
- [PlaylistArtCollage.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistArtCollage.kt): Secondary artwork collage variant for playlists.
- [PlaylistBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistBottomSheet.kt): Bottom sheet displaying the user's playlists to quickly add a song.
- [PlaylistContainer.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistContainer.kt): Grid cell and list item container styles for playlists.
- [PlaylistCover.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistCover.kt): Main container rendering collage grids or custom images for playlists.
- [PlaylistCreationDialogs.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistCreationDialogs.kt): Form modals handling playlist name input and cover image picking.
- [PlaylistMultiSelectionBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlaylistMultiSelectionBottomSheet.kt): Batch options for playlists list.
- [QueueBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/QueueBottomSheet.kt): Core interactive bottom sheet for the active playing queue. Features drag-to-reorder, swipe-to-delete, and quick-add shortcuts.
- [RecentlyPlayedRangeSelector.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/RecentlyPlayedRangeSelector.kt): Filter chips setting time parameters on recently listened logs (e.g. today, week, month).
- [RecentlyPlayedSection.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/RecentlyPlayedSection.kt): Horizontal list wrapper rendering recently played tracks.
- [ReorderPresetsSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ReorderPresetsSheet.kt): Panel sorting equalizer presets ordering.
- [ReorderTabsSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ReorderTabsSheet.kt): Sorting panel adjusting main library categories order.
- [RoundedParallaxCarousell.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/RoundedParallaxCarousell.kt): A premium parallax scrolling card component showcasing featured albums.
- [SavePresetDialog.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/SavePresetDialog.kt): Form to input custom EQ naming profiles.
- [ScreenWrapper.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ScreenWrapper.kt): Scaffold wrapper encapsulating common UI elements, padding constraints, and transitions.
- [ShimmerBox.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ShimmerBox.kt): Renders skeleton loading effects.
- [SmartImage.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/SmartImage.kt): Utility component wrapper matching custom glide/coil configurations.
- [SongInfoBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/SongInfoBottomSheet.kt): Bottom sheet displaying details for a song (format, bit rate, sample rate, file size) along with options to edit tags, view lyrics, or set sleep timers.
- [SongPickerBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/SongPickerBottomSheet.kt): Selection sheet displaying tracks list to quickly populate a playlist.
- [StatsOverviewCard.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/StatsOverviewCard.kt): Summary dashboard card showing key listening aggregates on the stats screen.
- [StreamingProviderSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/StreamingProviderSheet.kt): Setup sheet handling login inputs for cloud providers.
- [SyncProgressBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/SyncProgressBar.kt): Detailed status progress overlay displayed during sync cycles.
- [TimerOptionsBottomSheet.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/TimerOptionsBottomSheet.kt): Form setting timer limits (e.g. 15m, 30m, end of track) to auto-pause the player.
- [ToggleSegmentButton.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/ToggleSegmentButton.kt): Horizontal segment control button.
- [UnifiedPlayerOverlaysLayer.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerOverlaysLayer.kt): Combines overlay sheets (Queue, Lyrics, Info) on top of the main player sheet.
- [UnifiedPlayerSheetLayers.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerSheetLayers.kt): Visual layer coordinates inside the full player (e.g. background blur, art card bounds).
- [UnifiedPlayerSheetShared.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerSheetShared.kt): Transition calculations, anchors, and offsets for sheet dragging.
- [UnifiedPlayerSheetV2.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerSheetV2.kt): The primary full-screen music player sheet. Implements glassmorphic overlays, cover art parallax scaling, progress sliders, and control buttons.
- [WavyArcSlider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/WavyArcSlider.kt): A premium wave-shaped volume arc control slider.
- [WavyMusicSlider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/WavyMusicSlider.kt): Animated waveform seeker that tracks song playback progress.
- [WavySliderExpressive.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/WavySliderExpressive.kt): Secondary waveform slider component variant.

Under subdirectories of `presentation/components`:
- `brickbreaker`: Contains the Easter Egg game logic.
- `external`: Wrapper layouts wrapping native system views.
- `player`: Subcomponents of the player layout (Play, Skip, Shuffle controls).
- `scoped`: Theme wrappers isolating localized color roles.
- `snapping`: Snap scroll helper models.
- `subcomps`: Layout parts (Artwork title cards, Volume indicators).

---

### 4.6. ViewModels (`presentation/viewmodel`)
Manage UI state and dispatch UI actions to repositories:
- [AccountsViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/AccountsViewModel.kt): Connects network credential inputs to streaming provider databases.
- [AiStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/AiStateHolder.kt): Maintains UI states for smart summaries and smart-playlist builder inputs.
- [AlbumDetailViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/AlbumDetailViewModel.kt): Fetches tracks matching selected album and processes headers layout colors.
- [ArtistDetailViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ArtistDetailViewModel.kt): Coordinates fetching bio descriptions, profile banners, and discography.
- [ArtistSettingsViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ArtistSettingsViewModel.kt): Manages setting inputs for artist formatting options.
- [CastRouteStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/CastRouteStateHolder.kt): Discovers Chromecast receiver nodes.
- [CastStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/CastStateHolder.kt): Holds connection parameters and media controls during casting.
- [CastTransferStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/CastTransferStateHolder.kt): Coordinates casting logic.
- [ColorSchemePair.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ColorSchemePair.kt): Simple pair storing light/dark Material ColorSchemes.
- [ColorSchemeProcessor.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ColorSchemeProcessor.kt): Extracts colors from album art bitmap streams and builds dynamic color roles.
- [ConnectivityStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ConnectivityStateHolder.kt): Publishes real-time network states.
- [DailyMixStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/DailyMixStateHolder.kt): UI states for daily mix screens.
- [DeviceCapabilitiesViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/DeviceCapabilitiesViewModel.kt): Collects diagnostic metrics and builds the shareable performance report payload.
- [EqualizerViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/EqualizerViewModel.kt): Handles preamp levels, EQ slider changes, and custom presets.
- [ExternalMediaStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ExternalMediaStateHolder.kt): Integrates external playback requests.
- [FileExplorerStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/FileExplorerStateHolder.kt): Maintains directory navigation history states.
- [FolderNavigationStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/FolderNavigationStateHolder.kt): State targets for folder-based browsing.
- [GenreDetailViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/GenreDetailViewModel.kt): Emits sorted song lists for genres.
- [LibraryStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/LibraryStateHolder.kt): Coordinates search query filtering, sorting configurations, and selection bounds in the main library.
- [LibraryTabsStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/LibraryTabsStateHolder.kt): Custom order state for library category tabs.
- [LibraryViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/LibraryViewModel.kt): Collects library database lists.
- [ListeningStatsTracker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ListeningStatsTracker.kt): Dispatches play metrics (listening counts, durations) to the DB.
- [LyricsSearchUiState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/LyricsSearchUiState.kt): UI states for web lyrics search.
- [LyricsStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/LyricsStateHolder.kt): Manages lyric scrolls, text sync adjustments, and custom manual lyric edits.
- [MainViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/MainViewModel.kt): Root state controller (verifies setup completion, monitors synchronization tasks, triggers initial scans).
- [MashupViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/MashupViewModel.kt): DJ mixing room state tracking.
- [MediaControllerSyncStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/MediaControllerSyncStateHolder.kt): Syncs active media playback controller flows with compose UI states.
- [MetadataEditStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/MetadataEditStateHolder.kt): Tracks edit form fields (title, artist, album art) during tag edits.
- [MultiSelectionStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/MultiSelectionStateHolder.kt): Holds selected track listings during batch actions.
- [PlaybackDispatchStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlaybackDispatchStateHolder.kt): Standardizes click events (play, next, prev, repeat modes) and routes commands to MediaController.
- [PlaybackStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlaybackStateHolder.kt): Monitors active media formats (bitrate, sample rate, decoder types, offload states).
- [PlayerSheetState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlayerSheetState.kt): Collapsed / Expanded state wrapper for the bottom player sheet.
- [PlayerUiState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlayerUiState.kt): Combines active tracks, queue positions, playing status, and playback configurations.
- [PlayerViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlayerViewModel.kt): The primary player viewmodel. Orchestrates playbacks, binds/unbinds the background MusicService, synchronizes volume parameters, and updates widget layers.
- [PlaylistDismissUndoStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlaylistDismissUndoStateHolder.kt): Undo state manager for deleted playlists.
- [PlaylistSelectionStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlaylistSelectionStateHolder.kt): Track picking state holder.
- [PlaylistViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/PlaylistViewModel.kt): Handles playlist creation, tracks addition, reordering, and collage cover generations.
- [QueueStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/QueueStateHolder.kt): Manages playing queue.
- [QueueUndoStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/QueueUndoStateHolder.kt): Undo state manager for tracks removed from the playing queue.
- [SearchStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SearchStateHolder.kt): Renders matching result lists and search suggestions dynamically.
- [SettingsViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SettingsViewModel.kt): Connects DataStore preference flows to setting screen states.
- [SetupViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SetupViewModel.kt): Coordinates onboarding wizard states.
- [SleepTimerStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SleepTimerStateHolder.kt): Controls sleep timer intervals and triggers pause commands.
- [SongInfoBottomSheetViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SongInfoBottomSheetViewModel.kt): Loads detailed format, directory, and tag parameters.
- [SongRemovalStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/SongRemovalStateHolder.kt): Coordinates single/batch file deletions from the device storage.
- [StablePlayerState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/StablePlayerState.kt): Unifies playback variables to minimize recomposition count.
- [StatsViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/StatsViewModel.kt): Emits graph and statistics data models.
- [ThemeStateHolder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/ThemeStateHolder.kt): Exposes active ColorSchemes and provides theme settings updates.
- [TransitionViewModel.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/viewmodel/TransitionViewModel.kt): Manages crossfade rules between specific songs.

---

### 4.7. Data Infrastructure (`data`)
Interfaces with Room databases, DataStore preferences, diagnostics trackers, files repositories, and playback services:

#### 4.7.1. Database (`data/database`)
- [PixelPlayDatabase.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/PixelPlayDatabase.kt): Root RoomDatabase. Defines tables, schemas, migrations, and hosts all DAOs.
- **DAOs**: 
  - [MusicDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/MusicDao.kt): The primary DAO. Contains queries to read/write songs, albums, artists, genres, stats, search suggestions, and folder hierarchies.
  - [AiCacheDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AiCacheDao.kt), [AiUsageDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AiUsageDao.kt): Cache handlers for Gemini summaries.
  - [AlbumArtThemeDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AlbumArtThemeDao.kt): Handles color parameters cached from extracted artworks.
  - [EngagementDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/EngagementDao.kt): Logs song listening counts, skipped tracks, and timestamps.
  - [FavoritesDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/FavoritesDao.kt): Quick reads and updates on song favorited markers.
  - [GDriveDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/GDriveDao.kt): Manages Google Drive directory configurations.
  - [JellyfinDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/JellyfinDao.kt), [NavidromeDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NavidromeDao.kt), [NeteaseDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NeteaseDao.kt), [QqMusicDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/QqMusicDao.kt), [TelegramDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TelegramDao.kt): Custom tables caching items from streaming providers.
  - [LocalPlaylistDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/LocalPlaylistDao.kt): Manages playlist names, song indexes, and collage configurations.
  - [LyricsDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/LyricsDao.kt): Manages cached lyric segments.
  - [SearchHistoryDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SearchHistoryDao.kt): Tracks user search history.
  - [TransitionDao.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TransitionDao.kt): Manages crossfade and transition settings.
- **Entities**:
  - [SongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SongEntity.kt): Core song record database table columns.
  - [AlbumEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AlbumEntity.kt), [ArtistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/ArtistEntity.kt), [PlaylistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/PlaylistEntity.kt): Table columns for albums, artists, and playlists.
  - Others: [AiCacheEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AiCacheEntity.kt), [AiUsageEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AiUsageEntity.kt), [AlbumArtThemeEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/AlbumArtThemeEntity.kt), [FavoritesEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/FavoritesEntity.kt), [GDriveFolderEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/GDriveFolderEntity.kt), [GDriveSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/GDriveSongEntity.kt), [JellyfinPlaylistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/JellyfinPlaylistEntity.kt), [JellyfinSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/JellyfinSongEntity.kt), [NavidromePlaylistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NavidromePlaylistEntity.kt), [NavidromeSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NavidromeSongEntity.kt), [NeteasePlaylistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NeteasePlaylistEntity.kt), [NeteaseSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/NeteaseSongEntity.kt), [PlaylistSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/PlaylistSongEntity.kt), [PlaylistWithSongsEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/PlaylistWithSongsEntity.kt), [QqMusicPlaylistEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/QqMusicPlaylistEntity.kt), [QqMusicSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/QqMusicSongEntity.kt), [SearchHistoryEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SearchHistoryEntity.kt), [SongArtistCrossRef.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SongArtistCrossRef.kt), [SongEngagementEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SongEngagementEntity.kt), [SongSearchFtsEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/SongSearchFtsEntity.kt), [TelegramChannelEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TelegramChannelEntity.kt), [TelegramSongEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TelegramSongEntity.kt), [TelegramTopicEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TelegramTopicEntity.kt), [TransitionRuleEntity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/database/TransitionRuleEntity.kt).

#### 4.7.2. Preferences (`data/preferences`)
- [UserPreferencesRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/UserPreferencesRepository.kt): Large repository managing general options (UI configs, active filters, backup indexes, directory exceptions, language settings).
- [ThemePreferencesRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/ThemePreferencesRepository.kt): Coordinates theme parameters (Light, Dark, Dynamic System Colors, custom palette overrides).
- [EqualizerPreferencesRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/EqualizerPreferencesRepository.kt): Saves and restores EQ slider profiles and preamp configurations.
- [PlaylistPreferencesRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/PlaylistPreferencesRepository.kt): Stores custom order profiles for playlists and tab selections.
- [AiPreferencesRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/AiPreferencesRepository.kt): Manages Gemini configurations.
- **Preference Enums**: Define layout configurations:
  - [AlbumArtColorAccuracy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/AlbumArtColorAccuracy.kt)
  - [AlbumArtPaletteStyle.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/AlbumArtPaletteStyle.kt)
  - [AppLanguage.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/AppLanguage.kt)
  - [CarouselStyle.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/CarouselStyle.kt)
  - [CollagePattern.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/CollagePattern.kt)
  - [EqualizerViewMode.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/EqualizerViewMode.kt)
  - [FullPlayerLoadingTweaks.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/FullPlayerLoadingTweaks.kt)
  - [LaunchTab.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/LaunchTab.kt)
  - [LibraryNavigationMode.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/LibraryNavigationMode.kt)
  - [NavBarStyle.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/NavBarStyle.kt)
  - [PreferenceBackupEntry.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/PreferenceBackupEntry.kt)
  - [TelegramTopicDisplayMode.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/preferences/TelegramTopicDisplayMode.kt)

#### 4.7.3. Repositories (`data/repository`)
These implement the repository interfaces to retrieve data:
- [SongRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/SongRepository.kt), [MediaStoreSongRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/MediaStoreSongRepository.kt): Interfaces with Android ContentResolver to read local audio files.
- [MusicRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/MusicRepository.kt), [MusicRepositoryImpl.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/MusicRepositoryImpl.kt): Primary data coordinator. Manages interactions between Room DB tables, updates statistics, searches tracks, and handles tag editing writes.
- [ArtistImageRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/ArtistImageRepository.kt): Fetches bio descriptions and banners from remote databases (e.g. MusicBrainz/Wikipedia).
- [FolderTreeBuilder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/FolderTreeBuilder.kt): Builds nested virtual hierarchies of folders matching file storage structures.
- [LyricsRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/LyricsRepository.kt), [LyricsRepositoryImpl.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/LyricsRepositoryImpl.kt): Coordinates fetching lyrics from cache or online APIs.
- [TransitionRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/TransitionRepository.kt), [TransitionRepositoryImpl.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/repository/TransitionRepositoryImpl.kt): Manages crossfade rules database operations.

#### 4.7.4. Services (`data/service`)
- [MusicService.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/MusicService.kt): Core Media3 background service. Binds controllers, handles lockscreen/bluetooth media buttons, manages system notifications, and hosts the player engines.
- [CoilBitmapLoader.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/CoilBitmapLoader.kt): Loads cover art images into Media3 system controllers.
- [MusicNotificationProvider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/MusicNotificationProvider.kt), [LocalOnlyMediaNotificationProvider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/LocalOnlyMediaNotificationProvider.kt): Customizes Android status-bar media control layouts.
- [PixelPlayMediaButtonReceiver.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/PixelPlayMediaButtonReceiver.kt): Custom key receiver targeting remote device commands.
- [PlaybackActivityTracker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/PlaybackActivityTracker.kt): Dispatches logs when playback starts/stops.
- [ReplayGainProcessor.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/ReplayGainProcessor.kt): Normalizes track volumes using tags.
- [WidgetUpdateManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/WidgetUpdateManager.kt): Pushes updates to Android Glance home screen widgets.
- [SleepTimerReceiver.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/SleepTimerReceiver.kt): System alarm receiver pausing player.
- [TrustedMediaItemsResolution.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/TrustedMediaItemsResolution.kt): Validates media IDs.
- [CastSyncCoordinator.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/CastSyncCoordinator.kt): Syncs phone queues with Chromecast devices.

#### 4.7.5. Playback Engines (`data/service/player`)
- [DualPlayerEngine.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/DualPlayerEngine.kt): Core custom player wrapping two ExoPlayer instances. Coordinates gapless transitions and seamless track-crossfading.
- [CastPlayer.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/CastPlayer.kt): ExoPlayer implementation for Chromecast.
- [AudioDecoderPolicy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/AudioDecoderPolicy.kt): Matches files to hardware/software decoders.
- [HiFiCapabilityChecker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/HiFiCapabilityChecker.kt): Scans device capabilities for high-res output.
- [HiResSampleRateCapAudioProcessor.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/HiResSampleRateCapAudioProcessor.kt): Downsamples audio if files exceed hardware capacities.
- [SurroundDownmixProcessor.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/SurroundDownmixProcessor.kt): Normalizes multi-channel surround tracks to stereo.
- [TransitionController.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/player/TransitionController.kt): Computes volume curves during crossfades.

#### 4.7.6. Streaming Integrations (`data/gdrive`, `data/jellyfin`, `data/navidrome`, `data/netease`, `data/qqmusic`, `data/telegram`)
Services, stream proxies, and api builders for streaming integrations:
- **Google Drive**: [GDriveApiService.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/gdrive/GDriveApiService.kt), [GDriveRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/gdrive/GDriveRepository.kt), [GDriveStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/gdrive/GDriveStreamProxy.kt), [GDriveConstants.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/gdrive/GDriveConstants.kt).
- **Jellyfin**: [JellyfinRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/jellyfin/JellyfinRepository.kt), [JellyfinStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/jellyfin/JellyfinStreamProxy.kt).
- **Navidrome**: [NavidromeRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/navidrome/NavidromeRepository.kt), [NavidromeStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/navidrome/NavidromeStreamProxy.kt).
- **Netease Cloud Music**: [NeteaseRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/netease/NeteaseRepository.kt), [NeteaseStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/netease/NeteaseStreamProxy.kt).
- **QQ Music**: [QqMusicRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/qqmusic/QqMusicRepository.kt), [QqMusicStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/qqmusic/QqMusicStreamProxy.kt).
- **Telegram**: [TelegramCacheManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/telegram/TelegramCacheManager.kt), [TelegramClientManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/telegram/TelegramClientManager.kt), [TelegramRepository.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/telegram/TelegramRepository.kt), [TelegramStreamProxy.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/telegram/TelegramStreamProxy.kt).

#### 4.7.7. Chromecast & Local HTTP Streaming Server (`data/service/http` & `data/service/cast`)
- [MediaFileHttpServerService.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/http/MediaFileHttpServerService.kt): Local HTTP server service. When casting local music files to Chromecast, it streams files from local storage over the local network via HTTP.
- [CastSessionSecurity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/http/CastSessionSecurity.kt): Secures dynamic links between the mobile app and casting receivers.
- [CastAudioMimeUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/cast/CastAudioMimeUtils.kt): Maps file containers into Cast-compatible formats.
- [CastOptionsProvider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/cast/CastOptionsProvider.kt): Setup receiver application IDs.
- [CastRemotePlaybackState.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/cast/CastRemotePlaybackState.kt): Maps receiver progress outputs.
- [IsoBmffAudioCodecDetector.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/cast/IsoBmffAudioCodecDetector.kt): Analyzes MP4 containers for audio casting compatibility.

#### 4.7.8. Wear OS Syncing & Operations (`data/service/wear`)
- [PhoneDirectWatchTransferCoordinator.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/PhoneDirectWatchTransferCoordinator.kt): Large coordinator managing file splitting, transmission packet checks, and watch transfer logs.
- [PhoneWatchTransferStateStore.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/PhoneWatchTransferStateStore.kt), [PhoneWatchTransferCancellationStore.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/PhoneWatchTransferCancellationStore.kt): Tracks and cancels watch downloads.
- [WatchTransferForegroundService.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/WatchTransferForegroundService.kt): Foreground service keeping connection streams alive during batch transmissions.
- [WearCommandReceiver.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/WearCommandReceiver.kt): Receives and executes remote commands from the watch.
- [WearStatePublisher.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/WearStatePublisher.kt): Publishes playback progress, track details, and library changes to the Wear OS device.
- [WearPhoneTransferSender.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/WearPhoneTransferSender.kt): Channels binary packets via Wearable API channels.
- [WearThemePaletteFactory.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/service/wear/WearThemePaletteFactory.kt): Generates color palette configurations to theme the Wear OS interface based on mobile art colors.

#### 4.7.9. Background Syncing Tasks (`data/worker`)
- [SyncWorker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/SyncWorker.kt): Large WorkManager task. Scans Android MediaStore, processes directories, extracts tags/artwork files, updates DB entries, and flags changes.
- [SyncManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/SyncManager.kt): Coordinator scheduling sync tasks.
- [NavidromeSyncWorker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/NavidromeSyncWorker.kt): Synchronization task specific to Navidrome libraries.
- [AiWorker.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/AiWorker.kt), [AiWorkerManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/AiWorkerManager.kt): Manages background AI summaries tasks.
- [AlbumGroupingUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/AlbumGroupingUtils.kt): Helper matching tracks to parent albums based on tags and directories.
- [ArtistParsingUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/data/worker/ArtistParsingUtils.kt): Helper splitting multi-artist tags.

---

### 4.8. Core Application Utilities (`utils`)
- [AlbumArtUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AlbumArtUtils.kt): Large utility. Handles embedding checks, writes artwork cache files, extracts tags, and resolves fallbacks.
- [AlbumArtCacheManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AlbumArtCacheManager.kt): Clears and limits the album art cache folder.
- [AppLocaleManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AppLocaleManager.kt): Switches languages dynamically.
- [AppShortcutManager.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AppShortcutManager.kt): Updates launcher dynamic shortcuts.
- [AudioDecoder.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AudioDecoder.kt): Resolves decoders supporting raw streams.
- [AudioFileProvider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AudioFileProvider.kt): ContentProvider enabling file sharing.
- [AudioMetaUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/AudioMetaUtils.kt): Extracts bitrate and metadata properties.
- [ColorUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/ColorUtils.kt): Converts color formats and measures luminance.
- [CrashHandler.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/CrashHandler.kt): Catches unhandled thread exceptions and writes details to disk.
- [DirectoryFilterUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/DirectoryFilterUtils.kt), [DirectoryRuleResolver.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/DirectoryRuleResolver.kt): Evaluates directory exclusion configurations.
- [FileDeletionUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/FileDeletionUtils.kt): Handles file deletion requests.
- [LyricsUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/LyricsUtils.kt): Huge utility parsing standard lyric formats (LRC, SRT, VTT) and aligning scroll timings.
- [TtmlLyricsParser.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/TtmlLyricsParser.kt): Parses TTML lyric files.
- [MediaStorePermissionHelper.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/MediaStorePermissionHelper.kt): Manages storage access permissions.
- [MediaStoreSelectionUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/MediaStoreSelectionUtils.kt): Filters files by size/duration thresholds.
- [PlaylistCoverColors.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/PlaylistCoverColors.kt): Resolves gradients for fallback playlist covers.
- [QueueUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/QueueUtils.kt): Queue shuffling and layout builders.
- [StorageUtils.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/StorageUtils.kt): Resolves file size labels and formats paths.
- [ZipShareHelper.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/utils/ZipShareHelper.kt): Compels batch selections into shared zip archives.

---

## 5. Wear OS Module (`:wear`)

Located in `wear/src/main/java/com/theveloper/pixelplay`:

- [WearApp.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/wear/src/main/java/com/theveloper/pixelplay/WearApp.kt): Entry point launching watch interface layouts.
- **`data` package**: Coordinates direct file writes and receives mobile sync updates.
- **`di` package**: Hilt setup for watch managers.
- **`presentation` package**: Contains Wear OS user interfaces (remote player views, volume sliders, connection status dialogs).

---

## 6. Key UI Customization Entry Points

If you plan to modify only the **UI & Design Aesthetics** without changing business logic, here are the core files to focus on:

1. **Colors, Shape, and Typography**:
   - Update [Color.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Color.kt) to modify the base color palette.
   - Adjust [Theme.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Theme.kt) to change how the dynamic colors are generated and applied.
   - Update [Type.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/ui/theme/Type.kt) to switch fonts or tweak text sizes/weights.
2. **Main Player Interface**:
   - Modify [UnifiedPlayerSheetV2.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/UnifiedPlayerSheetV2.kt) to customize the full-screen playback screen (art card placement, blur levels, seeker bar look, overlay drawers).
   - Edit [WavyMusicSlider.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/WavyMusicSlider.kt) to style the animated waveform seeker.
3. **App Structure & Bottom Navigation**:
   - Customize [PlayerInternalNavigationBar.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/PlayerInternalNavigationBar.kt) to change the bottom nav bar layout (corner radius, sizing, icons).
   - Adjust [AppSidebarDrawer.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/components/AppSidebarDrawer.kt) to update the sidebar layout.
4. **General App Shell**:
   - Tweak [MainActivity.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/MainActivity.kt)'s `MainUI` scaffold parameters (e.g. edge-to-edge system bar paddings).
5. **Dashboard, Library & Detail Screen Layouts**:
   - Modify [HomeScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/HomeScreen.kt) (Home Dashboard grid/cards).
   - Modify [LibraryScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/LibraryScreen.kt) (Library structure, tabs, favorites).
   - Customize detail screens: [AlbumDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/AlbumDetailScreen.kt), [ArtistDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/ArtistDetailScreen.kt), and [PlaylistDetailScreen.kt](file:///c:/Users/omnay/Downloads/PixelPlayer/app/src/main/java/com/theveloper/pixelplay/presentation/screens/PlaylistDetailScreen.kt).
