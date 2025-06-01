package com.holocanon.library.sorting.internal.data

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.holocanon.library.sorting.model.SortStyle
import com.holocanon.library.sorting.usecase.GetSortStyle
import com.holocanon.library.sorting.usecase.ReverseSort
import com.holocanon.library.sorting.usecase.SaveSortStyle
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class, binding<GetSortStyle>())
@ContributesBinding(AppScope::class, binding<SaveSortStyle>())
@ContributesBinding(AppScope::class, binding<ReverseSort>())
class SortStyleRepository(
    private val dataStore: SortingDataStore,
) : GetSortStyle, SaveSortStyle, ReverseSort {
    private val sortStylePreferencesKey = intPreferencesKey(SORT_STYLE_KEY)
    private val ascendingPreferencesKey =
        booleanPreferencesKey(ASCENDING_KEY)

    override fun getSortStyle(): Flow<SortStyle> = dataStore.data.map { prefs ->
        val style = prefs[sortStylePreferencesKey] ?: SortStyle.DEFAULT_STYLE.style
        val ascending = prefs[ascendingPreferencesKey] ?: SortStyle.DEFAULT_STYLE.ascending
        SortStyle(style, ascending)
    }

    override suspend fun saveSortStyle(style: SortStyle) {
        dataStore.edit { prefs ->
            prefs[sortStylePreferencesKey] = style.style
            prefs[ascendingPreferencesKey] = style.ascending
        }
    }

    override suspend fun reverse() {
        dataStore.edit { prefs ->
            prefs[ascendingPreferencesKey] = !(prefs[ascendingPreferencesKey] ?: true)
        }
    }

    companion object {
        private const val SORT_STYLE_KEY = "sort-style"
        private const val ASCENDING_KEY = "sort-ascending"
    }
}
