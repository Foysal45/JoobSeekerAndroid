package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.editResume.adapters.models.DataItem

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: DataItem)
    fun getData(): DataItem
    fun goBack()
}

interface TitleChange {
    fun setTitle(tit: String?)
}