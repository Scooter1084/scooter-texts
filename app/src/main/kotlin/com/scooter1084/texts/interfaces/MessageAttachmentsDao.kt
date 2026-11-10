package com.scooter1084.texts.interfaces

import androidx.room.Dao
import androidx.room.Query
import com.scooter1084.texts.models.MessageAttachment

@Dao
interface MessageAttachmentsDao {
    @Query("SELECT * FROM message_attachments")
    fun getAll(): List<MessageAttachment>
}
