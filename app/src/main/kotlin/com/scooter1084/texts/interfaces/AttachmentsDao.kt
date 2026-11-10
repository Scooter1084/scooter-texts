package com.scooter1084.texts.interfaces

import androidx.room.Dao
import androidx.room.Query
import com.scooter1084.texts.models.Attachment

@Dao
interface AttachmentsDao {
    @Query("SELECT * FROM attachments")
    fun getAll(): List<Attachment>
}
