package com.stopstone.myapplication.base

import androidx.recyclerview.widget.DiffUtil

abstract class BaseDiffCallback<T : Any> : DiffUtil.ItemCallback<T>() {
    abstract fun getItemId(item: T): Any
    abstract fun areContentsEqual(oldItem: T, newItem: T): Boolean

    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return getItemId(oldItem) == getItemId(newItem)
    }

    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return areContentsEqual(oldItem, newItem)
    }
}