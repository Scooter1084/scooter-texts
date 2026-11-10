package com.scooter1084.texts.dialogs

import android.app.Activity
import org.fossify.commons.extensions.getAlertDialogBuilder
import org.fossify.commons.extensions.setupDialogStuff
import com.scooter1084.texts.R
import com.scooter1084.texts.databinding.DialogGroupMessageSendBinding

class GroupMessageSendDialog(
    activity: Activity,
    callback: (sendAsGroupMms: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    init {
        var selectedChoice: Boolean? = null
        val binding = DialogGroupMessageSendBinding.inflate(activity.layoutInflater)
        activity.getAlertDialogBuilder()
            .setPositiveButton(R.string.group_mms) { _, _ -> selectedChoice = true }
            .setNegativeButton(R.string.separate_messages) { _, _ -> selectedChoice = false }
            .apply {
                activity.setupDialogStuff(
                    view = binding.root,
                    dialog = this,
                    titleId = R.string.send_group_message
                ) { dialog ->
                    dialog.setOnDismissListener {
                        val choice = selectedChoice
                        onDismiss()
                        choice?.let(callback)
                    }
                }
            }
    }
}
