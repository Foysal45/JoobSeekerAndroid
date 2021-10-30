package com.bdjobs.app.ajkerDeal.ui.video_shopping.live_cart

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.R
import com.bdjobs.app.ajkerDeal.api.models.live_product.LiveProductData
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bdjobs.app.databinding.FragmentLiveCartBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.jetbrains.anko.toast
import kotlin.concurrent.thread

@SuppressLint("SetTextI18n")
class LiveCartBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentLiveCartBinding? = null
    var onBuyClick: ((model: Boolean) -> Unit)? = null
    var onDeleteClick: ((model: Boolean) -> Unit)? = null

    //private val viewModel: LiveProductListViewModel by inject()
    private var liveId: Int = 0
    private var cartProductList: MutableList<LiveProductData> = mutableListOf()

    private lateinit var dataAdapter: LiveCartAdapter

    companion object {

        fun newInstance(liveId: Int, cartProductList: MutableList<LiveProductData>): LiveCartBottomSheet = LiveCartBottomSheet().apply {
            this.liveId = liveId
            this.cartProductList = cartProductList
        }

        val tag: String = LiveCartBottomSheet::class.java.name
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
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
        return FragmentLiveCartBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dataAdapter = LiveCartAdapter()
        dataAdapter.initList(cartProductList)
        binding?.recyclerview?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext())
                adapter = dataAdapter
                addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            }
        }
        dataAdapter.onItemClick = { model, position ->
            pictureDialog(model)
        }
        dataAdapter.onDeleteClick = { model, position ->
            cartProductList.removeAt(position)
            dataAdapter.removeItem(position)
            binding?.titleTV?.text = "কার্ট (${DigitConverter.toBanglaDigit(cartProductList.size, true)})"
            onDeleteClick?.invoke(true)
            if (cartProductList.isEmpty()) {
                dismiss()
            }
        }

        binding?.titleTV?.text = "কার্ট (${DigitConverter.toBanglaDigit(cartProductList.size, true)})"

        binding?.buyNowBtn?.setOnClickListener {
            if (cartProductList.isNotEmpty()) {
                onBuyClick?.invoke(true)
            } else {
                context?.toast("কার্টে প্রোডাক্ট অ্যাড করুন")
            }
        }

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
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val window = dialog.window
        window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
        window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        dialog.show()
        close.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}