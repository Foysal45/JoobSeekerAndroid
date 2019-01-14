package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.editResume.adapters.models.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: DataItem)
    fun passArmyData(data: ArmydataItem)
    fun passAreaOfExpsData(data: AreaofExperienceItem)
    fun getAreaOfExpsData(): AreaofExperienceItem
    fun validateField(et: TextInputEditText, til: TextInputLayout): Boolean
    fun getData(): DataItem
    fun getArmyData(): ArmydataItem
    fun goBack()
}

interface EduInfo {
    fun setTitle(tit: String?)
    fun dataStorage(): DataStorage
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: AcaDataItem)
    fun passTrainingData(data: Tr_DataItem)
    fun getData(): AcaDataItem
    fun getTrainingData(): Tr_DataItem
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun goBack()
}

interface PersonalInfo {
    fun setTitle(tit: String?)
    fun setEditButton(b: Boolean, type: String)
    fun getCareerData(): Ca_DataItem
    fun passCareerData(data: Ca_DataItem)
    fun getPersonalData(): P_DataItem
    fun passPersonalData(data: P_DataItem)
    fun getContactData(): C_DataItem
    fun passContactData(data: C_DataItem)
    fun validateField(et: TextInputEditText, til: TextInputLayout): Boolean
    //fun setDeleteButton(b: Boolean)
    //fun goToEditInfo(check: String)
    fun goBack()
}

interface TitleChange {
    fun setTitle(tit: String?)
}