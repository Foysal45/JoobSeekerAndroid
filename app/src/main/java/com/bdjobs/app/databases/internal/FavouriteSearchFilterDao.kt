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

    @Query("SELECT * FROM FavouriteSearch WHERE industrialCat =:icat or functionalCat=:fcat or location=:location or organization=:qOT or jobnature=:qJobNature or joblevel=:qJobLevel or postedon=:qPosted or deadline=:qDeadline or keyword =:txtsearch or experience =:qExp or gender=:qGender or genderb=:qGenderB or jobtype=:qJobSpecialSkill or retiredarmy=:qRetiredArmy or age=:qAge or newspaper=:newspaper or workPlace=:workPlace or personWithDisability=:personWithDisability or facilitiesForPWD=:facilitiesForPWD")
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
            facilitiesForPWD: String? = ""
    ): List<FavouriteSearch>

}