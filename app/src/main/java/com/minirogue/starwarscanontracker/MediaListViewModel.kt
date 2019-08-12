package com.minirogue.starwarscanontracker

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.preference.PreferenceManager
import androidx.lifecycle.*
import com.minirogue.starwarscanontracker.coroutinehelpers.OperationQueue
import com.minirogue.starwarscanontracker.database.MediaAndNotes
import com.minirogue.starwarscanontracker.database.MediaNotes
import com.minirogue.starwarscanontracker.database.SWMRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*

internal class MediaListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SWMRepository
    val filters: LiveData<MutableList<FilterObject>>
    val allFilters: LiveData<List<FilterObject>>
    private val data: LiveData<List<MediaAndNotes>>
    private val sortedData = MediatorLiveData<List<MediaAndNotes>>()
    private val checkboxText: Array<String>
    private val sortStyle = MutableLiveData<SortStyle>()
    private val connMgr: ConnectivityManager
    private val unmeteredOnly: Boolean
    private val sortCacheFileName = application.cacheDir.toString() + "/sortCache"
    private val sortQueue = OperationQueue()


    val filteredMediaAndNotes: LiveData<List<MediaAndNotes>>
        get() = sortedData
    val isNetworkAllowed: Boolean
        get() = !connMgr.isActiveNetworkMetered || !unmeteredOnly

    init {
        connMgr = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        repository = SWMRepository(application)
        filters = repository.getFilters()
        allFilters = repository.allFilters
        val prefs = PreferenceManager.getDefaultSharedPreferences(application)
        checkboxText = arrayOf(prefs.getString(application.getString(R.string.watched_read), application.getString(R.string.watched_read)),
                prefs.getString(application.getString(R.string.want_to_watch_read), application.getString(R.string.want_to_watch_read)),
                prefs.getString(application.getString(R.string.owned), application.getString(R.string.owned)))
        unmeteredOnly = prefs.getBoolean(application.getString(R.string.setting_unmetered_sync_only), true)
        data = repository.filteredMediaAndNotes
        viewModelScope.launch {
            val newSort = async { getSavedSort() }
            sortStyle.postValue(newSort.await())
        }
        sortedData.addSource(data) { viewModelScope.launch { sort() } }
        sortedData.addSource(sortStyle) { viewModelScope.launch { sort(); saveSort() } }
    }

    fun setSort(newCompareType: Int) {
        sortStyle.postValue(SortStyle(newCompareType, true))
    }

    private suspend fun saveSort() = withContext(Dispatchers.IO) {
        val style = sortStyle.value
        if (style != null) {
            val cacheFile = File(sortCacheFileName)
            cacheFile.writeText(style.style.toString() + " " + if (style.ascending) "1" else "0")
        }
    }

    private suspend fun getSavedSort(): SortStyle? = withContext(Dispatchers.IO) {
        val cacheFile = File(sortCacheFileName)
        if (!cacheFile.exists()) {
            SortStyle(SortStyle.SORT_TITLE, true)
        } else {
            val split = cacheFile.readText().split(" ")
            SortStyle(split[0].toInt(), split[1].toInt() == 1)
        }
    }


    fun reverseSort() {
        val currentStyle = sortStyle.value
        if (currentStyle != null) {
            sortStyle.postValue(sortStyle.value!!.reversed())
        }
    }

    private suspend fun sort() = withContext(Dispatchers.Default) {
        //Log.d(TAG, "Sort called");
        sortQueue.afterPrevious {
            val toBeSorted = data.value
            if (toBeSorted != null) {
                Collections.sort(toBeSorted, sortStyle.value
                        ?: SortStyle(SortStyle.SORT_TITLE, true))
                sortedData.postValue(toBeSorted)
            }
        }
    }

    fun getSortStyle(): LiveData<SortStyle> {
        return sortStyle
    }

    fun removeFilter(filter: FilterObject) {
        repository.removeFilter(filter)
    }

    fun addFilter(filter: FilterObject) {
        repository.addFilter(filter)
    }

    fun isCurrentFilter(filter: FilterObject): Boolean {
        return repository.isCurrentFilter(filter)
    }

    fun update(mediaNotes: MediaNotes) {
        repository.update(mediaNotes)
    }

    fun convertTypeToString(typeId: Int): String {
        return repository.convertTypeToString(typeId)
    }


    fun getCheckboxText(boxNumber: Int): String {
        return checkboxText[boxNumber]
    }

    companion object {
        private const val TAG = "MediaListViewModel"
    }
}
