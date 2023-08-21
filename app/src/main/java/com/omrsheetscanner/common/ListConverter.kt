package com.omrsheetscanner.common

import androidx.room.TypeConverter
import com.google.gson.Gson

class ListConverter {

    @TypeConverter
    fun fromIntList(value: List<Int>): String = Gson().toJson(value)

    @TypeConverter
    fun toIntList(value: String): List<Int> =
        try {
            Gson().fromJson<List<Int>>(value)
        } catch (e: Exception) {
            emptyList()
        }
}