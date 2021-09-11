package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class WorkoutEntry(
    @PrimaryKey val weid: Int,
    @ColumnInfo(name = "length") val length: Int?,
    @ColumnInfo(name = "multiple_sets") val multipleSets: Boolean,
    @ColumnInfo(name = "priority") val priority: Int?,
    @ColumnInfo(name = "inner_rest") val innerRest: Int?,
    @Embedded val exercice: Exercice

)