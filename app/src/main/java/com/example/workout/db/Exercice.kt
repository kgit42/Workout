package com.example.workout.db

import android.location.Address
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

//Entität mit Default-Werten
@Entity
data class Exercice(
    @PrimaryKey(autoGenerate = true) val eid: Int = 0,
    @ColumnInfo(name = "name") val name: String? = "",
    @ColumnInfo(name = "category") val category: String? = "",
    @ColumnInfo(name = "animation") val animation: String? = "",
    @ColumnInfo(name = "audio") val audio: String? = "",
    @ColumnInfo(name = "bilateral") val bilateral: Boolean? = false,
)