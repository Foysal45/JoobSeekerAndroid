package com.bdjobs.app.databases.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface SuggestionDAO {

    @Query("SELECT * FROM Suggestion WHERE flag = :flag ORDER BY InsertTime DESC LIMIT 5")
    fun getSuggestionByFlag(flag: String): List<Suggestion>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSuggestion(suggestion: Suggestion)

    @Query("DELETE FROM Suggestion")
    fun deleteAllSuggestion()

    @Query("DELETE FROM Suggestion WHERE flag = :flag")
    fun deleteAllKeywordSuggestion(flag: String)
}