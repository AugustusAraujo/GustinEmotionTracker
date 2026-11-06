package com.example.gustinemotiontracker.wear.data

import android.util.Log
import com.example.gustinemotiontracker.core.serialization.PromptConfigSerializer
import com.example.gustinemotiontracker.wear.scheduler.PromptScheduler
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService

class PromptConfigListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            val path = event.dataItem.uri.path
            if (event.type == DataEvent.TYPE_CHANGED &&
                path == PromptConfigSerializer.PROMPT_CONFIG_PATH
            ) {
                val config = runCatching {
                    PromptConfigSerializer.fromDataMap(
                        DataMapItem.fromDataItem(event.dataItem).dataMap
                    )
                }.getOrNull()
                if (config != null) {
                    Log.i(TAG, "PromptConfig recebida: enabled=${config.enabled}")
                    PromptConfigStore(this).save(config)
                    PromptScheduler(this).schedule(config)
                }
            }
        }
    }

    private companion object {
        const val TAG = "PromptConfigListener"
    }
}
