package com.example.ui.util

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import com.example.data.model.SpotifyTrackEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SpotifyTrack(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationText: String,
    val coverUrl: String,
    val audioUrl: String,
    val spotifyUri: String
)

object SpotifyClassPlayer {

    private val defaultList = listOf(
        SpotifyTrack(
            id = "1",
            title = "Monokrom",
            artist = "Tulus",
            album = "Monokrom",
            durationText = "3:34",
            coverUrl = "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80",
            audioUrl = "https://actions.google.com/sounds/v1/water/rain_heavy.ogg",
            spotifyUri = "spotify:track:1"
        ),
        SpotifyTrack(
            id = "2",
            title = "Kisah Klasik",
            artist = "Sheila On 7",
            album = "Kisah Klasik Untuk Masa Depan",
            durationText = "4:15",
            coverUrl = "https://images.unsplash.com/photo-1470225620780-dba8ba36b745?w=500&q=80",
            audioUrl = "https://actions.google.com/sounds/v1/weather/thunder_crack.ogg",
            spotifyUri = "spotify:track:2"
        ),
        SpotifyTrack(
            id = "3",
            title = "Ingatlah Hari Ini",
            artist = "Project Pop",
            album = "Pop OK",
            durationText = "4:02",
            coverUrl = "https://images.unsplash.com/photo-1493225457124-a3eb161ffa5f?w=500&q=80",
            audioUrl = "https://actions.google.com/sounds/v1/water/waves_crashing_against_rocks.ogg",
            spotifyUri = "spotify:track:3"
        )
    )

    private val _playlist = MutableStateFlow<List<SpotifyTrack>>(defaultList)
    val playlist: StateFlow<List<SpotifyTrack>> = _playlist.asStateFlow()

    private var mediaPlayer: MediaPlayer? = null

    private val _currentTrack = MutableStateFlow<SpotifyTrack>(defaultList[0])
    val currentTrack: StateFlow<SpotifyTrack> = _currentTrack.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentProgress = MutableStateFlow(0f)
    val currentProgress: StateFlow<Float> = _currentProgress.asStateFlow()

    fun updateFromEntities(entities: List<SpotifyTrackEntity>) {
        if (entities.isEmpty()) return
        val mapped = entities.map { e ->
            SpotifyTrack(
                id = e.id.toString(),
                title = e.title,
                artist = e.artist,
                album = e.album,
                durationText = e.durationText,
                coverUrl = e.coverUrl.ifBlank { "https://images.unsplash.com/photo-1511671782779-c97d3d27a1d4?w=500&q=80" },
                audioUrl = e.audioUrl.ifBlank { "https://actions.google.com/sounds/v1/water/rain_heavy.ogg" },
                spotifyUri = e.spotifyUri
            )
        }
        _playlist.value = mapped
        if (_playlist.value.none { it.id == _currentTrack.value.id }) {
            _currentTrack.value = mapped[0]
        }
    }

    fun playTrack(context: Context, track: SpotifyTrack) {
        try {
            if (_currentTrack.value.id == track.id && mediaPlayer != null) {
                if (!_isPlaying.value) {
                    mediaPlayer?.start()
                    _isPlaying.value = true
                }
                return
            }

            stop()
            _currentTrack.value = track

            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                val safeUrl = if (track.audioUrl.startsWith("http")) track.audioUrl else "https://actions.google.com/sounds/v1/water/rain_heavy.ogg"
                setDataSource(context, Uri.parse(safeUrl))
                setOnPreparedListener { mp ->
                    mp.start()
                    _isPlaying.value = true
                }
                setOnCompletionListener {
                    _isPlaying.value = false
                    _currentProgress.value = 0f
                    playNext(context)
                }
                setOnErrorListener { _, _, _ ->
                    _isPlaying.value = false
                    false
                }
                prepareAsync()
            }
            mediaPlayer = player
        } catch (e: Exception) {
            _currentTrack.value = track
            _isPlaying.value = true
        }
    }

    fun togglePlay(context: Context) {
        if (_isPlaying.value) {
            pause()
        } else {
            playTrack(context, _currentTrack.value)
        }
    }

    fun pause() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.pause()
                }
            }
        } catch (_: Exception) {}
        _isPlaying.value = false
    }

    fun stop() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                it.reset()
                it.release()
            }
        } catch (_: Exception) {}
        mediaPlayer = null
        _isPlaying.value = false
        _currentProgress.value = 0f
    }

    fun playNext(context: Context) {
        val list = _playlist.value
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value.id }
        val nextIndex = if (currentIndex >= 0 && currentIndex < list.size - 1) currentIndex + 1 else 0
        if (list.isNotEmpty()) {
            playTrack(context, list[nextIndex])
        }
    }

    fun playPrevious(context: Context) {
        val list = _playlist.value
        val currentIndex = list.indexOfFirst { it.id == _currentTrack.value.id }
        val prevIndex = if (currentIndex > 0) currentIndex - 1 else (list.size - 1).coerceAtLeast(0)
        if (list.isNotEmpty()) {
            playTrack(context, list[prevIndex])
        }
    }
}
