package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments.VideoCommentsModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.video_reply_comments.VideoReplyCommentsFragment
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.FragmentVideoCommentsBinding
import com.bdjobs.app.ajkerDeal.utilities.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class VideoCommentsFragment : Fragment() {

    private var binding: FragmentVideoCommentsBinding? = null
    private val viewModel: VideoCommentsViewModel by inject()

    private lateinit var dataAdapter: VideoCommentsAdapter

    //Bundle
    private lateinit var bundle: Bundle
    private var model: VideoCommentsModel? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var bdjobsUserSession: BdjobsUserSession

    //variables
    private var totalProduct = 0
    private var isLoading = false
    private val visibleThreshold = 5
    private var catalogID = 0

    companion object {
        @JvmStatic
        fun newInstance(bundle: Bundle, catalogID: Int): VideoCommentsFragment = VideoCommentsFragment().apply {
            this.bundle = bundle
            this.catalogID = catalogID
        }

        @JvmField
        val tag: String = VideoCommentsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentVideoCommentsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        sessionManager = SessionManager
        bdjobsUserSession = BdjobsUserSession(requireContext())

        binding?.crossBtn?.setImageResource(R.drawable.ic_close_white_24dp)
        binding?.crossBtn?.setOnClickListener {
            (parentFragment as CommentBottomSheet).dismissParent()
        }
        dataAdapter = VideoCommentsAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        dataAdapter.onReplyClicked = { model, commentId ->
            this.model = model
            gotoDetails(commentId)
        }

        fetchAllVideoComments(0, 20)

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is ViewState.ShowMessage -> {
                    context?.toast(state.message)
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


        binding?.recyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                Timber.d("recycler view")
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = (recyclerView.layoutManager as LinearLayoutManager).itemCount
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    //Log.i(TAG, "onScrolled: \n"+ "position : "+currentItemCount + " firstCall : "+ firstCall + " mTotalFeaturedProduct : " + mTotalFeaturedProduct + " "+isLoading);
                    if (!isLoading && currentItemCount <= lastVisibleItem + visibleThreshold && currentItemCount < totalProduct) {
                        isLoading = true
                        fetchAllVideoComments(lastVisibleItem, totalProduct)
                    }
                }
            }
        })

        binding?.commentBox?.setOnClickListener {

            if (bdjobsUserSession.isLoggedIn!!) {
                showCommentBox()
            } else {
                //removed, use it in your favor
                /*val intent = Intent(requireContext(), SignInNewActivity::class.java)
                startActivity(intent)*/
            }
        }


    }

    private fun showCommentBox() {
        val dialog = CommentBoxBottomSheet.newInstance()
        val tag = CommentBoxBottomSheet.tag
        dialog.show(childFragmentManager, tag)
        dialog.onItemClicked = { message ->
            dialog.dismiss()
            Timber.d(message)
            hideKeyboard()
            sendComment(message)
        }
    }

    fun gotoDetails(commentId: Int) {

        val bundle = bundleOf(
            "CommentId" to model?.commentId,
            "CustomerId" to (model?.customerId ?: 0),
            "CustomerName" to model?.customerName,
            "InsertedOn" to model?.insertedOn
        )

        Timber.d("commentId $commentId")
        Timber.d("commentReply ${model?.replyComments}")
        val tag = VideoReplyCommentsFragment.tag
        val fragment = VideoReplyCommentsFragment.newInstance(model?.replyComments!!, commentId, catalogID, bundle)
        addFragment(fragment, tag)

        //fragment.show(childFragmentManager, tag)

    }


    fun fetchAllVideoComments(index: Int, count: Int) {

        binding?.progressBar?.visibility = View.VISIBLE

        Timber.d("catalogID VideoComments $catalogID")


        viewModel.fetchVideoComments(catalogID, index, count).observe(viewLifecycleOwner, Observer { list ->
            dataAdapter.initLoad(list)
            binding?.title?.text = "মন্তব্য করুন (${DigitConverter.toBanglaDigit(list.size)}টি)"
        })


    }

    private fun sendComment(comment: String) {
        val customerId = bdjobsUserSession.userId
        val comment = comment

        //date format added
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val insertedOn = sdf.format(Date().time)
        val vsCatalogId = catalogID

        Timber.d("customerId ${customerId}, comment $comment, insertedOn $insertedOn, vsCatalogId $vsCatalogId")
        //Toast.makeText(context, "customerId $customerId, comment $comment, insertedOn $insertedOn, vsCatalogId $vsCatalogId", Toast.LENGTH_SHORT).show()

        if (bdjobsUserSession.isLoggedIn!!) {
            if (!comment.isNullOrEmpty()) {
                //binding?.commentBox?.text?.clear()
                viewModel.insertVideoComments(customerId!!.toInt(), comment, insertedOn, vsCatalogId).observe(viewLifecycleOwner, Observer { check ->
                    if (check) {

                        Toast.makeText(context, "কমেন্ট অ্যাড করা হয়েছে", Toast.LENGTH_SHORT).show()
                        fetchAllVideoComments(0, 20)
                    }
                })

            } else {
                Toast.makeText(context, "কমেন্ট বক্সে কিছু লিখা নেই", Toast.LENGTH_SHORT).show()
            }
        } else{
            //removed, use it in your favor
            /*val intent = Intent(requireContext(), SignInNewActivity::class.java)
            startActivity(intent)*/
            Toast.makeText(context, "কমেন্ট করতে লগইন করুন", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause() {
        super.onPause()
        Timber.d("State Pause")
        binding?.crossBtn?.visibility = View.GONE
        binding?.title?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        Timber.d("State Resume")
        binding?.crossBtn?.visibility = View.VISIBLE
        binding?.title?.visibility = View.VISIBLE
    }


    private fun addFragment(fragment: Fragment, tag: String) {
        (parentFragment as CommentBottomSheet).replaceFragment(fragment, tag)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}