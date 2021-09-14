package com.example.workout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.workout.db.Exercice
import com.example.workout.db.WorkoutEntry

class HelperClass {

    companion object {
        //Liste mit eventl. hinzuzufügenden Übungen
        var _listToAdd: ArrayList<Exercice> = arrayListOf()

        //Liste mit hinzuzufügenden Übungen
        var listToAdd: ArrayList<Exercice> = arrayListOf()

        fun submitList(){
            listToAdd.addAll(_listToAdd)
        }

        //Liste mit hinzuzufügenden WorkoutEntries
        val workoutentriesToAdd = MutableLiveData<ArrayList<WorkoutEntry>>().apply {
            value = arrayListOf()
        }
    }


}

