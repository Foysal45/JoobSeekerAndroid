package com.bdjobs.app.ajkerDeal.ui.checkout_live

import android.content.pm.PackageInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.checkout_live.LiveOrderRequest
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.ajkerDeal.api.models.order.DeliveryChargesModel
import com.bdjobs.app.ajkerDeal.api.models.order.DeliveryInfoModel
import com.bdjobs.app.ajkerDeal.api.models.order.PaymentMediumModel
import com.bdjobs.app.ajkerDeal.checkout.AddAddressBottomSheet
import com.bdjobs.app.ajkerDeal.checkout.DeliveryAddressAdapter
import com.bdjobs.app.ajkerDeal.checkout.PaymentMediumAdapter
import com.bdjobs.app.ajkerDeal.checkout.model.CheckoutDataModel
import com.bdjobs.app.ajkerDeal.checkout.model.CheckoutUserData
import com.bdjobs.app.ajkerDeal.ui.video_shopping.live_cart.LiveCartAdapter
import com.bdjobs.app.ajkerDeal.utilities.*
import com.bdjobs.app.databinding.FragmentCheckoutBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.lang.reflect.Type

class CheckoutFragment: Fragment() {

    private var binding: FragmentCheckoutBinding? = null

    private lateinit var deliveryAddressAdapter: DeliveryAddressAdapter
    private var paymentMediumAdapter: PaymentMediumAdapter? = null

    private val deliveryAddressList: MutableList<CheckoutUserData> = mutableListOf()
    private var listType: Type = object : TypeToken<MutableList<CheckoutUserData>>() {}.type

    //private lateinit var productModel: LiveProductData
    private var cartProductList: MutableList<LiveProductData> = mutableListOf()
    private var isDeliveryAddressSelected: Boolean = false
    private var checkoutUserData: CheckoutUserData? = null
    private var isProcessingOrder: Boolean = false

    private var merchantId: Int = 0
    private var paymentMode = "both"
    private var channelType = ""
    private var orderPlaceFlag = 0
    private var deliveryCharge: Int = 0
    private var grandTotal: Int = 0
    private var totalProductPrice: Int = 0
    private var appVersion: String = ""
    private var isPaymentMediumSelected: Boolean = false
    private var cardType = ""
    private var paymentType = ""
    private var paymentStatus = "I"

    private lateinit var sessionManager: SessionManager
    private var deliveryChargesModel = DeliveryChargesModel()

    private val gson: Gson by inject()
    private val viewModel: CheckoutLiveViewModel by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentCheckoutBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cartProductList = activity?.intent?.getParcelableArrayListExtra("productModelList") ?: mutableListOf()
        merchantId = activity?.intent?.getIntExtra("merchantId", 0) ?: 0
        paymentMode = activity?.intent?.getStringExtra("paymentMode") ?: "both"
        channelType = activity?.intent?.getStringExtra("channelType") ?: ""
        orderPlaceFlag = activity?.intent?.getIntExtra("orderPlaceFlag", 0) ?: 0


        sessionManager = SessionManager
        initProduct()
        //region modified
        if (sessionManager.isLoggedIn) {
            showAddressList()
        } else {
            showCustomerInfo()
        }
        //endregion

        initPayment()
        clickListener()
        getPackageInfo()
        calculateTotalPrice()

    }

    private fun showCustomerInfo() {

        binding?.customerLayout?.visibility = View.VISIBLE
        checkoutUserData = CheckoutUserData()
        isDeliveryAddressSelected = true
    }

    private fun showAddressList() {
        binding?.deliveryLayout?.visibility = View.VISIBLE
        deliveryAddressList.clear()
        deliveryAddressList.addAll(gson.fromJson(sessionManager.deliveryAddressBook, listType))
        if (deliveryAddressList.size > 0) {
            binding?.addAddressTV?.text = "অন্য ডেলিভারি এড্রেস যোগ করুন"

        } else {
            Handler(Looper.getMainLooper()).postDelayed({
                showAddressBook()
            }, 200L)
        }
        deliveryAddressAdapter = DeliveryAddressAdapter(deliveryAddressList)
        with(binding?.recyclerViewAddress!!) {
            isNestedScrollingEnabled = false
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            //  addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            val divider = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
            divider.setDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_envelop_check)!!)
            binding?.recyclerViewAddress?.addItemDecoration(divider)
            adapter = deliveryAddressAdapter
        }
        deliveryAddressAdapter.onItemSelected = { position, model ->
            checkoutUserData = model
            isDeliveryAddressSelected = true
        }
        deliveryAddressAdapter.onItemEditPressed = { position, CheckoutUserData ->
            showAddressBook(CheckoutUserData)
        }
        Handler(Looper.getMainLooper()).postDelayed({
            if (deliveryAddressAdapter.selectedIndex == -1 && deliveryAddressList.size > 0) {
                binding?.recyclerViewAddress?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
            }
        }, 200L)

    }

    private fun showAddressBook(model: CheckoutUserData? = null) {

        Timber.d("showAddressBook called")
        val dialog = AddAddressBottomSheet.newInstance(model, CheckoutDataModel())
        val tag = AddAddressBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onFromSaved = { uid, districtId, thanaId, areaId, address, mobile, alterMobile,
                               districtName, thanaName, areaName, postCode, thirdPartyLocationId ->
            // Update
            if (uid > 0) {
                deliveryAddressList.find { it.uid == uid }?.let { queryModel ->
                    queryModel.districtId = districtId
                    queryModel.thanaId = thanaId
                    queryModel.areaId = areaId
                    queryModel.billingAddress = address
                    queryModel.mobile = mobile
                    queryModel.alternateMobile = alterMobile
                    queryModel.districtName = districtName
                    queryModel.thanaName = thanaName
                    queryModel.areaName = areaName
                    queryModel.postCode = postCode
                    queryModel.thirdPartyLocationId = thirdPartyLocationId
                }
            } else {
                // Add
                val checkoutUserDataModel =
                    CheckoutUserData(System.currentTimeMillis(), districtId, thanaId, areaId, address, mobile, alterMobile, districtName, thanaName, areaName, postCode, thirdPartyLocationId)
                deliveryAddressList.add(checkoutUserDataModel)
                binding?.addAddressTV?.text = "অন্য ডেলিভারি এড্রেস যোগ করুন"
            }
            deliveryAddressAdapter.notifyDataSetChanged()
            Handler(Looper.getMainLooper()).postDelayed({
                if (deliveryAddressAdapter.selectedIndex == -1 && deliveryAddressList.size > 0) {
                    binding?.recyclerViewAddress?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                }
            }, 200L)

            sessionManager.deliveryAddressBook = gson.toJson(deliveryAddressList, listType)
        }
    }

    private fun clickListener() {

        binding?.addAddressLayout?.setOnClickListener {
            showAddressBook()
        }

        binding?.payNowBtn?.setOnClickListener {
            if (!isProcessingOrder && validation()) {
                //TODO
                //region Check here whether the user is logged in or not
                if (sessionManager.isLoggedIn) {
                    orderPlace()
                } else {
                    //registerUser()
                    fetchUserData()
                }
                //endregion
            }
        }

    }

    private fun fetchUserData() {
        checkoutUserData = CheckoutUserData().apply {
            if (binding?.mobileNumber?.text.toString().isNotEmpty()) {
               mobile = binding?.mobileNumber?.text.toString()
            }
            if (binding?.deliveryAddress?.text.toString().isNotEmpty()) {
               billingAddress = binding?.deliveryAddress?.text.toString()
            }
        }
        orderPlace()
    }

    private fun initProduct() {

        val dataAdapter = LiveCartAdapter()
        dataAdapter.initList(cartProductList)
        with(binding?.recyclerview!!) {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        dataAdapter.onDeleteClick = { model, position ->
            if (cartProductList.size > 1) {
                cartProductList.removeAt(position)
                dataAdapter.removeItem(position)
                calculateTotalPrice()
            } else {
                binding?.root?.snackbar("কমপক্ষে একটি প্রোডাক্ট থাকতে হবে")
            }
        }

        dataAdapter.onActionClicked = { flag ->
            when (flag) {
                0 -> {
                    Timber.d("ProductQuantity Decreased")
                    calculateTotalPrice()
                }
                1 -> {
                    Timber.d("ProductQuantity Increased")
                    calculateTotalPrice()
                }
            }
        }
    }

    private fun initPayment() {

        with(binding?.paymentMediumRV!!) {
            setHasFixedSize(false)
            disableItemAnimator()
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }

        viewModel.fetchDeliveryInfo().observe(viewLifecycleOwner, Observer { model:DeliveryInfoModel ->

            val paymentMediumList: MutableList<PaymentMediumModel> = mutableListOf()

            /*model.paymentMediums.add(
                PaymentMediumModel(
                    3, "কার্ড পেমেন্ট",
                    "https://static.ajkerdeal.com/images/paymentmethodimage/card.svg", 1,
                    "both", 1,
                    DeliveryChargesModel(50, 85)
                )
            )*/
            /*model.paymentMediums.add(
                PaymentMediumModel(
                    4, "ক্যাশ অন ডেলিভারি",
                    "https://static.ajkerdeal.com/images/paymentmethodimage/cod.svg", 1,
                    "both", 1,
                    DeliveryChargesModel(55, 85)
                )
            )*/

            Timber.d("paymentMode $paymentMode")
            when (paymentMode) {
                "advance" -> {
                    val filterPaymentMedium = model.paymentMediums.filterNot {it-> it.id == AppConstant.COD || it.id == AppConstant.EMI }
                    paymentMediumList.clear()
                    paymentMediumList.addAll(filterPaymentMedium)
                    Timber.d("paymentMode $paymentMode filterPaymentMedium $filterPaymentMedium")
                }
                "cod" -> {
                    val filterPaymentMedium = model.paymentMediums.filter {it-> it.id == AppConstant.COD }
                    paymentMediumList.clear()
                    paymentMediumList.addAll(filterPaymentMedium)
                    Timber.d("paymentMode $paymentMode filterPaymentMedium $filterPaymentMedium")
                }
                else -> {
                    paymentMediumList.clear()
                    paymentMediumList.addAll(model.paymentMediums)
                    Timber.d("paymentMode $paymentMode filterPaymentMedium $paymentMediumList")
                }
            }
            Timber.d("requestBody PaymentMediumList $paymentMediumList")
            paymentMediumAdapter = PaymentMediumAdapter(paymentMediumList)
            binding?.paymentMediumRV?.adapter = paymentMediumAdapter
            paymentMediumAdapter?.onItemSelected = { position, model ->
                isPaymentMediumSelected = true
                deliveryCharge = model.deliveryCharges.regularDeliveryCharge
                when (model.id) {
                    // bKash
                    1 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.bKash
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.bKash
                    }
                    // AD Balance
                    2 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.ADC
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.ADC
                    }
                    // "Debit/Credit Card"
                    3 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.CARD
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.CARD
                    }
                    // "COD"
                    4 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.COD
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.COD
                    }
                    // "EMI"
                    5 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.EMI
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.EMI
                    }
                    // "Rocket"
                    6 -> {
                        cardType = com.bdjobs.app.ajkerDeal.enums.CardTypeDef.ROCKET
                        paymentType = com.bdjobs.app.ajkerDeal.enums.PaymentTypeDef.ROCKET
                    }
                }
            }
            if (paymentMediumList.size == 1) {
                Handler(Looper.getMainLooper()).postDelayed({
                    binding?.paymentMediumRV?.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                }, 200L)
            }
        })
    }

    private fun calculateTotalPrice() {

        totalProductPrice = 0
        cartProductList.forEach { productModel ->
            totalProductPrice += (productModel.productPrice * productModel.cartDealQuantity)
        }
        grandTotal = totalProductPrice + deliveryCharge
        val priceText = "সর্বমোট: ৳" + DigitConverter.toBanglaDigit(totalProductPrice, true)
        binding?.totalPrice?.text = priceText
    }

    private fun validation(): Boolean {

        hideKeyboard()
        calculateTotalPrice()

        if (!isPaymentMediumSelected) {
            context?.toast("পেমেন্ট মাধ্যম সিলেক্ট করুন")
            return false
        }

        if (totalProductPrice <= 0) {
            context?.toast("প্রোডাক্ট প্রাইস সঠিক নয়")
            return false
        }

        val mobileNumber = binding?.mobileNumber?.text?.toString() ?: ""
        if (!sessionManager.isLoggedIn && (mobileNumber.isEmpty() || !isValidPhone(mobileNumber))) {
            context?.toast("সঠিক মোবাইল নম্বর লিখুন")
            return false
        }

        if (!isDeliveryAddressSelected) {
            context?.toast("ডেলিভারি এড্রেস সিলেক্ট করুন")
            return false
        }

        val deliveryAddress = binding?.deliveryAddress?.text?.toString() ?: ""
        if (!sessionManager.isLoggedIn && deliveryAddress.isEmpty()) {
            context?.toast("ডেলিভারি ঠিকানা লিখুন")
            return false
        }

        if (sessionManager.isLoggedIn && !isDeliveryAddressSelected) {
            context?.toast("ডেলিভারি এড্রেস সিলেক্ট করুন")
            return false
        }

        return true
    }

    /*private fun registerUser() {

        val mobileNumber = binding?.mobileNumber?.text?.toString() ?: ""
        viewModel.fetchSignUpInfo(
            RegistrationRequest(
                mobileNumber, mobileNumber, "User${generateRandomNumber(999)}", sessionManager.deviceId, sessionManager.firebaseToken
            )
        ).observe(viewLifecycleOwner, Observer { model ->
            if (model.id == 0) {
                sessionManager.userId = model.currentCustomerId
                orderPlace()
            } else if (model.id > 0) {
                sessionManager.userId = model.id
                orderPlace()
            }

        })
    }*/

    private fun orderPlace() {

        isProcessingOrder = true
        binding?.payNowBtn?.text = "প্রসেসিং, অপেক্ষা করুন"
        binding?.progressBar?.visibility = View.VISIBLE

        /*
        * Here we sent different data to place the order
        * but we mainly require 2 data
        * 1. Mobile Number  (required)
        * 2. Address        (required)
        * SO, if the user is logged in, we get the Data from Session Manager
        * otherwise, we take data from the required field
        * */


        val requestBody: MutableList<LiveOrderRequest> = mutableListOf()
        cartProductList.forEach { productModel ->
            val model = LiveOrderRequest(
                productModel.id,
                productModel.productPrice,
                productModel.cartDealQuantity,
                deliveryCharge,
                merchantId,
                sessionManager.userId,
                checkoutUserData!!.districtId,
                checkoutUserData!!.thanaId,
                checkoutUserData!!.areaId,
                checkoutUserData!!.postCode,
                checkoutUserData!!.billingAddress,
                checkoutUserData!!.mobile,
                checkoutUserData!!.alternateMobile,
                cardType,
                paymentStatus,
                paymentType,
                if (cartProductList.size > 1) 1 else 0,
                totalProductPrice,
                productModel.liveId,
                channelType,
                orderPlaceFlag,
                appVersion
            )
            requestBody.add(model)
        }

        Timber.d("requestBody $requestBody")

        //TODO activate the following api
        viewModel.insertLiveOrder(requestBody).observe(viewLifecycleOwner, Observer { responseList ->
            isProcessingOrder = false
            binding?.payNowBtn?.text = "অর্ডার করুন"
            binding?.progressBar?.visibility = View.GONE

            if (responseList.isNotEmpty()) {
                alert(null, getString(R.string.order_success)).show()
            } else {
                alert(null, getString(R.string.order_processing_failed)).show()
                return@Observer
            }

            val responseModel = responseList.first()
            val isCartOrder = responseList.size > 1
            when (cardType) {
                com.bdjobs.app.ajkerDeal.enums.CardTypeDef.bKash -> {
                    val bKashUrl = if (isCartOrder) {
                        "${AppConstant.LIVE_BKASH_GATEWAY}?LiveShopCartID=${responseModel.shopCartId}"
                    } else {
                        "${AppConstant.LIVE_BKASH_GATEWAY}?LiveOrderId=${responseModel.liveOrderId}"
                    }
                    val bundle = bundleOf(
                        "loadUrl" to bKashUrl,
                        "webTitle" to "পেমেন্ট"
                    )
                    findNavController().navigate(R.id.nav_webView, bundle)
                }
                com.bdjobs.app.ajkerDeal.enums.CardTypeDef.COD -> {
                    val codUrl = if (isCartOrder) {
                        "${AppConstant.LIVE_COD_GATEWAY}?LiveShopCartId=${responseModel.shopCartId}"
                    } else {
                        "${AppConstant.LIVE_COD_GATEWAY}?LiveOrderId=${responseModel.liveOrderId}"
                    }
                    val bundle = bundleOf(
                        "loadUrl" to codUrl,
                        "webTitle" to "পেমেন্ট"
                    )
                    findNavController().navigate(R.id.nav_webView, bundle)
                }

                /*CardTypeDef.CARD -> {
                    val sslUrl = if (list.size > 1) {
                        "${AppConstant.GATEWAY_SSL_CART}?SCID=${responseModel.shopCartId}&totalPoint=${responseModel.totalPoint}&vId=0&vType=0"
                    } else {
                        "${AppConstant.GATEWAY_SSL_SINGLE}?CID=${responseModel.couponId}&totalPoint=${responseModel.totalPoint}&vId=0&vType=0"
                    }
                    val bundle = bundleOf(
                        "loadUrl" to sslUrl,
                        "webTitle" to "পেমেন্ট"
                    )
                    findNavController().navigate(R.id.nav_webView, bundle)
                }*/
                /*CardTypeDef.ROCKET -> {
                    val sslUrl = if (list.size > 1) {
                        "${AppConstant.GATEWAY_SSL_CART}?SCID=${responseModel.shopCartId}&totalPoint=${responseModel.totalPoint}&vId=0&vType=0"
                    } else {
                        "${AppConstant.GATEWAY_SSL_SINGLE}?CID=${responseModel.couponId}&totalPoint=${responseModel.totalPoint}&vId=0&vType=0"
                    }
                    val bundle = bundleOf(
                        "loadUrl" to sslUrl,
                        "webTitle" to "পেমেন্ট"
                    )
                    findNavController().navigate(R.id.nav_webView, bundle)
                }*/
            }


            /*if (list.isEmpty()) {
                alert(null, getString(R.string.order_processing_failed)).show()
            } else {
                if (list.first().couponId == 0) {
                    alert(null, list.first().orderMessage).show()
                    return@Observer
                }
                alert(null, getString(R.string.order_success)).show()

                }
            }*/

        })
    }

    private fun getPackageInfo() {
        val pInfo: PackageInfo = requireContext().packageManager.getPackageInfo(requireContext().packageName, 0)
        appVersion = pInfo.versionName
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}