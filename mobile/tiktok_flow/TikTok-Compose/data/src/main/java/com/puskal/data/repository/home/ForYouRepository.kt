package com.puskal.data.repository.home

import android.content.Context
import com.puskal.data.model.VideoModel
import com.puskal.data.source.VideoDataSource.fetchVideos
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Created by Puskal Khadka on 3/15/2023.
 */
class ForYouRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getForYouPageFeeds(): Flow<List<VideoModel>> {
        return fetchVideos(context)
    }
}
