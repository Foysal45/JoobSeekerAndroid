package com.bdjobs.app.editResume.callbacks

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun editInfo()
}

interface TitleChange {
    fun setTitle(tit: String?)
}