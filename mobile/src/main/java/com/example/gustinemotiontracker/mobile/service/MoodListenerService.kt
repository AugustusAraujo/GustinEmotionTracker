package com.example.gustinemotiontracker.mobile.service

import android.util.Log
import com.example.gustinemotiontracker.core.serialization.MoodEntrySerializer
import com.example.gustinemotiontracker.mobile.MoodTrackerApplication
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class MoodListenerService : WearableListenerService() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path
            if (event.type == DataEvent.TYPE_CHANGED &&
                path?.startsWith(MoodEntrySerializer.MOOD_ENTRY_PATH) == true
            ) {
                val entry = runCatching {
                    MoodEntrySerializer.fromDataMap(
                        DataMapItem.fromDataItem(event.dataItem).dataMap
                    )
                }.getOrNull()
                if (entry != null) {
                    scope.launch {
                        (application as MoodTrackerApplication).repository.insert(entry)
                        Log.i(TAG, "Registro salvo: ${entry.emotion} (${entry.quadrant.name})")
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        scope.cancel()
        super.onDestroy()
    }

    private companion object {
        const val TAG = "MoodListenerService"
    }
}
