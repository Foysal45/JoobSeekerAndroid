package com.bdjobs.app.databases.internal

import androidx.room.*

@Dao
interface FavouriteSearchFilterDao {

    @Query("SELECT * FROM FavouriteSearch ORDER BY createdon DESC")
    fun getAllFavouriteSearchFilter(): List<FavouriteSearch>


    @Query("SELECT * FROM FavouriteSearch WHERE filterid = :filterid")
    fun getFavouriteSearchByID(filterid: String?): FavouriteSearch

    @Query("SELECT * FROM FavouriteSearch WHERE filtername = :filterName")
    fun getFavouriteSearchByName(filterName: String): FavouriteSearch

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

    @Query("SELECT * FROM FavouriteSearch WHERE industrialCat =:icat and functionalCat=:fcat and location=:location and organization=:qOT and jobnature=:qJobNature and joblevel=:qJobLevel and postedon=:qPosted and deadline=:qDeadline and keyword =:txtsearch and experience =:qExp and gender=:qGender and genderb=:qGenderB and jobtype=:qJobSpecialSkill and retiredarmy=:qRetiredArmy and age=:qAge and newspaper=:newspaper and workPlace=:workPlace and personWithDisability=:personWithDisability")
    fun getFavFilterByFilters(
            icat: String? = "",
            fcat: String? = "",
            location: String? = "",
            qOT: String? = "",
            qJobNature: String? = "",
            qJobLevel: String? = "",
            qPosted: String? = "",
            qDeadline: String? = "",
            txtsearch: String? = "",
            qExp: String? = "",
            qGender: String? = "",
            qGenderB: String? = "",
            qJobSpecialSkill: String? = "",
            qRetiredArmy: String? = "",
            qAge: String? = "",
            newspaper: String? = "",
            workPlace: String? = "",
            personWithDisability: String? = "",
    ): List<FavouriteSearch>

}