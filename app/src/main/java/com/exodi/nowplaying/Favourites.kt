package com.exodi.nowplaying

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class Favourites(context: Context) {

    private val prefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE)

    private val _ids = MutableStateFlow(prefs.getStringSet(KEY, emptySet()).orEmpty())
    val ids: StateFlow<Set<Long>> = _ids.asStateFlow()

    fun contains(trackId: Long): Boolean = _ids.value.contains(trackId)

    fun toggle(trackId: Long) {
        val next = _ids.value.toMutableSet()
        if (!next.add(trackId)) next.remove(trackId)
        prefs.edit().putStringSet(KEY, next).apply()
        _ids.value = next
    }

    private companion object {
        const val KEY = "ids"
    }
}
