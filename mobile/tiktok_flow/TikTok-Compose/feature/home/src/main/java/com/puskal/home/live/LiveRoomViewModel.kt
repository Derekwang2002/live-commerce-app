package com.puskal.home.live

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.puskal.data.repository.live.LiveRoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LiveRoomViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val liveRoomRepository: LiveRoomRepository
) : ViewModel() {

    private val roomId: String = savedStateHandle.get<String>("room_id").orEmpty()

    private val _uiState = MutableStateFlow(LiveRoomState(roomId = roomId))
    val uiState: StateFlow<LiveRoomState> = _uiState.asStateFlow()

    init {
        loadLiveRoom()
    }

    private fun loadLiveRoom() {
        viewModelScope.launch {
            val room = liveRoomRepository.getLiveRoom(roomId)
            _uiState.value = LiveRoomState(
                roomId = room.roomId,
                anchor = room.anchor,
                title = room.title,
                liveStreamUrl = room.liveStreamUrl,
                onlineCount = room.onlineCount,
                statusTags = room.statusTags,
                comments = room.comments,
                currentProduct = room.currentProduct,
                isLoading = false
            )
        }
    }
}
