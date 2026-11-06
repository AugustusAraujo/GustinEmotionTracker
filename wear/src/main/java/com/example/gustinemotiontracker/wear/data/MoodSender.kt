package com.example.gustinemotiontracker.wear.data

import android.content.Context
import android.util.Log
import com.example.gustinemotiontracker.core.model.MoodEntry
import com.example.gustinemotiontracker.core.serialization.MoodEntrySerializer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable

class MoodSender(context: Context) {

    private val dataClient = Wearable.getDataClient(context.applicationContext)

    fun send(entry: MoodEntry) {
        val request = PutDataMapRequest.create("${MoodEntrySerializer.MOOD_ENTRY_PATH}/${entry.timestamp}")
        MoodEntrySerializer.toDataMap(entry, request.dataMap)
        request.setUrgent()
        dataClient.putDataItem(request.asPutDataRequest())
            .addOnSuccessListener {
                Log.i(TAG, "MoodEntry enfileirado: ${entry.emotion} (${entry.quadrant.name})")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Falha ao enfileirar MoodEntry: ${entry.emotion}", e)
            }
    }

    private companion object {
        const val TAG = "MoodSender"
    }
}
