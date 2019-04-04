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
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun getData(): DataItem
    fun getArmyData(): ArmydataItem
    fun goBack()

    fun setExpIDs(idArr: ArrayList<String>)
    fun getExpIDs(): ArrayList<String>

    fun setIsFirst(b: Boolean)
    fun getIsFirst(): Boolean

    fun saveExpsArray(exps: ArrayList<DataItem>?)
    fun getExpsArray(): ArrayList<DataItem>

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
    fun passProfessionalData(Data: ProfessionalDataModel)
    fun getProfessionalData(): ProfessionalDataModel
    fun saveButtonClickStatus(value:Boolean)
    fun getClickStatus():Boolean
}


interface OtherInfo {
    fun setTitle(tit: String?)
    fun dataStorage(): DataStorage
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passLanguageData(data: LanguageDataModel)
    fun passSpacializationData(data: SpecializationDataModel)
    fun passReferenceData(data: ReferenceDataModel)
    fun getReferenceData(): ReferenceDataModel
    fun getLanguageData(): LanguageDataModel
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun goBack()
    fun setEditButton(b: Boolean)
    fun getSpecializationData(): SpecializationDataModel


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
    fun getOriData(): ORIdataItem
    fun passOriData(data: ORIdataItem)
    fun getPrefAreasData(): PreferredAreasData
    fun passPrefAreasData(data: PreferredAreasData)
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun setThana(thana: String?)
    fun getThana(): String?
    fun setPostOffice(po: String?)
    fun getPostOffice(): String?
    fun setPmThana(thana: String?)
    fun getPmThana(): String?
    /*fun setDistrict(po: String?)
    fun getDistrict(): String?
    fun setPmDistrict(thana: String?)
    fun getPmDistrict(): String?*/
    fun setPmPostOffice(po: String?)
    fun getPmPostOffice(): String?
    fun goBack()
}

interface TitleChange {
    fun setTitle(tit: String?)
}