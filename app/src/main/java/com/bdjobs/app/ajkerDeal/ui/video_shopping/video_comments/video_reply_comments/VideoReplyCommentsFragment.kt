package com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.video_reply_comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bdjobs.app.ajkerDeal.api.models.video_comments.load_all_comments.VideoReplyCommentsModel
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.CommentBottomSheet
import com.bdjobs.app.ajkerDeal.ui.video_shopping.video_comments.CommentBoxBottomSheet
import com.bdjobs.app.R
import com.bdjobs.app.SessionManger.BdjobsUserSession
import com.bdjobs.app.databinding.FragmentVideoReplyCommentsBinding
import com.bdjobs.app.ajkerDeal.utilities.DigitConverter
import com.bdjobs.app.ajkerDeal.utilities.SessionManager
import com.bdjobs.app.ajkerDeal.utilities.hideKeyboard
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class VideoReplyCommentsFragment : Fragment() {

    private var binding: FragmentVideoReplyCommentsBinding? = null
    private val viewModel: VideoReplyCommentsViewModel by inject()

    //Bundle
    private lateinit var model: List<VideoReplyCommentsModel>
    private lateinit var bundle: Bundle
    private lateinit var sessionManager: SessionManager
    private lateinit var bdjobsUserSession: BdjobsUserSession

    private lateinit var dataAdapter: VideoReplyCommentsAdapter

    //Variables
    private var commentId = 0
    private var catalogID = 0
    private val requestCodeCheckOut = 12920

    companion object{
        @JvmStatic
        fun newInstance(model: List<VideoReplyCommentsModel>, commentId: Int, catalogID: Int, bundle: Bundle): VideoReplyCommentsFragment = VideoReplyCommentsFragment().apply {
            this.model = model
            this.bundle = bundle
            this.commentId = commentId
            this.catalogID = catalogID
        }

        @JvmField
        val tag: String = VideoReplyCommentsFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentVideoReplyCommentsBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Timber.d("Bundle $model")
        sessionManager = SessionManager
        bdjobsUserSession = BdjobsUserSession(requireContext())


        binding?.backBtn?.setImageResource(R.drawable.ic_arrow_back)
        binding?.backBtn?.setOnClickListener {
            (parentFragment as CommentBottomSheet).popBackStack()
        }
        dataAdapter = VideoReplyCommentsAdapter()
        with(binding?.recyclerView!!) {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(requireContext())
            adapter = dataAdapter
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
        }

        dataAdapter.initLoad(model)

        binding?.title?.text = "উত্তর (${DigitConverter.toBanglaDigit(model.size)}টি)"

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

    private fun sendComment(replyComment: String) {
        val customerId = bdjobsUserSession.userId
        val replyComment = replyComment
        val commentId = bundle["CommentId"].toString().toInt()

        //date format added
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
        val insertedOn = sdf.format(Date().time)
        val vsCatalogId = catalogID
        Timber.d("customerId $customerId, comment $replyComment, insertedOn $insertedOn, vsCatalogId $vsCatalogId, commentId $commentId")
        //Toast.makeText(context, "customerId $customerId, comment $replyComment, insertedOn $insertedOn, vsCatalogId $vsCatalogId, commentId $commentId", Toast.LENGTH_SHORT).show()

        if (bdjobsUserSession.isLoggedIn!!) {
            if (!replyComment.isNullOrEmpty()) {
                //binding?.commentBox?.text?.clear()
                viewModel.insertVideoReplyComments(customerId!!.toInt(), replyComment, insertedOn, vsCatalogId, commentId).observe(viewLifecycleOwner, Observer { check->
                    if (check) {

                        Toast.makeText(context, "কমেন্ট অ্যাড করা হয়েছে", Toast.LENGTH_SHORT).show()
                        (parentFragment as CommentBottomSheet).popBackStack()
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
        binding?.backBtn?.visibility = View.GONE
        binding?.title?.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        Timber.d("State Resume")
        binding?.backBtn?.visibility = View.VISIBLE
        binding?.title?.visibility = View.VISIBLE
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }
}