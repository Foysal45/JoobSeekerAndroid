package com.bdjobs.app.common

import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

//
// Created by Soumik on 11/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Soumik Bhattacharjee. All rights reserved.
//

abstract class CommonAdapter<T,V: ViewBinding>(

    var diffCallback : DiffUtil.ItemCallback<T>
) : ListAdapter<T, CommonViewHolder<V>>(
    AsyncDifferConfig.Builder(diffCallback)
        .build()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommonViewHolder<V> {
        val binding = createBinding(parent)
        return CommonViewHolder(binding)
    }

    protected abstract fun createBinding(parent: ViewGroup) : V

    override fun onBindViewHolder(holder: CommonViewHolder<V>, position: Int) {
        bind(holder.binding,getItem(position),position)
    }

    protected abstract fun bind(binding:V, item:T,position: Int)
}