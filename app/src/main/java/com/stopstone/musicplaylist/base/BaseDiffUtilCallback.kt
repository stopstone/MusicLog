package com.stopstone.musicplaylist.base

import androidx.recyclerview.widget.DiffUtil

class BaseDiffCallback<T : BaseIdModel> : DiffUtil.ItemCallback<T>() {
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}

interface BaseIdModel {
    val id: Any
    override fun equals(other: Any?): Boolean
}