package com.example.workout.db

import androidx.room.TypeConverter
import com.google.gson.Gson

import com.google.gson.reflect.TypeToken


object Converters {
    @TypeConverter
    fun fromString(value: String?): ArrayList<WorkoutEntry> {
        val listType = object : TypeToken<ArrayList<WorkoutEntry?>?>() {}.type
        return Gson().fromJson(value, listType)
    }

    @TypeConverter
    fun fromArrayList(list: ArrayList<WorkoutEntry?>?): String {
        val gson = Gson()
        return gson.toJson(list)
    }
}
