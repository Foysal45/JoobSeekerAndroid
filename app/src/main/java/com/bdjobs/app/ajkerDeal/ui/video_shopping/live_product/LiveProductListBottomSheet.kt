package com.bdjobs.app.ajkerDeal.ui.video_shopping.live_product

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.firebase.LiveProductEvent
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.databinding.FragmentLiveProductListBinding
import com.bdjobs.app.ajkerDeal.utilities.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.URL
import java.util.*
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class LiveProductListBottomSheet : BottomSheetDialogFragment() {

    private var binding: FragmentLiveProductListBinding? = null
    var onBuyClick: ((model: LiveProductData) -> Unit)? = null
    var onCartClick: ((model: LiveProductData) -> Unit)? = null
    var onShopCartClicked: (() -> Unit)? = null

    private lateinit var dataAdapter: LiveProductAdapter
    private lateinit var productUpdateRef: DatabaseReference

    private var liveId: Int = 0
    private var productList: MutableList<LiveProductData> = mutableListOf()
    private var listPosition: Int = -1
    private var cartProductList: MutableList<LiveProductData> = mutableListOf()
    private var isAutoOpen: Boolean = false
    private var redirectToFB: Boolean = false

    private val viewModel: LiveProductListViewModel by inject()

    companion object {

        fun newInstance(
            liveId: Int,
            productList: MutableList<LiveProductData>,
            listPosition: Int,
            cartProductList: MutableList<LiveProductData>,
            redirectToFB: Boolean,
            isAutoOpen: Boolean = false
        ): LiveProductListBottomSheet =
            LiveProductListBottomSheet().apply {
                this.liveId = liveId
                this.productList = productList
                this.listPosition = listPosition
                this.isAutoOpen = isAutoOpen
                this.cartProductList = cartProductList
                this.redirectToFB = redirectToFB
            }

        val tag: String = LiveProductListBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogThemeTransparent)
    }

    override fun onStart() {
        super.onStart()

        val dialog: BottomSheetDialog? = dialog as BottomSheetDialog?
        dialog?.setCanceledOnTouchOutside(true)
        val bottomSheet: FrameLayout? = dialog?.findViewById(com.google.android.material.R.id.design_bottom_sheet)
        //val metrics = resources.displayMetrics

        if (bottomSheet != null) {
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_COLLAPSED
            thread {
                activity?.runOnUiThread {
                    val dynamicHeight = binding?.parent?.height ?: 500
                    BottomSheetBehavior.from(bottomSheet).peekHeight = dynamicHeight
                }
            }
            with(BottomSheetBehavior.from(bottomSheet)) {
                //state = BottomSheetBehavior.STATE_COLLAPSED
                skipCollapsed = false
                isHideable = false

            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveProductListBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = LiveProductAdapter()
        dataAdapter.setSelectedPosition(listPosition)
        dataAdapter.setRedirectionFlag(redirectToFB)
        dataAdapter.cartDataList(cartProductList)
        with(binding?.recyclerview!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            itemAnimator = null
        }
        dataAdapter.onBuyClick = { model, position ->
            onBuyClick?.invoke(model)
        }
        dataAdapter.onItemClick = { model, position ->
            pictureDialog(model)
        }
        dataAdapter.onCartClick = { model, position ->
            onCartClick?.invoke(model)
            binding?.cartCount?.text = DigitConverter.toBanglaDigit(cartProductList.size)
            dataAdapter.notifyItemChanged(position)
        }
        dataAdapter.onDownloadClick = { model, position ->
            model.coverPhoto?.let { it->
                downloadImage(it, model.productPrice)
            }
        }

        fetchProduct()

        if (redirectToFB) {
            binding?.cartCount?.isVisible = false
        } else {
            binding?.cartCount?.text = DigitConverter.toBanglaDigit(cartProductList.size)
            binding?.cartCount?.setOnClickListener {
                if (cartProductList.isNotEmpty()) {
                    onShopCartClicked?.invoke()
                }
            }
        }

        //TODO connect it to Firebase Database
        //val dbRef = Firebase.database.getReference("LiveShow")
        //productUpdateRef = dbRef.child("productUpdate").child(liveId.toString())
        //productUpdateRef.addValueEventListener(productUpdateLister)

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    requireContext().toast(state.message)
                }
                is ViewState.KeyboardState -> {
                    hideKeyboard()
                }
                is ViewState.ProgressState -> {
                    if (state.isShow) {
                        binding?.progressBar?.visibility = View.VISIBLE
                    } else {
                        binding?.progressBar?.visibility = View.GONE
                    }
                }
            }
        })

        if (isAutoOpen) {
            binding?.chronometer?.let { chronometer ->
                chronometer.visibility = View.VISIBLE
                chronometer.base = SystemClock.elapsedRealtime() + AppConstant.AUTO_HIDE_DIALOG_TIME
                chronometer.start()
            }
            binding?.chronometer?.setOnChronometerTickListener { chronometer ->
                val elapsed = chronometer.base - SystemClock.elapsedRealtime()
                if (elapsed <= 0L) {
                    chronometer.stop()
                    chronometer.visibility = View.GONE
                    dismiss()
                }
                //Timber.d("chronometerTickListener  $elapsed")
            }
        }

        binding?.recyclerview?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    if (isAutoOpen) {
                        binding?.chronometer?.onChronometerTickListener = null
                        binding?.chronometer?.stop()
                        binding?.chronometer?.visibility = View.GONE
                    }
                }
            }
        })

        binding?.closeBtn?.setOnClickListener {
            if (isAutoOpen) {
                binding?.chronometer?.onChronometerTickListener = null
                binding?.chronometer?.stop()
            }
            dismiss()
        }


    }

    private val productUpdateLister = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            Timber.tag("firebaseDebug").d("productUpdate $snapshot")
            if (snapshot.exists()) {
                val model: LiveProductEvent? = snapshot.getValue(LiveProductEvent::class.java)
                val timeStamp: Long = model?.time ?: 0
                if (timeStamp > 0L) {
                    val currentTimeStamp = Date().time - 120000
                    if (timeStamp > currentTimeStamp) {
                        if (isResumed) {
                            fetchProduct()
                        }
                    }
                }
            }
        }

        override fun onCancelled(error: DatabaseError) {
            Timber.tag("firebaseDebug").d("chatRoomReference ${error.message}")
        }
    }

    override fun onStop() {
        super.onStop()
        binding?.chronometer?.onChronometerTickListener = null
        binding?.chronometer?.stop()
        Timber.d("onStop called from LiveProductListBottomSheet")
    }

    private fun fetchProduct() {
        dataAdapter.initList(productList)
        binding?.titleTV?.text = "প্রোডাক্ট (${DigitConverter.toBanglaDigit(productList.size, true)})"
        binding?.liveIdTV?.text = "(লাইভ: ${DigitConverter.toBanglaDigit(liveId)})"
        if (productList.isEmpty()) {
            binding?.emptyView?.visibility = View.VISIBLE
        } else {
            binding?.emptyView?.visibility = View.GONE
        }
        if (listPosition > -1) {
            binding?.recyclerview!!.postDelayed({
                binding?.recyclerview!!.smoothScrollToPosition(listPosition)
            }, 200L)
        }
        //no need to activate the following, data is coming with bundle
        /*viewModel.fetchLiveProducts(liveId).observe(viewLifecycleOwner, Observer {
            dataAdapter.initList(it)
            binding?.titleTV?.text = "প্রোডাক্ট (${DigitConverter.toBanglaDigit(it.size, true)})"
            if (it.isEmpty()) {
                binding?.emptyView?.visibility = View.VISIBLE
            } else {
                binding?.emptyView?.visibility = View.GONE
            }
        })*/
    }

    private fun pictureDialog(model: LiveProductData, listener: ((type: Int) -> Unit)? = null) {
        val builder = MaterialAlertDialogBuilder(requireContext())
        val view = layoutInflater.inflate(R.layout.dialog_product_overview, null)
        builder.setView(view)
        val title: TextView = view.findViewById(R.id.title)
        val productImage: ImageView = view.findViewById(R.id.image)
        val close: ImageView = view.findViewById(R.id.close)

        title.text = "প্রোডাক্ট কোড: ${model.id} দাম: ${DigitConverter.toBanglaDigit(model.productPrice, true)}৳"
        Glide.with(productImage)
            .load(model.coverPhoto)
            .apply(RequestOptions().placeholder(R.drawable.logo))
            .into(productImage)

        val dialog = builder.create()
        dialog.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        productUpdateRef.removeEventListener(productUpdateLister)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

    private fun downloadImage(url: String, price: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                //download the image from url
                val bitmapOriginal = BitmapFactory.decodeStream(URL(url).openConnection().getInputStream())
                val paint = Paint().apply {
                    color = Color.RED
                    style = Paint.Style.FILL
                    textSize = 80f
                    isAntiAlias = true
                }
                val bitmapCanvas = Bitmap.createBitmap(bitmapOriginal.width, bitmapOriginal.height, bitmapOriginal.config)
                Canvas(bitmapCanvas).apply {
                    drawBitmap(bitmapOriginal, 0f, 0f, null)
                    drawText("Price: $price", 20f, 80f, paint)
                }

                val tempFile = createNewFile(requireContext(), FileType.Picture)
                tempFile?.let { file ->
                    file.outputStream().use { outputStream ->
                        bitmapCanvas.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        moveToDICM(requireContext(), file.absolutePath, FileType.Picture) { isSuccess, path ->

                        }
                    }
                }

                withContext(Dispatchers.Main) {
                    context?.toast("ডাউনলোড হয়েছে SDCard/Download/${getString(R.string.app_name_folder)}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}