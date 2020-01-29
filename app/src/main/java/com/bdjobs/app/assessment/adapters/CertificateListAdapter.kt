package com.bdjobs.app.assessment.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bdjobs.app.assessment.models.Certificate
import com.bdjobs.app.assessment.models.CertificateData
import com.bdjobs.app.databinding.ItemCertificateBinding


class CertificateListAdapter(val context: Context, val clickListener: ClickListener) :
        ListAdapter<CertificateData, CertificateListAdapter.CertificateViewHolder>(DiffUserCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CertificateViewHolder {
        return CertificateViewHolder.from(parent)
    }

    private var selectedItemViewHolder: CertificateViewHolder? = null

    override fun onBindViewHolder(holder: CertificateViewHolder, position: Int) {
        val certificate = getItem(position)
        holder.bind(certificate, clickListener)
    }


    companion object DiffUserCallback : DiffUtil.ItemCallback<CertificateData>() {
        override fun areItemsTheSame(oldItem: CertificateData, newItem: CertificateData): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: CertificateData, newItem: CertificateData): Boolean {
            return oldItem?.assessmentId == newItem?.assessmentId
        }
    }


    class CertificateViewHolder(private var binding: ItemCertificateBinding) :
            RecyclerView.ViewHolder(binding.root) {


        companion object {

            fun from(parent: ViewGroup): CertificateViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemCertificateBinding.inflate(layoutInflater, parent, false)
                return CertificateViewHolder(binding)
            }
        }


        fun bind(
                certificate: CertificateData,
                clickListener: ClickListener
        ) {
            binding.certificate = certificate
            binding.executePendingBindings()
            binding.clickListener = clickListener
        }
    }
}

class ClickListener(val clickListener: (id: CertificateData) -> Unit) {
    fun onClick(certificateData: CertificateData) = clickListener(certificateData)
}


