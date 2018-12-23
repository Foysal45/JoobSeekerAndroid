package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.editResume.adapters.models.DataItem

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun saveInfo(data: DataItem)
}

interface TitleChange {
    fun setTitle(tit: String?)
}