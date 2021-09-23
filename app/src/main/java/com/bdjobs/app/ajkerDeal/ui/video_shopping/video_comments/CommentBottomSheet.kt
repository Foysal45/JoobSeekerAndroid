package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.bdjobs.app.R
import com.bdjobs.app.databinding.FragmentCommentBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import timber.log.Timber
import kotlin.concurrent.thread

class CommentBottomSheet: BottomSheetDialogFragment() {

    private var binding: FragmentCommentBottomSheetBinding? = null

    private lateinit var bundle: Bundle
    private var catalogID = 0

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle, catalogID: Int): CommentBottomSheet = CommentBottomSheet().apply {
            this.bundle = bundle
            this.catalogID = catalogID
        }

        @JvmField
        val tag: String = CommentBottomSheet::class.java.name
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
            BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
            thread {
                activity?.runOnUiThread {
                    val parentHeight = binding?.parent?.height ?: 500
                    Timber.d("bottomSheetDebug height $parentHeight")
                    BottomSheetBehavior.from(bottomSheet).peekHeight = parentHeight + 1000
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
        return FragmentCommentBottomSheetBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fragment = VideoCommentsFragment.newInstance(bundle, catalogID)
        val tag = VideoCommentsFragment.tag

        replaceFragment(fragment, tag)

    }

    fun replaceFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction().apply {
            replace(R.id.containerComment, fragment, tag)
            addToBackStack(tag)
            commit()
        }
    }

    fun addFragment(fragment: Fragment, tag: String) {
        childFragmentManager.beginTransaction().apply {
            add(R.id.containerComment, fragment, tag)
            addToBackStack(tag)
            commit()
        }
    }

    fun dismissParent() {
        dismiss()
    }

    fun popBackStack() {
        childFragmentManager.popBackStack()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

}