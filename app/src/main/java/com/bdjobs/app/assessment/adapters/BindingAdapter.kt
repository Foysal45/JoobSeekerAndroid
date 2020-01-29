package com.bdjobs.app.assessment.adapters

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.assessment.models.CertificateData

@BindingAdapter("list")
fun bindPostsRecyclerView(recyclerView: RecyclerView, data: List<CertificateData?>?) {
    val adapter = recyclerView.adapter as CertificateListAdapter
    data?.let {
        adapter.submitList(data)
    }
}

