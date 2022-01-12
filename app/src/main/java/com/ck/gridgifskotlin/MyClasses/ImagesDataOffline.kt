package com.ck.gridgifskotlin.MyClasses

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class ImagesDataOffline {
    @PrimaryKey(autoGenerate = true)
    var Id = 0

    @ColumnInfo(name = "previewImg")
    var previewImageLocal: String? = null

    @ColumnInfo(name = "OriginalImg")
    var OriginalImageLocal: String? = null
}