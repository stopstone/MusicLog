package com.stopstone.myapplication.data.model.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.stopstone.myapplication.base.BaseIdModel

@Entity(tableName = "search_history")
data class SearchHistory(
    @PrimaryKey(autoGenerate = true) override val id: Int = 0,
    val query: String,
    val timestamp: Long
): BaseIdModel