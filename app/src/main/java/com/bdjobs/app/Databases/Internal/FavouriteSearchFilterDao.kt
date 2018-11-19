package com.bdjobs.app.Databases.Internal

import androidx.room.*

@Dao
interface FavouriteSearchFilterDao {

    @Query("SELECT * FROM FavouriteSearch ORDER BY filtername")
    fun getAllFavouriteSearchFilter(): List<FavouriteSearch>

    @Query("SELECT * FROM FavouriteSearch ORDER BY filtername DESC LIMIT 2")
    fun getLatest2FavouriteSearchFilter(): List<FavouriteSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteSearchFilter(favouriteSearch: FavouriteSearch)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun updateFavouriteSearchFilter(favouriteSearch: FavouriteSearch)

    @Query("DELETE FROM FavouriteSearch")
    fun deleteAllFavouriteSearch()

    @Query("DELETE FROM FavouriteSearch WHERE filterid = :filterid")
    fun deleteFavouriteSearchByID(filterid:String)
}