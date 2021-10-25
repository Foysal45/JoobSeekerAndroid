package com.bdjobs.app.ajkerDeal.ui.checkout_live.live_order_management

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.ajkerDeal.api.PagingModel
import com.bdjobs.app.ajkerDeal.api.models.live_order_management.LiveOrderManagementResponseBody
import com.bdjobs.app.ajkerDeal.utilities.*
import com.bdjobs.app.databinding.FragmentLiveOrderManagementBinding
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*

class LiveOrderManagementFragment : Fragment() {

    private var binding: FragmentLiveOrderManagementBinding? = null
    private val viewModel: LiveOrderManagementViewModel by inject()

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private lateinit var dataAdapterPending: LiveOrderManagementAdapter

    private lateinit var sessionManager: SessionManager

    private var customerId: Int = 0

    private var isExcelDownload: Boolean = false

    //variables
    private var totalProduct = 0
    private var isLoading = false
    private val visibleThreshold = 5
    private var currentTotalCount = 0

    companion object {
        @JvmStatic
        fun newInstance(): LiveOrderManagementFragment = LiveOrderManagementFragment().apply {
        }

        @JvmField
        val tag: String = LiveOrderManagementFragment::class.java.name
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return FragmentLiveOrderManagementBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dataAdapterPending = LiveOrderManagementAdapter()
        sessionManager = SessionManager
        customerId = sessionManager.userId

        setAdapterValue()
        pendingPagingState()
        setDataToRecyclerView()

        viewModel.viewState.observe(viewLifecycleOwner, Observer { state:ViewState ->
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

    }

    private fun setAdapterValue() {
        binding?.orderRecyclerView?.let { recyclerView ->
            with(recyclerView) {
                setHasFixedSize(true)
                layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                Timber.d("FlowOfTheProcess 11")
                adapter = dataAdapterPending
                fetchOrderManagementPendingList(0)
            }
        }
    }

    private fun setDataToRecyclerView() {

        Timber.d("FlowOfTheProcess 30")
        currentTotalCount = 0
        binding?.orderRecyclerView?.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) {
                    val currentItemCount = recyclerView.layoutManager?.itemCount ?: 0
                    val lastVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                    /*val lastVisibleItemPositions = (recyclerView.layoutManager as StaggeredGridLayoutManager).findLastVisibleItemPositions(null)
                    val lastVisibleItem = when {
                        lastVisibleItemPositions.last() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.last()
                        }
                        lastVisibleItemPositions.first() != RecyclerView.NO_POSITION -> {
                            lastVisibleItemPositions.first()
                        }
                        else -> {
                            0
                        }
                    }*/

                    Timber.d("onScrolled: CurrentTotalCount: $currentTotalCount, ItemCount: $currentItemCount <= lastVisible: $lastVisibleItem + $visibleThreshold ${!isLoading}")
                    if (!isLoading && currentItemCount <= (lastVisibleItem + visibleThreshold)) {
                        Timber.d("onScrolled: loading CurrentTotalCount: $currentTotalCount, ItemCount: $currentItemCount <= lastVisible: $lastVisibleItem + $visibleThreshold ${!isLoading}")
                        isLoading = true
                        //currentTotalCount += currentItemCount

                        Timber.d("FlowOfTheProcess 31")
                        fetchOrderManagementPendingList(currentItemCount)
                    }
                }
            }
        })
    }

    private fun pendingPagingState() {

        Timber.d("FlowOfTheProcess 41")

        viewModel.pagingState.observe(viewLifecycleOwner, Observer { state:PagingModel<List<LiveOrderManagementResponseBody>> ->
            isLoading = false
            binding?.swipeRefreshLayout?.isRefreshing = false

            if (state.dataList.size < 20) {
                isLoading = true
            }

            if (state.isInitLoad) {
                dataAdapterPending.initLoad(state.dataList)
                //totalProduct = state.totalCount
                totalProduct = state.dataList.size

                binding?.orderListCountLayout?.visibility = View.VISIBLE
                binding?.orderListCountTv?.text = DigitConverter.toBanglaDigit(totalProduct, false)

                if (state.dataList.isEmpty()) {
                    isLoading = false
                    binding?.emptyView?.isVisible = true
                }

                if (isExcelDownload) {
                    isExcelDownload = false
                    //generateExcel()
                }
            } else {
                if (state.dataList.isEmpty()) {
                    isLoading = true
                } else {
                    Timber.d("recyclerViewDebug ${state.dataList.size}")
                    dataAdapterPending.pagingLoad(state.dataList)
                }
            }
        })
    }

    private fun fetchOrderManagementPendingList(currentItemCount: Int) {
        Timber.d("FlowOfTheProcess 21")
        binding?.swipeRefreshLayout?.isRefreshing = true
        viewModel.fetchLiveOrderManagementList(customerId, currentItemCount, 20)
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}