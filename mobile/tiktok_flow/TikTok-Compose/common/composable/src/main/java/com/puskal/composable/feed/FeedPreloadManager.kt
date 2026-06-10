package com.puskal.composable.feed

import com.puskal.data.model.VideoModel

class FeedPreloadManager {

    fun onCurrentIndexChanged(currentIndex: Int, videos: List<VideoModel>) {
        // Placeholder for Media3 DefaultPreloadManager integration.
        // Current repository uses media3 1.0.0-rc02, so preload integration should be
        // enabled only after upgrading Media3 to a version that supports PreloadManager.
        videos.drop(currentIndex + 1).take(3)
    }

    fun release() {
        // no-op until PreloadManager is integrated
    }
}
