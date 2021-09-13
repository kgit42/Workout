package com.example.workout

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
        var workoutentriesToAdd: ArrayList<WorkoutEntry> = arrayListOf()
    }


}

