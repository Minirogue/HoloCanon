package com.minirogue.starwarscanontracker.model

import android.content.Context
import android.util.Log
import com.minirogue.starwarscanontracker.model.room.MediaDatabase
import com.minirogue.starwarscanontracker.model.room.pojo.MediaAndNotes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class TransferDatabase @Inject constructor(private val correctNamedDatabase: MediaDatabase, private val ctx : Context) {

    companion object {
        private const val TAG = "DatabaseTransfer"
    }
    fun checkAndTransferFrom(wrongName: String){
        GlobalScope.launch{transferFrom(wrongName)}
    }

    private suspend fun transferFrom(wrongName: String) = withContext(Dispatchers.Default){
        val wrongNamedDatabaseFile = File(ctx.getDatabasePath(wrongName).absolutePath)
        if (wrongNamedDatabaseFile.exists()) {
            Log.i(TAG, "Incorrectly named database detected. Attempting to transfer data to correct database.")
            val wrongDatabase = MediaDatabase.getDatabaseByName(wrongName, ctx)
            val seriesJob = launch {updateSeries(wrongDatabase)}
            val typeJob = launch {updateType(wrongDatabase)}
            val allMediaAndNotes = wrongDatabase.daoMedia.allMediaAndNotes
            seriesJob.join()
            typeJob.join()
            val itemsAndNotesJob = launch { updateItemsAndNotes(allMediaAndNotes) }
            itemsAndNotesJob.join()
            Log.i(TAG, "Deleting incorrect database")
            withContext(Dispatchers.IO) {
                if (wrongNamedDatabaseFile.delete()){
                    Log.i(TAG, "deleted successfully")
                }else{
                    Log.i(TAG, "deletion failed")
                }
            }
        }
    }

    private suspend fun updateSeries(wrongDatabase: MediaDatabase) = withContext(Dispatchers.Default){
        Log.i(TAG, "starting series insertion")
        val allSeries = wrongDatabase.daoSeries.getAllNonLive()
        allSeries.forEach { correctNamedDatabase.daoSeries.insert(it) }
        Log.i(TAG, "series insertion complete")
    }

    private suspend fun updateType(wrongDatabase: MediaDatabase) = withContext(Dispatchers.Default){
        Log.i(TAG, "starting mediatype insertion")
        val allSeries = wrongDatabase.daoType.allNonLive
        allSeries.forEach { correctNamedDatabase.daoType.insert(it) }
        Log.i(TAG, "mediatype insertion complete")

    }

    private suspend fun updateItemsAndNotes(allItemsAndNotes: List<MediaAndNotes>) = withContext(Dispatchers.Default){
        Log.i(TAG, "updating medaitems and notes")
        allItemsAndNotes.forEach {
            if (correctNamedDatabase.daoMedia.insert(it.mediaItem) < 0){
                correctNamedDatabase.daoMedia.update(it.mediaItem)
            }
            if (correctNamedDatabase.daoMedia.insert(it.mediaNotes) < 0){
                correctNamedDatabase.daoMedia.update(it.mediaNotes)
            }
        }
        Log.i(TAG, "media and notes updated")
    }


}