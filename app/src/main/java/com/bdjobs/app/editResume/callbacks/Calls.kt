package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.editResume.adapters.models.ArmydataItem
import com.bdjobs.app.editResume.adapters.models.DataItem

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

interface TitleChange {
    fun setTitle(tit: String?)
}