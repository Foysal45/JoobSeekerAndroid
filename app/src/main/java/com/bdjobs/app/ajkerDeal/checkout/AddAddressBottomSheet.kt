package com.bdjobs.app.ajkerDeal.checkout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.fragment.app.Fragment
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.checkout.location_dialog.LocationSelectionDialog
import com.bdjobs.app.ajkerDeal.checkout.location_dialog.LocationType
import com.bdjobs.app.ajkerDeal.checkout.model.CheckoutDataModel
import com.bdjobs.app.ajkerDeal.checkout.model.CheckoutUserData
import com.bdjobs.app.ajkerDeal.utilities.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlin.concurrent.thread

/**
 * A simple [Fragment] subclass.
 */
class AddAddressBottomSheet : BottomSheetDialogFragment() {

    private lateinit var parentLayout: LinearLayout
    private lateinit var districtSelect: LinearLayout
    private lateinit var thanaSelect: LinearLayout
    private lateinit var areaSelect: LinearLayout
    private lateinit var layoutPostOffice: LinearLayout

    private lateinit var districtSelectTV: TextView

    private lateinit var tv_gap0: TextView
    private lateinit var tv_gap1: TextView
    private lateinit var tv_gap2: TextView
    private lateinit var tv_gap3: TextView
    private lateinit var tv_gap4: TextView
    private lateinit var tv_gap5: TextView

    private lateinit var tv_zila: TextView
    private lateinit var tv_thana: TextView
    private lateinit var tv_post: TextView
    private lateinit var tv_thikana: TextView
    private lateinit var tv_mobile: TextView
    private lateinit var tv_alt_mobile: TextView

    private lateinit var thanaSelectTV: TextView
    private lateinit var areaSelectTV: TextView
    private lateinit var deliveryAddressET: EditText
    private lateinit var mobileET: EditText
    private lateinit var alternateMobileET: EditText
    private lateinit var saveBtn: Button

    // Data
    private var checkoutUserData: CheckoutUserData? = null
    private var districtId: Int = 0
    private var thanaId: Int = 0
    private var areaId: Int = 0
    private var thirdPartyLocationId: Int = 0
    private var isAreaAvailable: Boolean = false
    private var districtName: String = ""
    private var thanaName: String = ""
    private var areaName: String = ""
    private var postCode: String = ""
    private var uid: Long = 0
    private var flagArea: Int = 0
    private var checkoutDataModel: CheckoutDataModel? = null

    private lateinit var bottomSheet: FrameLayout

    var onFromSaved: ((uid: Long, districtId: Int, thanaId: Int, areaId: Int, address: String, mobile: String, alterMobile: String, districtName: String, thanaName: String, areaName: String, postCode: String, thirdPartyLocationId: Int) -> Unit)? =
        null

    companion object {
        @JvmStatic
        fun newInstance(): AddAddressBottomSheet = AddAddressBottomSheet().apply {

        }

        @JvmStatic
        fun newInstance(checkoutUserData: CheckoutUserData?, checkoutDataModel: CheckoutDataModel): AddAddressBottomSheet =
            AddAddressBottomSheet().apply {
                this.checkoutUserData = checkoutUserData
                this.checkoutDataModel = checkoutDataModel
            }

        @JvmField
        val tag = AddAddressBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_address_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val backBtn: ImageView = view.findViewById(R.id.back_btn)
        parentLayout = view.findViewById(R.id.parent_layout)
        districtSelect = view.findViewById(R.id.select_district)
        thanaSelect = view.findViewById(R.id.select_thana)
        areaSelect = view.findViewById(R.id.select_area)
        layoutPostOffice = view.findViewById(R.id.layout_post_office)

        tv_gap0 = view.findViewById(R.id.tv_gap0)
        tv_gap1 = view.findViewById(R.id.tv_gap1)
        tv_gap2 = view.findViewById(R.id.tv_gap2)
        tv_gap3 = view.findViewById(R.id.tv_gap3)
        tv_gap4 = view.findViewById(R.id.tv_gap4)
        tv_gap5 = view.findViewById(R.id.tv_gap5)

        tv_zila = view.findViewById(R.id.tv_zila)
        tv_thana = view.findViewById(R.id.tv_thana)
        tv_post = view.findViewById(R.id.tv_post)
        tv_thikana = view.findViewById(R.id.tv_thikana)
        tv_mobile = view.findViewById(R.id.tv_mobile)
        tv_alt_mobile = view.findViewById(R.id.tv_alt_mobile)

        districtSelectTV = view.findViewById(R.id.select_district_tv)
        thanaSelectTV = view.findViewById(R.id.select_thana_tv)
        areaSelectTV = view.findViewById(R.id.select_area_tv)
        deliveryAddressET = view.findViewById(R.id.deliveryAddress_et)
        mobileET = view.findViewById(R.id.mobile_et)
        alternateMobileET = view.findViewById(R.id.alternateMobile_et)
        saveBtn = view.findViewById(R.id.save_btn)

        /* barikoyInterface = RetrofitSingleton.getInstance(getApplicationContext(), "barikoi")
             .create(barikoyInterface::class.java)*/

        //countryList = ArrayList()
        //autoCompleteAddressAdapter = AutoCompleteAddressAdapter(getApplicationContext(), countryList)

        checkoutUserData?.let { model ->
            districtId = model.districtId
            thanaId = model.thanaId
            areaId = model.areaId
            districtName = model.districtName
            thanaName = model.thanaName
            isAreaAvailable = model.areaId != 0
            if (model.areaId > 0) {
                layoutPostOffice.visibility = View.VISIBLE
                tv_gap2.visibility = View.VISIBLE
                //BottomSheetBehavior.from(bottomSheet)?.state = BottomSheetBehavior.STATE_EXPANDED
            } else {
                layoutPostOffice.visibility = View.GONE
                tv_gap2.visibility = View.GONE
            }
            deliveryAddressET.setText(model.billingAddress)
            mobileET.setText(model.mobile)
            alternateMobileET.setText(model.alternateMobile)
            districtSelectTV.text = model.districtName
            thanaSelectTV.text = model.thanaName
            areaSelectTV.text = model.areaName
            uid = model.uid
        }


        districtSelect.setOnClickListener {
            goToLocationDialog(LocationType.District)
        }
        thanaSelect.setOnClickListener {
            if (districtId != 0) {
                goToLocationDialog(LocationType.Thana, districtId)


            } else {
                showShortToast(context, getString(R.string.select_district))
            }
        }
        areaSelect.setOnClickListener {
            if (districtId != 0 && thanaId != 0) {
                if (isAreaAvailable) {
                    goToLocationDialog(LocationType.Area, districtId, thanaId)
                } else {
                    showShortToast(context, getString(R.string.no_aria))
                    /*layoutPostOffice.visibility = View.GONE
                    tv_gap2.visibility = View.GONE*/
                }
            } else {
                showShortToast(context, getString(R.string.select_thana))
            }

        }

        backBtn.setOnClickListener {
            hideKeyboard()
            dismiss()
        }

        saveBtn.setOnClickListener {

            hideKeyboard()

            if (!validate()) {
                return@setOnClickListener
            }
            val address = deliveryAddressET.text.toString()
            val mobile = mobileET.text.toString()
            val alternateMobile = alternateMobileET.text.toString()

            onFromSaved?.invoke(
                uid,
                districtId,
                thanaId,
                areaId,
                address,
                mobile,
                alternateMobile,
                districtName,
                thanaName,
                areaName,
                postCode,
                thirdPartyLocationId
            )
            dismiss()
        }

        alternateMobileET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                val alternateMobile = alternateMobileET.text.toString()

                if (alternateMobile.isNotEmpty() && !com.bdjobs.app.ajkerDeal.utilities.Validator.isValidMobileNumber(alternateMobile)) {
                    showShortToast(
                        context,
                        getString(R.string.write_proper_alternative_phone_number)
                    )
                    true
                } else {
                    hideKeyboard()
                    false
                }

            } else {
                false
            }
        }

        tv_gap0.setOnClickListener() {
            hideKeyboard()
        }

        tv_gap1.setOnClickListener() {
            hideKeyboard()
        }

        tv_gap2.setOnClickListener() {
            hideKeyboard()
        }

        tv_gap3.setOnClickListener() {
            hideKeyboard()
        }

        tv_gap4.setOnClickListener() {
            hideKeyboard()
        }

        tv_gap5.setOnClickListener() {
            hideKeyboard()
        }

        tv_zila.setOnClickListener() {
            hideKeyboard()
        }

        tv_thana.setOnClickListener() {
            hideKeyboard()
        }

        tv_post.setOnClickListener() {
            hideKeyboard()
        }

        tv_thikana.setOnClickListener() {
            hideKeyboard()
        }

        tv_mobile.setOnClickListener() {
            hideKeyboard()
        }

        tv_alt_mobile.setOnClickListener() {
            hideKeyboard()
        }
        parentLayout.setOnClickListener() {
            hideKeyboard()
        }


        deliveryAddressET.setOnEditorActionListener { v, actionId, event ->


            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                val address = deliveryAddressET.text.toString()
                if (address.length < 15) {
                    showShortToast(context, getString(R.string.details_address))

                    true

                } else {

                    false
                }


            } else {
                false
            }
        }


        mobileET.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {

                val mobile = mobileET.text.toString()
                if (!com.bdjobs.app.ajkerDeal.utilities.Validator.isValidMobileNumber(mobile)) {
                    showShortToast(context, getString(R.string.write_proper_phone_number))
                    true
                } else {

                    false
                }


            } else {
                false
            }
        }

      /*  deliveryAddressET.addTextChangedListener(object : TextWatcher {
            // var isBackspaceClicked = false

            override fun afterTextChanged(s: Editable) {

                val name = s.toString()
                //  if (!isBackspaceClicked) {
                if (name != null && !name.isEmpty() && name != "null") {
                    search_data(s.toString())
                }
                *//*  } else { // Your "backspace" handling
                  }*//*
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

                *//*  isBackspaceClicked = if (after < count) {
                      true
                  } else {
                      false
                  }*//*
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })*/

    }


    private fun validate(): Boolean {

        val address = deliveryAddressET.text.toString()
        val mobile = mobileET.text.toString()
        val alternateMobile = alternateMobileET.text.toString()

        if (districtId == 0) {
            showShortToast(context, getString(R.string.select_district))
            return false
        }
        if (thanaId == 0) {
            showShortToast(context, getString(R.string.select_thana))
            return false
        }
        if (isAreaAvailable && areaId == 0) {
            layoutPostOffice.visibility = View.VISIBLE
            tv_gap2.visibility = View.VISIBLE
            showShortToast(context, getString(R.string.select_aria))
            return false
        }
        if (address.isEmpty() || address.length < 15) {
            showShortToast(context, getString(R.string.details_address))
            return false
        }
        if (mobile.isEmpty()) {
            showShortToast(context, getString(R.string.write_phone_number))
            return false
        }
        if (alternateMobile.isEmpty()) {
            showShortToast(context, getString(R.string.write_alternative_phone_number))
            return false
        }
        if (!com.bdjobs.app.ajkerDeal.utilities.Validator.isValidMobileNumber(mobile)) {
            showShortToast(context, getString(R.string.write_proper_phone_number))
            return false
        }
//        if(mobile.isEmpty() && mobile.length < 11){
//            showShortToast(context, getString(R.string.write_proper_phone_number))
//            return false
//        }
        if (alternateMobile.isNotEmpty() && !com.bdjobs.app.ajkerDeal.utilities.Validator.isValidMobileNumber(alternateMobile)) {
            showShortToast(context, getString(R.string.write_proper_alternative_phone_number))
            return false
        }
//        if(alternateMobile.isEmpty() && alternateMobile.length < 11){
//            showShortToast(context, getString(R.string.write_proper_alternative_phone_number))
//            return false
//        }

        return true
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)

        bottomSheet = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        val metrics = resources.displayMetrics


        BottomSheetBehavior.from(bottomSheet)?.state = BottomSheetBehavior.STATE_COLLAPSED
        //BottomSheetBehavior.from(bottomSheet)?.peekHeight = metrics.heightPixels / 2
        thread {
            activity?.runOnUiThread {
                val dynamicHeight = parentLayout.height
                BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
            }
        }

        BottomSheetBehavior.from(bottomSheet)?.skipCollapsed = true
        BottomSheetBehavior.from(bottomSheet)?.isHideable = false
        BottomSheetBehavior.from(bottomSheet)
            ?.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    if (isAdded) {

                    }
                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet)?.state =
                            BottomSheetBehavior.STATE_EXPANDED
                        BottomSheetBehavior.STATE_DRAGGING
                    }
                }
            })

    }

/*    private fun search_data(s: String) {

        barikoyInterface.getlocationj(s).enqueue(object : Callback<DataModelBariKoi> {
            override fun onResponse(
                call: Call<DataModelBariKoi>,
                response: Response<DataModelBariKoi>
            ) {
                if (response.isSuccessful) {
                    if (response.body() != null) {

                        val dataModel = response.body()
                        if (dataModel!!.places != null) {
                            countryList = ArrayList<Place>(dataModel!!.places.asList())

                            autoCompleteAddressAdapter = AutoCompleteAddressAdapter(getApplicationContext(), countryList)
                            deliveryAddressET.setAdapter(autoCompleteAddressAdapter)
                        } else {

                        }
                    } else {
                    }
                } else {
                }
            }

            override fun onFailure(call: Call<DataModelBariKoi>, t: Throwable) {
                //  Log.d("fail", "" + t.toString())
            }
        })
    }*/

    private fun goToLocationDialog(
        locationType: LocationType,
        districtId: Int = 0,
        thanaId: Int = 0
    ) {

        val dialog = LocationSelectionDialog.newInstance(locationType, districtId, thanaId, checkoutDataModel?.merchantId ?: 0, checkoutDataModel?.isDistrictWiseProduct ?: 0)
        val tag = LocationSelectionDialog.tag
        dialog.show(childFragmentManager, tag)

        dialog.onDistrictPicked = { districtModel ->
            this.districtId = districtModel?.districtId ?: 0
            this.districtName = districtModel?.districtBng ?: ""
            districtSelectTV.text = districtName
            this.thanaId = 0
            this.areaId = 0
            thanaSelectTV.text = "থানা নির্বাচন করুন"
            areaSelectTV.text = "নির্বাচন করুন"
        }

        dialog.onThanaPicked = { thanaModel, isAreaAvailable ->
            this.thanaId = thanaModel?.thanaId ?: 0
            this.thanaName = thanaModel?.thanaBng ?: ""
            this.thirdPartyLocationId = thanaModel?.thirdPartyLocationId ?: 0
            thanaSelectTV.text = thanaName
            this.isAreaAvailable = isAreaAvailable
            if (!isAreaAvailable) {
                //areaSelectTV.text = getString(R.string.no_aria)
                layoutPostOffice.visibility = View.GONE
                tv_gap2.visibility = View.GONE
            } else {
                layoutPostOffice.visibility = View.VISIBLE
                tv_gap2.visibility = View.VISIBLE
                areaSelectTV.text = "নির্বাচন করুন"
                BottomSheetBehavior.from(bottomSheet)?.state = BottomSheetBehavior.STATE_EXPANDED
            }
            this.areaId = 0
            this.areaName = ""
            this.postCode = thanaModel?.postalCode ?: "0"
        }

        dialog.onAreaPicked = { areaModel ->
            //this.areaId = areaModel?.areaId ?: 0
            //this.areaName = areaModel?.areaBng ?: ""
            this.areaId = areaModel?.thanaId ?: 0
            this.areaName = areaModel?.thanaBng ?: ""
            this.postCode = areaModel?.postalCode ?: ""
            areaSelectTV.text = areaName
            /*  if (areaModel!!.isBlocked == 0){
                  //Toast.makeText(context, "এই এরিয়াতে সাময়িকভাবে ডেলিভারি দেয়া বন্ধ আছে। আপনি চাইলে " + thanaName + " থেকে প্রোডাক্ট কালেক্ট করতে পারেন", Toast.LENGTH_LONG).show()
                  areaSelectTV.text = areaName

              } else {
                  areaSelectTV.text = areaName
              }*/
        }

    }


    // show short toast method
    private fun showShortToast(context: Context?, message: String) {
        if (context != null) {
            val toast = Toast.makeText(context, message, Toast.LENGTH_LONG)
            //toast.setGravity(Gravity.BOTTOM, 0, 0)
            toast.show()
        }
    }
}




