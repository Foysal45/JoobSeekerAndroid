package com.bdjobs.app.Databases.Internal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.*


@Entity(tableName = "Suggestion", indices = [(Index(value = ["Suggestions"], unique = true))])
data class Suggestion(@ColumnInfo(name = "Suggestions")
                      val suggestion: String,
                      @ColumnInfo(name = "Flag")
                      val flag: String,
                      @ColumnInfo(name = "UserID")
                      val userID: String?=null,
                      @ColumnInfo(name = "InsertTime")
                      val time: Date) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}