package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.editResume.adapters.models.AcaDataItem
import com.bdjobs.app.editResume.adapters.models.ArmydataItem
import com.bdjobs.app.editResume.adapters.models.DataItem
import com.bdjobs.app.editResume.adapters.models.Tr_DataItem

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: DataItem)
    fun passArmyData(data: ArmydataItem)
    fun getData(): DataItem
    fun getArmyData(): ArmydataItem
    fun goBack()
}

interface EduInfo {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: AcaDataItem)
    fun passTrainingData(data: Tr_DataItem)
    fun getData(): AcaDataItem
    fun getTrainingData(): Tr_DataItem
    fun goBack()
}

interface PersonalInfo {
    fun setTitle(tit: String?)
    fun setEditButton(b: Boolean)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun goBack()
}

interface TitleChange {
    fun setTitle(tit: String?)
}