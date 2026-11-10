package com.scooter1084.texts.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.fossify.commons.extensions.notificationManager
import org.fossify.commons.helpers.ensureBackgroundThread
import com.scooter1084.texts.extensions.deleteMessage
import com.scooter1084.texts.extensions.updateLastConversationMessage
import com.scooter1084.texts.helpers.IS_MMS
import com.scooter1084.texts.helpers.MESSAGE_ID
import com.scooter1084.texts.helpers.THREAD_ID
import com.scooter1084.texts.helpers.refreshConversations
import com.scooter1084.texts.helpers.refreshMessages

class DeleteSmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val threadId = intent.getLongExtra(THREAD_ID, 0L)
        val messageId = intent.getLongExtra(MESSAGE_ID, 0L)
        val isMms = intent.getBooleanExtra(IS_MMS, false)
        context.notificationManager.cancel(threadId.hashCode())
        ensureBackgroundThread {
            context.deleteMessage(messageId, isMms)
            context.updateLastConversationMessage(threadId)
            refreshMessages()
            refreshConversations()
        }
    }
}
