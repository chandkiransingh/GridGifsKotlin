package com.ck.gridgifskotlin.MyClasses

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ImageDataDao {
    @get:Query("SELECT * FROM ImagesDataOffline")
    val allImages: List<ImagesDataOffline?>?

    @Insert
    fun insertImage(vararg imgDataOffline: ImagesDataOffline?)
}