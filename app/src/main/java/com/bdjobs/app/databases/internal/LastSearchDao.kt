package com.bdjobs.app.databases.internal

import androidx.room.*

@Dao
interface LastSearchDao {


    @Transaction
    fun updateLastSearch(lastSearch: LastSearch) {
        deleteAllLastSearch()
        insertLastSearch(lastSearch)
    }

    @Query("SELECT * FROM LastSearch")
    fun getLastSearch(): List<LastSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastSearch(lastSearch: LastSearch):Long

    @Query("DELETE FROM LastSearch")
    fun deleteAllLastSearch()

}