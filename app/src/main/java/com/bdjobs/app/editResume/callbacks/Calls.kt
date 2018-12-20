package com.bdjobs.app.editResume.callbacks

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun editInfo()
}

interface TitleChange {
    fun setTitle(tit: String?)
}