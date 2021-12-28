package com.bdjobs.app.common

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

//
// Created by Soumik on 11/8/2021.
// piyal.developer@gmail.com
// Copyright (c) 2021 Soumik Bhattacharjee. All rights reserved.
//

class CommonViewHolder<out T : ViewBinding> constructor(val binding: T) :
    RecyclerView.ViewHolder(binding.root)