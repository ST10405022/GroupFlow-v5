package com.example.groupflow.data

import androidx.room.TypeConverter
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

/**
 * Converters to allow Room to handle unsupported types like java.util.Date and LocalDateTime.
 */
class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time

    @TypeConverter
    fun fromLocalDateTime(value: Long?): LocalDateTime? =
        value?.let {
            Date(it).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        }

    @TypeConverter
    fun localDateTimeToTimestamp(dateTime: LocalDateTime?): Long? = dateTime?.atZone(ZoneId.systemDefault())?.toInstant()?.toEpochMilli()

    @TypeConverter
    fun fromString(value: String?): Map<String, Boolean>? {
        if (value == null) return null
        val mapType = object : TypeToken<Map<String, Boolean>>() {}.type
        return Gson().fromJson(value, mapType)
    }

    @TypeConverter
    fun fromMap(map: Map<String, Boolean>?): String? {
        if (map == null) return null
        return Gson().toJson(map)
    }
}
