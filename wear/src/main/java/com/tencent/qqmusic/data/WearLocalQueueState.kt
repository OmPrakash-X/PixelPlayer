package com.tencent.qqmusic.data

import com.tencent.qqmusic.shared.WearLibraryItem

data class WearLocalQueueState(
    val items: List<WearLibraryItem> = emptyList(),
    val currentIndex: Int = -1,
)
