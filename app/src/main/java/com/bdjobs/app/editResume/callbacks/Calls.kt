package com.bdjobs.app.editResume.callbacks

import com.bdjobs.app.API.ModelClasses.AddExpModel
import com.bdjobs.app.Databases.External.DataStorage
import com.bdjobs.app.editResume.adapters.models.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

interface EmpHisCB {
    fun setTitle(tit: String?)
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passData(data: DataItem)
    fun passArmyData(data: ArmydataItem?)
    fun passAreaOfExpsData(data: AreaofExperienceItem)
    fun getAreaOfExpsData(): AreaofExperienceItem
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun getData(): DataItem
    fun getArmyData(): ArmydataItem
    fun goBack()
    fun getBackFrom(): String?
    fun setBackFrom(from: String?)

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
    fun getData(): AcaDataItem
    fun setAcademicList(aca: ArrayList<AcaDataItem>)
    fun setTrainingList(aca: ArrayList<Tr_DataItem>)
    fun setProfessionalList(aca: ArrayList<ProfessionalDataModel>)
    fun getAcademicList(): ArrayList<AcaDataItem>?
    fun getTrainingList(): ArrayList<Tr_DataItem>?
    fun getProfessionalList(): ArrayList<ProfessionalDataModel>?
    fun passTrainingData(data: Tr_DataItem)
    fun getTrainingData(): Tr_DataItem
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun goBack()
    fun passProfessionalData(Data: ProfessionalDataModel)
    fun getProfessionalData(): ProfessionalDataModel

    fun saveButtonClickStatus(value:Boolean)
    fun getClickStatus():Boolean
    fun getBackFrom(): String?
    fun setBackFrom(from: String?)
}


interface OtherInfo {
    fun setTitle(tit: String?)
    fun dataStorage(): DataStorage
    fun setDeleteButton(b: Boolean)
    fun goToEditInfo(check: String)
    fun passLanguageData(data: LanguageDataModel)
    fun passSpecializationDataNew(data: ArrayList<AddExpModel>?,SkillDes : String, extraCuricular :String)
    fun passSpecializationData(data: SpecializationDataModel)
    fun passReferenceData(data: ReferenceDataModel)
    fun getReferenceData(): ReferenceDataModel
    fun getLanguageData(): LanguageDataModel
    fun validateField(char: String, et: TextInputEditText, til: TextInputLayout): Boolean
    fun goBack()
    fun getBackFrom(): String?
    fun setBackFrom(from: String?)
    fun setEditButton(b: Boolean)
    fun getSpecializationDataNew(): ArrayList<AddExpModel>?
    fun getSpecializationData(): SpecializationDataModel
    fun setItemClick(position: Int)
    fun getItemClick():Int
    fun showEditDialog(item : Skill?)
    fun getSkillDes():String?
    fun getExtraCuri():String?
    fun confirmationPopup(deleteItem:String)

    //added by rakib
    fun getLanguageList():ArrayList<LanguageDataModel>?
    fun setLanguageList(lan : ArrayList<LanguageDataModel>)

    fun getReferenceList():ArrayList<ReferenceDataModel>?
    fun setReferenceList(ref: ArrayList<ReferenceDataModel>)

    fun setExtraCurricularActivity(extra : String)
    fun getExtraCurricularActivity():String?

    fun setSkillDescription(desc : String)
    fun getSkillDescription() : String?

    fun setSkills(skills : ArrayList<Skill?>)
    fun getSkills() : ArrayList<Skill?>?


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
    fun getBackFrom(): String?
    fun setBackFrom(from: String?)
    fun setPresentDistrict(district : String?)
    fun getPresentDistrict() : String?
    fun setPermanentDistrict(district: String?)
    fun getPermanentDistrict() : String?
}

interface TitleChange {
    fun setTitle(tit: String?)
}