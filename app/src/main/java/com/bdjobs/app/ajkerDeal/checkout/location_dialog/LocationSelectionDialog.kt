package com.bdjobs.app.ajkerDeal.checkout.location_dialog


import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.endpoints.LocationInterface
import com.bdjobs.app.ajkerDeal.api.models.location.DistrictInfoModel
import com.bdjobs.app.ajkerDeal.api.models.location.LocationResponse
import com.bdjobs.app.ajkerDeal.api.models.location.ThanaInfoModel
import com.bdjobs.app.ajkerDeal.utilities.hideKeyboard
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import java.util.*

enum class LocationType {
    District, Thana, Area
}

class LocationSelectionDialog : BottomSheetDialogFragment() {

    private lateinit var crossBtn: ImageView
    private lateinit var searchEditText: EditText
    private lateinit var searchBtn: ImageView
    private lateinit var placeListRV: RecyclerView
    private var progressBar: ProgressBar? = null
    private lateinit var extraSpace: View


    private lateinit var locationInterface: LocationInterface
    private var handler = Handler()
    private var workRunnable: Runnable? = null

    private var districtList: MutableList<DistrictInfoModel> = mutableListOf()
    private var districtListCopy: MutableList<DistrictInfoModel> = mutableListOf()
    private var thanaList: MutableList<ThanaInfoModel> = mutableListOf()
    private var thanaListCopy: MutableList<ThanaInfoModel> = mutableListOf()
    private var areaList: MutableList<ThanaInfoModel> = mutableListOf()
    private var areaListCopy: MutableList<ThanaInfoModel> = mutableListOf()

    private var locationType: LocationType =
        LocationType.District
    private var districtId: Int = 0
    private var thanaId: Int = 0
    private var areaId: Int = 0
    private var merchantId: Int = 0
    private var isSpecialDistrictMerchant: Int = 0

    var onDistrictPicked: ((districtModel: DistrictInfoModel?) -> Unit)? = null
    var onThanaPicked: ((thanaModel: ThanaInfoModel?, isAreaAvailable: Boolean) -> Unit)? = null
    var onAreaPicked: ((areaModel: ThanaInfoModel?) -> Unit)? = null
    //var onAreaPicked: ((areaModel: AreaInfoModel?) -> Unit)? = null

    companion object {

        @JvmStatic
        fun newInstance(locationType: LocationType, districtId: Int = 0, thanaId: Int = 0, merchantId: Int = 0, isSpecialDistrictMerchant: Int = 0): LocationSelectionDialog =
            LocationSelectionDialog().apply {
                this.locationType = locationType
                this.districtId = districtId
                this.thanaId = thanaId
                this.merchantId = merchantId
                this.isSpecialDistrictMerchant = isSpecialDistrictMerchant
            }

        @JvmField
        val tag = LocationSelectionDialog::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogThemeTransparent)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_location_selection_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        crossBtn = view.findViewById(R.id.cross_btn)
        searchEditText = view.findViewById(R.id.search_edit_text)
        searchBtn = view.findViewById(R.id.search_btn)
        placeListRV = view.findViewById(R.id.place_list_rv)
        progressBar = view.findViewById(R.id.progress_bar)
        extraSpace = view.findViewById(R.id.extraSpace)

        locationInterface = com.bdjobs.app.ajkerDeal.api.RetrofitSingleton.getInstance(context, "apiBase").create(
            LocationInterface::class.java)

        when (locationType) {

            LocationType.District -> {

                val locationAdapter =
                    LocationDistrictAdapter(
                        districtList
                    )
                with(placeListRV) {
                    layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    adapter = locationAdapter
                }
                locationAdapter.onItemClicked = { _, model ->
                    hideKeyboard()
                    onDistrictPicked?.invoke(model)
                    dismiss()
                }
                loadDistrict(districtId)
            }
            LocationType.Thana -> {
                val locationAdapter = LocationThanaAdapter(thanaList)
                with(placeListRV) {
                    layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    adapter = locationAdapter
                }
                locationAdapter.onItemClicked = { _, model ->
                    hideKeyboard()
                    onThanaPicked?.invoke(model, model?.hasArea == 1)
                    dismiss()
                }
                loadThana(districtId)
            }
            LocationType.Area -> {
                val locationAdapter = LocationAreaAdapter(areaList)
                with(placeListRV) {
                    layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                    adapter = locationAdapter
                }
                locationAdapter.onItemClicked = { _, model ->
                    hideKeyboard()
                    onAreaPicked?.invoke(model)
                    dismiss()
                }
                loadArea(thanaId, districtId)
            }
        }


        manageSearch()

        crossBtn.setOnClickListener {
            hideKeyboard()
            dismiss()
        }
    }

    private fun manageSearch() {

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                workRunnable?.let {
                    handler.removeCallbacks(it)
                }
                workRunnable = Runnable {
                    val searchKey = p0.toString()
                    search(searchKey)
                }
                handler.postDelayed(workRunnable!!, 500L)
            }

        })

        searchBtn.setOnClickListener {
            hideKeyboard()
            val searchKey = searchEditText.text.toString()
            search(searchKey)
        }
    }

    private fun search(searchKey: String) {

        progressBar?.visibility = View.VISIBLE
        when (locationType) {

            LocationType.District -> {


                if (districtListCopy.isEmpty()) {
                    districtListCopy.clear()
                    districtListCopy.addAll(districtList)
                }
                if (searchKey.isEmpty()) {
                    (placeListRV.adapter as LocationDistrictAdapter).setDataList(districtListCopy)
                    progressBar?.visibility = View.GONE
                    return
                }
                val filteredList = districtListCopy.filter { model ->
                    (model.district.toLowerCase(Locale.US).contains(searchKey.toLowerCase(Locale.US))) || (model.districtBng.contains(
                        searchKey
                    ))
                }
                (placeListRV.adapter as LocationDistrictAdapter).setDataList(filteredList)

            }
            LocationType.Thana -> {

                //progressBar?.visibility = View.VISIBLE
                if (thanaListCopy.isEmpty()) {
                    thanaListCopy.clear()
                    thanaListCopy.addAll(thanaList)
                }
                if (searchKey.isEmpty()) {
                    (placeListRV.adapter as LocationThanaAdapter).setDataList(thanaList)
                    progressBar?.visibility = View.GONE
                    return
                }
                val filteredList = thanaListCopy.filter { model ->
                    (model.thana.toLowerCase(Locale.US).contains(searchKey.toLowerCase(Locale.US))) || (model.thanaBng.contains(
                        searchKey
                    ))
                }
                (placeListRV.adapter as LocationThanaAdapter).setDataList(filteredList)
            }
            LocationType.Area -> {
                if (areaListCopy.isEmpty()) {
                    areaListCopy.clear()
                    areaListCopy.addAll(areaList)
                }
                if (searchKey.isEmpty()) {
                    (placeListRV.adapter as LocationAreaAdapter).setDataList(areaList)
                    progressBar?.visibility = View.GONE
                    return
                }
                val filteredList = areaListCopy.filter { model ->
                    (model.thana.toLowerCase(Locale.US).contains(searchKey.toLowerCase(Locale.US))) || (model.thanaBng.contains(
                        searchKey
                    ))
                }
                (placeListRV.adapter as LocationAreaAdapter).setDataList(filteredList)
            }
        }
        progressBar?.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        val dialog = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(false)
        val bottomSheet: FrameLayout = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)!!
        val metrics = resources.displayMetrics
        BottomSheetBehavior.from(bottomSheet).peekHeight = metrics.heightPixels / 2
        BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
        BottomSheetBehavior.from(bottomSheet).skipCollapsed = true
        BottomSheetBehavior.from(bottomSheet).isHideable = false
        BottomSheetBehavior.from(bottomSheet)
            .setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {
                    BottomSheetBehavior.from(bottomSheet).state =
                        BottomSheetBehavior.STATE_EXPANDED
                }

                override fun onStateChanged(p0: View, p1: Int) {
                    if (p1 == BottomSheetBehavior.STATE_DRAGGING) {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }
                }
            })

        extraSpace.minimumHeight = (metrics.heightPixels)
    }

    private fun loadDistrict(districtId: Int) {

        progressBar?.visibility = View.VISIBLE
        val call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> = if (isSpecialDistrictMerchant == 1) {
            locationInterface.getDistrictList(merchantId, districtId, thanaId)
        } else {
            locationInterface.getDistrictList(districtId)
        }
        call.enqueue(object : Callback<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> {

            override fun onResponse(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, response: Response<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>) {
                progressBar?.visibility = View.GONE
                if (response.isSuccessful && response.body() != null && isAdded) {

                    districtList.clear()
                    districtList.addAll(response.body()!!.data.districtInfo)
                    placeListRV.adapter?.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, t: Throwable) {
                Timber.d("onFailure ${t.message}")
                progressBar?.visibility = View.GONE
            }
        })
    }

    private fun loadThana(districtId: Int) {

        progressBar?.visibility = View.VISIBLE
        val call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> = if (isSpecialDistrictMerchant == 1) {
            locationInterface.getDistrictList(merchantId, districtId, thanaId)
        } else {
            locationInterface.getDistrictList(districtId)
        }
        call.enqueue(object : Callback<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> {

            override fun onResponse(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, response: Response<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>) {
                progressBar?.visibility = View.GONE
                if (response.isSuccessful && response.body() != null && isAdded) {

                    if (!response.body()!!.data.districtInfo.isNullOrEmpty()) {
                        thanaList.clear()
                        thanaList.addAll(response.body()!!.data.districtInfo[0].thanaHome)
                        placeListRV.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, t: Throwable) {
                Timber.d("onFailure ${t.message}")
                progressBar?.visibility = View.GONE
            }
        })

    }

    private fun loadArea(thanaID: Int, districtId: Int, isCourier: Int = 0) {

        progressBar?.visibility = View.VISIBLE
        val call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> = if (isSpecialDistrictMerchant == 1) {
            locationInterface.getDistrictList(merchantId, districtId, thanaID)
        } else {
            locationInterface.getDistrictList(thanaID)
        }
        //locationInterface.getThanaOrAreaList(thanaID, districtId, isCourier)
        call.enqueue(object : Callback<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>> {

            override fun onResponse(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, response: Response<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>) {
                progressBar?.visibility = View.GONE
                if (response.isSuccessful && response.body() != null && isAdded) {

                    if (!response.body()!!.data.districtInfo.isNullOrEmpty()) {
                        //  Timber.d("Area List - " + response.body()!!.data.area[0].isBlocked + " " + response.body())
                        areaList.clear()
                        areaList.addAll(response.body()!!.data.districtInfo[0].thanaHome)
                        placeListRV.adapter?.notifyDataSetChanged()
                    }
                }
            }

            override fun onFailure(call: Call<com.bdjobs.app.ajkerDeal.api.models.checkout.ResponseGenericModel<LocationResponse>>, t: Throwable) {
                Timber.d("onFailure ${t.message}")
                progressBar?.visibility = View.GONE
            }
        })

    }
}
