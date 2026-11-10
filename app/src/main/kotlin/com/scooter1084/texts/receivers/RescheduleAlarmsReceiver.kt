package com.scooter1084.texts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.commons.helpers.ensureBackgroundThread
import com.scooter1084.texts.extensions.rescheduleAllScheduledMessages

/**
 * Reschedules alarms after boot/package updates.
 */
class RescheduleAlarmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        ensureBackgroundThread {
            context.rescheduleAllScheduledMessages()
            pendingResult.finish()
        }
    }
}
