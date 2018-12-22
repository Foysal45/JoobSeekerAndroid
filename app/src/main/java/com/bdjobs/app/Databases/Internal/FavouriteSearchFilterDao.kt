package com.bdjobs.app.Databases.Internal

import androidx.room.*

@Dao
interface FavouriteSearchFilterDao {

    @Query("SELECT * FROM FavouriteSearch ORDER BY createdon DESC")
    fun getAllFavouriteSearchFilter(): List<FavouriteSearch>


    @Query("SELECT * FROM FavouriteSearch WHERE filterid = :filterid")
    fun getFavouriteSearchByID(filterid: String): FavouriteSearch

    @Query("SELECT * FROM FavouriteSearch ORDER BY createdon DESC LIMIT 2")
    fun getLatest2FavouriteSearchFilter(): List<FavouriteSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavouriteSearchFilter(favouriteSearch: FavouriteSearch): Long

    @Transaction
    fun updateFavouriteSearchFilter(favouriteSearch: FavouriteSearch) {
        deleteFavouriteSearchByID(favouriteSearch.filterid!!)
        insertFavouriteSearchFilter(favouriteSearch)
    }

    @Query("DELETE FROM FavouriteSearch")
    fun deleteAllFavouriteSearch()

    @Query("DELETE FROM FavouriteSearch WHERE filterid = :filterid")
    fun deleteFavouriteSearchByID(filterid: String)
}