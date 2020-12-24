package com.bdjobs.app.databases.External

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import java.io.FileOutputStream
import java.io.IOException


internal class DBHelper(private val myContext: Context) : SQLiteOpenHelper(myContext, DB_NAME, null, DATABASE_VERSION) {
    //************************************************************************************************//


    private var myDataBase: SQLiteDatabase? = null

    private val isDatabaseExist: Boolean
        get() {
            var kontrol: SQLiteDatabase? = null

            try {
                val myPath = DB_PATH + DB_NAME
                kontrol = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY)

            } catch (e: SQLiteException) {
                kontrol = null
            }

            if (kontrol != null) {
                kontrol.close()
            }
            return kontrol != null
        }

    @Throws(IOException::class)
    fun crateDatabase() {
        val vtVarMi = isDatabaseExist

        if (!vtVarMi) {
            try {
                val db = this.readableDatabase
                if (db.isOpen){
                    db.close()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                copyDataBase()
            } catch (e: Exception) {
                // throw new Error("Error copying database");
                e.printStackTrace()
            }

        }
    }

    @Throws(IOException::class)
    private fun copyDataBase() {

        // Open your local db as the input stream
        try {
            val myInput = myContext.assets.open(DB_NAME)

            // Path to the just created empty db
            val outFileName = DB_PATH + DB_NAME

            // Open the empty db as the output stream
            val myOutput = FileOutputStream(outFileName)

            // transfer bytes from the inputfile to the outputfile
            val buffer = ByteArray(1024)
            var length: Int =myInput.read(buffer)
            while (length > 0) {
                myOutput.write(buffer, 0, length)
                length=myInput.read(buffer)
            }

            // Close the streams
            myOutput.flush()
            myOutput.close()
            myInput.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @Throws(SQLException::class)
    fun openDataBase() {
        // Open the database
        try {
            val myPath = DB_PATH + DB_NAME
            myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun dataBase(): SQLiteDatabase? {
        return myDataBase
    }

    fun getCursor(query: String): Cursor {

        return myDataBase!!.rawQuery(query, null)
    }

    @Synchronized
    override fun close() {
        if (myDataBase != null)
            myDataBase!!.close()
        super.close()
    }

    override fun onCreate(db: SQLiteDatabase) {}

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    companion object {

        val DB_PATH = "/data/data/com.bdjobs.app/databases/"
        val DB_NAME = "bdjobs_new.sqlite"
        val DATABASE_VERSION = 1

        //-----------------------------------JobRoleInfo------------------------------------------------//
        val TABLE_NAME_JOB_ROLE_INFO = "JobRoleInfo"
        val JOB_ROLE_INFO_COL_SL = "Sl"
        val JOB_ROLE_INFO_COL_JOB_ROLE_ID = "JObRoleID"
        val JOB_ROLE_INFO_CAT_NAME = "Cat_Name"
        val JOB_ROLE_INFO_COL_JOB_ROLE = "JobRole"
        val JOB_ROLE_INFO_COL_MODULE_NAME = "ModuleName"
        val JOB_ROLE_INFO_COL_MODULE_DESCRIPTION = "ModuleDescription"
        val JOB_ROLE_INFO_COL_DURATION = "Duration"
        val JOB_ROLE_INFO_COL_SAMPLE_LINK = "SampleLink"
        val JOB_ROLE_INFO_COL_CATEGORY_ID = "categoryid"
        val JOB_ROLE_INFO_COL_PRIORITY = "priority"
        //************************************************************************************************//

        //-----------------------------------Org_Types----------------------------------------------------//
        val TABLE_NAME_ORG_TYPES = "Org_Types"
        val ORG_TYPES_COL_ORG_TYPE_ID = "ORG_TYPE_ID"
        val ORG_TYPES_COL_ORG_TYPE_NAME = "ORG_TYPE_NAME"
        val ORG_TYPES_COL_IndustryId = "IndustryId"
        val ORG_TYPES_COL_ORG_TYPE_NAME_BNG = "ORG_TYPE_NAME_BNG"
        //************************************************************************************************//

        //-----------------------------EDU_LEVELS---------------------------------------------------------//
        val TABLE_NAME_EDU_LEVELS = "EDUCATION_LEVELS"
        val EDU_LEVELS_COL_EDU_LEVEL = "EDU_LEVEL"
        val EDU_LEVELS_COL_EDU_ID = "EDU_ID"
        //************************************************************************************************//

        val TABLE_NAME_BOARDS = "Board"
        val BOARDS_COL_NAME = "name"
        val BOARDS_COL_VALUE = "value"

        //-------------------------------EDU_TYPES--------------------------------------------------------//
        val TABLE_NAME_EDU_TYPES = "EDUCATION_TYPES"
        val EDU_TYPES_COL_EDU_TYPES = "EDU_TYPES"
        val EDU_TYPES_COL_EDU_TYPES_ID = "EDU_TYPE_ID"
        val EDU_TYPES_COL_EDU_TYPES_TAG = "TAG"
        //************************************************************************************************//

        //-----------------------------LOCATIONS----------------------------------------------------------//
        val TABLE_NAME_LOCATIONS = "Locations"
        val LOCATIONS_COL_LOCATION_ID = "L_ID"
        val LOCATIONS_COL_LOCATION_TYPE = "L_TYPE"
        val LOCATIONS_COL_LOCATION_NAME = "L_Name"
        val LOCATIONS_COL_LOCATION_NAME_BANGLA = "L_NameBangla"
        val LOCATIONS_COL_LOCATION_PARENT_ID = "ParentId"
        val LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH = "OutsideBangladesh"
        val LOCATIONS_COL_LOCATION_COUNTRY_CODE = "CountryCode"
        val LOCATIONS_COL_LOCATION_CODE_TYPE = "CodeType"

        //************************************************************************************************//

        //-----------------------------CATEGORY-----------------------------------------------------------//
        val TABLE_NAME_CATEGORY = "CATEGORY"
        val CATEGORY_COL_CAT_ID = "CAT_ID"
        val CATEGORY_COL_CAT_NAME = "CAT_NAME"
        val CATEGORY_COL_CAT_NAME_BANGLA = "CAT_NAME_Bangla"
        val CATEGORY_COL_CAT_ORD = "Ord"
        //************************************************************************************************//

        //-----------------------------INSTITUTES---------------------------------------------------------//
        val TABLE_NAME_INSTITUTES = "INSTITUTES"
        val INSTITUTES_COL_INSTITUTE_ID = "INST_ID"
        val INSTITUTES_COL_INSTITUTE_NAME = "INST_Name"
        //************************************************************************************************//

        //-------------------------MAJOR_SUBJECTS---------------------------------------------------------//
        val TABLE_NAME_MAJOR_SUBJECTS = "MAJOR_SUBJECT"
        val MAJOR_SUBJECTS_COL_MAJOR_ID = "MAJOR_Id"
        val MAJOR_SUBJECTS_COL_MAJOR_NAME = "MAJOR_Name"
        //************************************************************************************************//

        //--------------------------------RESULTS---------------------------------------------------------//
        val TABLE_NAME_RESULTS = "Results"
        val RESULTS_COL_RESULT = "Result"
        val RESULTS_COL_RESULT_ID = "Result_ID"
        //************************************************************************************************//

        //--------------------------------MARITAL---------------------------------------------------------//
        val TABLE_NAME_MARITAL = "Marital_Status"
        val MARITAL_COL_MARITAL_STATUS = "MRTL_ST"
        val MARITAL_COL_MARITAL_ID = "MRTL_ID"
        //************************************************************************************************//

        //---------------------------------GENDER---------------------------------------------------------//
        val TABLE_NAME_GENDER = "Gender"
        val GENDER_COL_GENDER = "Gender"
        val GENDER_COL_GENDER_ID = "G_ID"
        //************************************************************************************************//

        //--------------------------------LOOKING---------------------------------------------------------//
        val TABLE_NAME_LOOKING = "Looking_For"
        val LOOKING_COL_LOOKING_FOR = "LK_Name"
        val LOOKING_COL_LOOKING_ID = "LK_ID"
        //************************************************************************************************//

        //--------------------------------EDUCATION_DEGREES---------------------------------------------//
        val TABLE_NAME_EDUCATION_DEGREES = "educationDegrees"
        val EDUCATION_DEGREES_COL_EDU_LEVEL = "EduLevel"
        val EDUCATION_DEGREES_COL_DEGREE_NAME = "DegreeName"
        val EDUCATION_DEGREES_COL_EDUCATION_TYPE = "EducationType"
        //************************************************************************************************//


        //--------------------------------SUB_CATEGORY -------------------------------------------------//
        val TABLE_NAME_SUB_CATEGORY = "sub_category"
        val SUB_CATEGORY_SUB_CAT_ID = "SUB_CAT_ID"
        val SUB_CATEGORY_COL_CAT_ID = "CAT_ID"
        val SUB_CATEGORY_COL_SUB_NAME = "SUB_NAME"
        val SUB_CATEGORY_COL_SUB_TYPE = "SUB_TYPE"
        val SUB_CATEGORY_COL_SNO = "SNO"
        val SUB_CATEGORY_COL_Sub_NAME_BNG = "Sub_NAME_BNG"
        val SUB_CATEGORY_COL_User_Defined = "UserDefined"
        val SUB_CATEGORY_COL_Verified_On = "VerifiedOn"

        //************************************************************************************************//

        //---------------------------------NEWSPAPER----------------------------------------------------//
        val TABLE_NAME_NEWSPAPER = "newspaper"
        val NEWSPAPER_COL_NEWSPAPER_NAME = "name"
        val NEWSPAPER_COL_NEWSPAPER_ID = "id"
        //************************************************************************************************//

        //---------------------------------AGERANGE-----------------------------------------------------//
        val TABLE_NAME_AGERANGE = "AGE_RANGE"
        val AGERANGE_COL_AGE_RANGE = "AGE_RANGE_NAME"
        val AGERANGE_COL_AGE_ID = "AGE_RANGE_ID"

        //---------------------------------ORG_TYPE_JOB_SEARCH-----------------------------------------------------//
        val TABLE_NAME_ORG_TYPE_JOB_SEARCH = "Org_types_job_search"
        val ORG_TYPE_JOB_SEARCH_COL_ORG_TYPE = "orgType"
        val ORG_TYPE_JOB_SEARCH_COL_ID = "id"

        //---------------------------------ORG_TYPE_JOB_SEARCH-----------------------------------------------------//
        val TABLE_NAME_INDUSTRY = "Industries"
        val INDUSTRY_COL_INDUSTRY_NAME_ENGLISH = "IndustryName"
        val INDUSTRY_COL_INDUSTRY_NAME_BANGLA = "IndustryNameBng"
        val INDUSTRY_COL_ID = "IndustryId"


        //---------------------------------JOB_NATURE-----------------------------------------------------//
        val TABLE_NAME_JOB_NATURE = "JOB_NATURE"
        val JOB_NATURE_COL_JOB_NATURE_NAME = "JOB_NATURE_NAME"
        val JOB_NATURE_COL_JOB_NATURE_ID = "JOB_NATURE_ID"

        //---------------------------------EXPERIENCE-----------------------------------------------------//
        val TABLE_NAME_EXPERIENCE = "Experinece"
        val EXPERIENCE_COL_EXPERIENCE_NAME = "EXP_NAME"
        val EXPERIENCE_COL_EXPERIENCE_ID = "EXP_ID"

        //---------------------------------JOB_LEVEL-----------------------------------------------------//
        val TABLE_NAME_JOB_LEVEL = "JOB_LEVEL"
        val JOB_LEVEL_COL_JOB_LEVEL_NAME = "JOB_LEVEL_NAME"
        val JOB_LEVEL_COL_JOB_LEVEL_ID = "JOB_LEVEL_ID"

        //---------------------------------JOB_TYPE-----------------------------------------------------//
        val TABLE_NAME_JOB_TYPE = "JOB_TYPE"
        val JOB_TYPE_COL_JOB_TYPE_NAME = "JOB_TYPE_NAME"
        val JOB_TYPE_COL_ID = "JOB_ID"



        //---------------------------------POSTED_WITHIN-----------------------------------------------------//
        val TABLE_NAME_POSTED_WITHIN = "POSTED_WITHIN"
        val POSTED_WITHIN_COL_POSTED_WITHIN_NAME = "POSTED_WITHIN_NAME"
        val POSTED_WITHIN_COL_POSTED_WITHIN_ID = "POSTED_WITHIN_ID"



        //---------------------------------DEADLINE-----------------------------------------------------//
        val TABLE_NAME_JOB_DEADLINE = "DEADLINE"
        val JOB_DEADLINE_COL_JOB_DEADLINE_NAME = "DEADLINE_NAME"
        val JOB_DEADLINE_COL_ID = "DEADLINE_ID"

        //---------------------------------KEYWORD_SUGGESYIONS-----------------------------------------------------//
        val TABLE_NAME_KEYWORD_SUGGESYIONS = "keyword_suggestions"
        val KEYWORD_SUGGESYIONS_COL_KEYWORDS = "keyword"

        //---------------------------------DegreeWiseModule-----------------------------------------------------//
        val TABLE_DEGREE_WISE_MODULE = "DegreeWiseModule"
        val DEGREE_ID = "DegreeId"
        val AM_DEGREE_NAME = "AM_DegreeName"
        val REQUIRED_DEGREE = "Required"
        val MODULE_NAME = "ModuleName"
        val EXAM_DURATION = "Duration"
        val MODULE_SAMPLE_LINK = "SampleLink"
        val DEGREE_SUBJECTS = "Subjects"

    }
}