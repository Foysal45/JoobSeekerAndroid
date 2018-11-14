package com.bdjobs.app.Databases.External

import android.app.ProgressDialog
import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.util.Log



import java.io.IOException
import java.util.ArrayList

class DataStorage(context: Context) {

    private val dbHelper: DBHelper

    //-------------------------------------JOB_ROLE_INFO------------------------------------------//

    val allJobRoleCategory: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT DISTINCT " + DBHelper.JOB_ROLE_INFO_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " ORDER BY " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_CAT_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }


    //----------------------------------------ORG_TYPES-------------------------------------------//
    val allOrgTypes: ArrayList<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.ORG_TYPES_COL_ORG_TYPE_NAME + " FROM " + DBHelper.TABLE_NAME_ORG_TYPES
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.ORG_TYPES_COL_ORG_TYPE_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {

                e.printStackTrace()

            }

            return OrgTypes
        }

    //----------------------------------EDUCATION_LEVELS----------------------------------------------//

    val allEduLevels: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.EDU_LEVELS_COL_EDU_LEVEL + " FROM " + DBHelper.TABLE_NAME_EDU_LEVELS
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.EDU_LEVELS_COL_EDU_LEVEL)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }


    //------------------------------SUB_CATEGORY------------------------------------------------------//

    val allWorkDiscipline: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_COL_SUB_NAME + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_SUB_TYPE + " = 'Work Area'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_COL_SUB_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    val allSkills: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_COL_SUB_NAME + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_SUB_TYPE + " = 'Skill'"
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_COL_SUB_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }

    //------------------------------LOCATIONS---------------------------------------------------------//

    val allDomesticLocations: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT DISTINCT " + DBHelper.LOCATIONS_COL_LOCATION_NAME + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " = '0' ORDER BY " + DBHelper.LOCATIONS_COL_LOCATION_NAME
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    val allCountries: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_TYPE + " = 'Country' ORDER BY " + DBHelper.LOCATIONS_COL_LOCATION_NAME
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    val banglaAllDivision: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_PARENT_ID + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_ID + " !='-1'"
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }

    val allDivision: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_PARENT_ID + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_ID + " !='-1'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    val allCountryAndCountryCode: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME + "," + DBHelper.LOCATIONS_COL_LOCATION_COUNTRY_CODE + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_TYPE + " ='Country' AND " + DBHelper.LOCATIONS_COL_LOCATION_COUNTRY_CODE + " !='NULL' ORDER BY " + DBHelper.LOCATIONS_COL_LOCATION_NAME
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        val countryName = cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME))
                        val countryCode = cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_COUNTRY_CODE))
                        val countryNameAndCountryCode = "$countryName ($countryCode)"
                        OrgTypes.add(i, countryNameAndCountryCode)
                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }


    //------------------------------CATEGORIES--------------------------------------------------------//

    //String selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_NAME + " <> 'NULL'";
    val allExperienceCategories: ArrayList<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE (CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) < 30 AND " + DBHelper.CATEGORY_COL_CAT_NAME + " !='NULL') AND CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) >=0 ORDER BY " + DBHelper.CATEGORY_COL_CAT_ORD + "," + DBHelper.CATEGORY_COL_CAT_ID

                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes
        }

    //String selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_NAME + " <> 'NULL'";
    val allWhiteCollarCategories: ArrayList<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE (CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) < 30 AND " + DBHelper.CATEGORY_COL_CAT_NAME + " !='NULL') AND CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) >=-10 ORDER BY " + DBHelper.CATEGORY_COL_CAT_ORD + "," + DBHelper.CATEGORY_COL_CAT_ID

                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes
        }

    //String selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_NAME + " <> 'NULL'";
    val allBlueCollarCategoriesInBangla: ArrayList<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE (CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) > 60 and " + DBHelper.CATEGORY_COL_CAT_NAME + " !='NULL') OR CAST(" + DBHelper.CATEGORY_COL_CAT_ID + " AS INT) =-11 ORDER BY " + DBHelper.CATEGORY_COL_CAT_ORD + "," + DBHelper.CATEGORY_COL_CAT_ID
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_NAME_BANGLA)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes

        }

    //--------------------------------INSTITUTES------------------------------------------------------//

    val allInstitutes: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.INSTITUTES_COL_INSTITUTE_NAME + " FROM " + DBHelper.TABLE_NAME_INSTITUTES
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.INSTITUTES_COL_INSTITUTE_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    //--------------------------MAJOR_SUBJECTS--------------------------------------------------------//

    val allInMajorSubjects: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.MAJOR_SUBJECTS_COL_MAJOR_NAME + " FROM " + DBHelper.TABLE_NAME_MAJOR_SUBJECTS
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.MAJOR_SUBJECTS_COL_MAJOR_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    //----------------------------------Results---------------------------------------------------//
    val allResults: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.RESULTS_COL_RESULT + " FROM " + DBHelper.TABLE_NAME_RESULTS
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.RESULTS_COL_RESULT)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    //-----------------------------------Gender---------------------------------------------------//
    val allGender: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.GENDER_COL_GENDER + " FROM " + DBHelper.TABLE_NAME_GENDER
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.GENDER_COL_GENDER)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    //----------------------------------Marital---------------------------------------------------//
    val allMaritalStatus: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.MARITAL_COL_MARITAL_STATUS + " FROM " + DBHelper.TABLE_NAME_MARITAL
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.MARITAL_COL_MARITAL_STATUS)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }

    //------------------------------Looking_FOR---------------------------------------------------//
    val allLooking: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOOKING_COL_LOOKING_FOR + " FROM " + DBHelper.TABLE_NAME_LOOKING
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOOKING_COL_LOOKING_FOR)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()

            return OrgTypes.toTypedArray()
        }


    //------------------------------Newspaper---------------------------------------------------//
    val allNewspapers: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.NEWSPAPER_COL_NEWSPAPER_NAME + " FROM " + DBHelper.TABLE_NAME_NEWSPAPER + " ORDER BY " + DBHelper.NEWSPAPER_COL_NEWSPAPER_ID
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.NEWSPAPER_COL_NEWSPAPER_NAME)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }


    //------------------------------Looking_FOR---------------------------------------------------//
    val allAgeArrange: Array<String>
        get() {
            val OrgTypes = ArrayList<String>()
            try {
                dbHelper.openDataBase()
                val selectQuery = "SELECT " + DBHelper.AGERANGE_COL_AGE_RANGE + " FROM " + DBHelper.TABLE_NAME_AGERANGE
                Log.d("selectQuery", selectQuery)
                val cursor = dbHelper.getCursor(selectQuery)

                if (cursor != null && cursor.count > 0) {
                    cursor.moveToFirst()

                    for (i in 0 until cursor.count) {
                        OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.AGERANGE_COL_AGE_RANGE)))

                        cursor.moveToNext()
                    }
                }
                dbHelper.close()
            } catch (e: SQLException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return OrgTypes.toTypedArray()
        }

    init {
        val progressDialog = ProgressDialog(context)
        progressDialog.setCancelable(false)
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Initializing database..")
        dbHelper = DBHelper(context)
        try {
            dbHelper.crateDatabase()
        } catch (e: IOException) {
            e.printStackTrace()
            progressDialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        progressDialog.dismiss()
    }

    fun getJobRolesByJobCategory(ID: String): Array<String> {
        val OrgTypes = ArrayList<String>()
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT DISTINCT " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE + " FROM " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " WHERE " + DBHelper.JOB_ROLE_INFO_CAT_NAME + " = '" + ID + "' OR " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE + " = 'Test Name' ORDER BY " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE_ID
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OrgTypes.toTypedArray()
    }

    /*fun getModuleInformationByJobRole(jobRole: String, cat_name: String): ArrayList<ModuleInformationModel> {

        val moduleInformationModels = ArrayList<ModuleInformationModel>()
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT * FROM " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " WHERE " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE + " = '" + jobRole + "' and " + DBHelper.JOB_ROLE_INFO_CAT_NAME + " = '" + cat_name + "' ORDER BY " + DBHelper.JOB_ROLE_INFO_COL_MODULE_NAME
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                for (i in 0 until cursor.count) {
                    val moduleName = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_MODULE_NAME))
                    val moduleDescription = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_MODULE_DESCRIPTION))
                    val duration = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_DURATION))
                    val sampleLink = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_SAMPLE_LINK))

                    val moduleInformationModel = ModuleInformationModel()
                    moduleInformationModel.setModuleName(moduleName)
                    moduleInformationModel.setModuleDescription(moduleDescription)
                    moduleInformationModel.setDuration(duration)
                    moduleInformationModel.setSampleLink(sampleLink)

                    moduleInformationModels.add(moduleInformationModel)
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return moduleInformationModels
    }*/

    fun getJobRoleIDByJobRole(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE_ID + " FROM " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " WHERE " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getJobCategoryByJobRole(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.JOB_ROLE_INFO_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " WHERE " + DBHelper.JOB_ROLE_INFO_COL_JOB_ROLE + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.JOB_ROLE_INFO_CAT_NAME))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun setPriorityToCategory(categories: List<String>) {
        try {
            dbHelper.openDataBase()
            var selectQuery = ""
            if (categories.size == 1) {
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'B' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " in ('" + categories[0] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'F' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " not in ('0','" + categories[0] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
            } else if (categories.size == 2) {
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'B' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " in ('" + categories[0] + "','" + categories[1] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'F' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " not in ('0','" + categories[0] + "','" + categories[1] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
            } else if (categories.size == 3) {
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'B' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " in ('" + categories[0] + "','" + categories[1] + "','" + categories[2] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
                selectQuery = "update " + DBHelper.TABLE_NAME_JOB_ROLE_INFO + " set " + DBHelper.JOB_ROLE_INFO_COL_PRIORITY + " = 'F' where " + DBHelper.JOB_ROLE_INFO_COL_CATEGORY_ID + " not in ('0','" + categories[0] + "','" + categories[1] + "','" + categories[2] + "')"
                dbHelper.dataBase()!!.execSQL(selectQuery)
                Log.d("selectQuery", selectQuery)
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getOrgNameByID(ID: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.ORG_TYPES_COL_ORG_TYPE_NAME + " FROM " + DBHelper.TABLE_NAME_ORG_TYPES + " WHERE " + DBHelper.ORG_TYPES_COL_ORG_TYPE_ID + " = '" + ID + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.ORG_TYPES_COL_ORG_TYPE_NAME))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getOrgIDByOrgName(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.ORG_TYPES_COL_ORG_TYPE_ID + " FROM " + DBHelper.TABLE_NAME_ORG_TYPES + " WHERE " + DBHelper.ORG_TYPES_COL_ORG_TYPE_NAME + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.ORG_TYPES_COL_ORG_TYPE_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getEduLevelByID(ID: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDU_LEVELS_COL_EDU_LEVEL + " FROM " + DBHelper.TABLE_NAME_EDU_LEVELS + " WHERE " + DBHelper.EDU_LEVELS_COL_EDU_ID + " = '" + ID + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDU_LEVELS_COL_EDU_LEVEL))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getEduIDByEduLevel(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDU_LEVELS_COL_EDU_ID + " FROM " + DBHelper.TABLE_NAME_EDU_LEVELS + " WHERE " + DBHelper.EDU_LEVELS_COL_EDU_LEVEL + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDU_LEVELS_COL_EDU_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    //------------------------------------EDUCATION_TYPES---------------------------------------------//

    fun getEduTypesByEduLevelID(ID: String): Array<String> {
        val OrgTypes = ArrayList<String>()
        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDU_TYPES_COL_EDU_TYPES + " FROM " + DBHelper.TABLE_NAME_EDU_TYPES + " WHERE " + DBHelper.EDU_TYPES_COL_EDU_TYPES_TAG + " LIKE '%," + ID + ",%'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {
                OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.EDU_TYPES_COL_EDU_TYPES)))

                cursor.moveToNext()
            }
        }
        dbHelper.close()

        return OrgTypes.toTypedArray()
    }

    fun getEduTypeIDByEduType(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDU_TYPES_COL_EDU_TYPES_ID + " FROM " + DBHelper.TABLE_NAME_EDU_TYPES + " WHERE " + DBHelper.EDU_TYPES_COL_EDU_TYPES + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDU_TYPES_COL_EDU_TYPES_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getEduTypeByEduTypeID(id: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDU_TYPES_COL_EDU_TYPES + " FROM " + DBHelper.TABLE_NAME_EDU_TYPES + " WHERE " + DBHelper.EDU_TYPES_COL_EDU_TYPES_ID + " = '" + id + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDU_TYPES_COL_EDU_TYPES))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getWorkDisciplinesByCategoryID(catID: String): ArrayList<String> {
        val OrgTypes = ArrayList<String>()
        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_COL_SUB_NAME + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_SUB_TYPE + " = 'Work Area' AND " + DBHelper.SUB_CATEGORY_COL_CAT_ID + " = '" + catID + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {
                OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_COL_SUB_NAME)))

                cursor.moveToNext()
            }
        }
        dbHelper.close()

        return OrgTypes
    }

    fun getAllSkillsByCategoryID(categoryID: String): Array<String> {
        val OrgTypes = ArrayList<String>()
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_COL_SUB_NAME + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_SUB_TYPE + " = 'Skill' AND " + DBHelper.SUB_CATEGORY_COL_CAT_ID + " = '" + categoryID + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_COL_SUB_NAME)))

                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OrgTypes.toTypedArray()
    }

    fun getSkillIDBySkillType(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_SUB_CAT_ID + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_SUB_NAME + " = '" + name + "'"
            Log.d("selectQuery11", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_SUB_CAT_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getLocationIDByName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_ID + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_NAME + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getBanglaLocationIDByName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_ID + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getDependentLocationByParentName(locationName: String): Array<String> {
        val locationID = getLocationIDByName(locationName)
        val OrgTypes = ArrayList<String>()
        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_PARENT_ID + " ='" + locationID + "' AND " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_ID + " !='-1'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {
                OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME)))

                cursor.moveToNext()
            }
        }
        dbHelper.close()

        return OrgTypes.toTypedArray()

    }

    fun getDependentLocationByParentNameInBangla(locationName: String): Array<String> {
        val locationID = getBanglaLocationIDByName(locationName)
        var OrgTypes: ArrayList<String>? = null
        try {
            OrgTypes = ArrayList()
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_PARENT_ID + " ='" + locationID + "' AND " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_ID + " !='-1'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA)))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OrgTypes!!.toTypedArray()

    }

    fun getDependentPostOfficeByParentNameInBangla(locationName: String): Array<String> {
        val locationID = getBanglaLocationIDByName(locationName)
        val OrgTypes = ArrayList<String>()
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_LOCATIONS + " WHERE " + DBHelper.LOCATIONS_COL_LOCATION_PARENT_ID + " ='" + locationID + "' AND " + DBHelper.LOCATIONS_COL_LOCATION_OUTSIDE_BANGLADESH + " ='0' AND " + DBHelper.LOCATIONS_COL_LOCATION_ID + " !='-1' OR " + DBHelper.LOCATIONS_COL_LOCATION_ID + " ='-2'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.LOCATIONS_COL_LOCATION_NAME_BANGLA)))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OrgTypes.toTypedArray()

    }

    fun getCategoryIDByName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_ID + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_NAME + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getCategoryIDByBanglaName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_ID + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_NAME_BANGLA + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getCategoryNameByID(ID: String): String {

        var s = ""
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_ID + " = '" + ID + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_NAME))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getCategoryBanglaNameByID(ID: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.CATEGORY_COL_CAT_NAME_BANGLA + " FROM " + DBHelper.TABLE_NAME_CATEGORY + " WHERE " + DBHelper.CATEGORY_COL_CAT_ID + " = '" + ID + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.CATEGORY_COL_CAT_NAME_BANGLA))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getResultIDByResultName(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.RESULTS_COL_RESULT_ID + " FROM " + DBHelper.TABLE_NAME_RESULTS + " WHERE " + DBHelper.RESULTS_COL_RESULT + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.RESULTS_COL_RESULT_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getResultNameByResultID(ID: String): String {
        //RESULTS_COL_RESULT_ID

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.RESULTS_COL_RESULT + " FROM " + DBHelper.TABLE_NAME_RESULTS + " WHERE " + DBHelper.RESULTS_COL_RESULT_ID + " = '" + ID + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.RESULTS_COL_RESULT))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getGenderIDByGender(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.GENDER_COL_GENDER_ID + " FROM " + DBHelper.TABLE_NAME_GENDER + " WHERE " + DBHelper.GENDER_COL_GENDER + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.GENDER_COL_GENDER_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getMaritalIDByMaritalStatus(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.MARITAL_COL_MARITAL_ID + " FROM " + DBHelper.TABLE_NAME_MARITAL + " WHERE " + DBHelper.MARITAL_COL_MARITAL_STATUS + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.MARITAL_COL_MARITAL_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    fun getLookingIDByName(name: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.LOOKING_COL_LOOKING_ID + " FROM " + DBHelper.TABLE_NAME_LOOKING + " WHERE " + DBHelper.LOOKING_COL_LOOKING_FOR + " = '" + name + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.LOOKING_COL_LOOKING_ID))
            cursor.moveToNext()
        }
        dbHelper.close()
        return s
    }

    //------------------------------EducationDegrees----------------------------------------------//
    fun getEducationDegreesByEduLevelID(ID: String): Array<String> {
        val OrgTypes = ArrayList<String>()
        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME + " FROM " + DBHelper.TABLE_NAME_EDUCATION_DEGREES + " WHERE " + DBHelper.EDUCATION_DEGREES_COL_EDU_LEVEL + " = '" + ID + "' or " + DBHelper.EDUCATION_DEGREES_COL_EDU_LEVEL + " = '-1'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()

            for (i in 0 until cursor.count) {
                OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME)))

                cursor.moveToNext()
            }
        }
        dbHelper.close()

        return OrgTypes.toTypedArray()
    }

    fun isDegreeVerified(DegreeName: String): Boolean {
        var state = false
        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME + " FROM " + DBHelper.TABLE_NAME_EDUCATION_DEGREES + " WHERE " + DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME + " = '" + DegreeName + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME))
            cursor.moveToNext()
        }
        dbHelper.close()
        state = s.equals(DegreeName, ignoreCase = true)
        return state
    }

    fun getEducationTypeByEducationDegreeName(DegreeName: String): String {

        dbHelper.openDataBase()
        val selectQuery = "SELECT " + DBHelper.EDUCATION_DEGREES_COL_EDUCATION_TYPE + " FROM " + DBHelper.TABLE_NAME_EDUCATION_DEGREES + " WHERE " + DBHelper.EDUCATION_DEGREES_COL_DEGREE_NAME + " = '" + DegreeName + "'"
        Log.d("selectQuery", selectQuery)
        val cursor = dbHelper.getCursor(selectQuery)
        var s = ""

        if (cursor != null && cursor.count > 0) {
            cursor.moveToFirst()
            s = cursor.getString(cursor.getColumnIndex(DBHelper.EDUCATION_DEGREES_COL_EDUCATION_TYPE))
            cursor.moveToNext()
        }
        dbHelper.close()
        if (s == "" || s.matches("".toRegex())) {
            s = "0"
        }
        return s
    }

    //------------------------------BLUE_COLLAR_SUB_CATEGORY--------------------------------------------//

    fun getSubCategoriesByBlueCollarCategoryID(bluCollarCategoryID: String): Array<String> {
        val OrgTypes = ArrayList<String>()
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_COL_Sub_NAME_BNG + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_CAT_ID + " = '" + bluCollarCategoryID + "' AND " + DBHelper.SUB_CATEGORY_COL_SUB_TYPE + " = 'Skill'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()

                for (i in 0 until cursor.count) {
                    OrgTypes.add(i, cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_COL_Sub_NAME_BNG)))
                    cursor.moveToNext()
                }
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return OrgTypes.toTypedArray()
    }

    fun getBlueCollarSubCategoryIDByName(name: String): String? {

        dbHelper.openDataBase()
        var s: String? = null
        try {
            val selectQuery = "SELECT " + DBHelper.SUB_CATEGORY_SUB_CAT_ID + " FROM " + DBHelper.TABLE_NAME_SUB_CATEGORY + " WHERE " + DBHelper.SUB_CATEGORY_COL_Sub_NAME_BNG + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.SUB_CATEGORY_SUB_CAT_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getNewspaperIDByName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.NEWSPAPER_COL_NEWSPAPER_ID + " FROM " + DBHelper.TABLE_NAME_NEWSPAPER + " WHERE " + DBHelper.NEWSPAPER_COL_NEWSPAPER_NAME + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.NEWSPAPER_COL_NEWSPAPER_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getNewspaperNameById(ID: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.NEWSPAPER_COL_NEWSPAPER_NAME + " FROM " + DBHelper.TABLE_NAME_NEWSPAPER + " WHERE " + DBHelper.NEWSPAPER_COL_NEWSPAPER_ID + " = '" + ID + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.NEWSPAPER_COL_NEWSPAPER_NAME))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getAgeRangeIDByName(name: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.AGERANGE_COL_AGE_ID + " FROM " + DBHelper.TABLE_NAME_AGERANGE + " WHERE " + DBHelper.AGERANGE_COL_AGE_RANGE + " = '" + name + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.AGERANGE_COL_AGE_ID))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }

    fun getAgeRangeNameByID(ID: String): String? {

        var s: String? = null
        try {
            dbHelper.openDataBase()
            val selectQuery = "SELECT " + DBHelper.AGERANGE_COL_AGE_RANGE + " FROM " + DBHelper.TABLE_NAME_AGERANGE + " WHERE " + DBHelper.AGERANGE_COL_AGE_ID + " = '" + ID + "'"
            Log.d("selectQuery", selectQuery)
            val cursor = dbHelper.getCursor(selectQuery)
            s = ""

            if (cursor != null && cursor.count > 0) {
                cursor.moveToFirst()
                s = cursor.getString(cursor.getColumnIndex(DBHelper.AGERANGE_COL_AGE_RANGE))
                cursor.moveToNext()
            }
            dbHelper.close()
        } catch (e: SQLException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return s
    }
}