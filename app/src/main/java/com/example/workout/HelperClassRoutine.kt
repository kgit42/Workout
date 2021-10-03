package com.example.workout

import com.example.workout.db.Exercice
import com.example.workout.db.Routine
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry
import com.example.workout.ui.home.RoutineDetailFragment
import com.example.workout.ui.home.WorkoutDetailFragment

//Klasse HelperClass dient als Zwischenspeicher von Daten, die der Nutzer anlegt,
// die aber noch nicht endgültig in die DB geschrieben werden sollen
class HelperClassRoutine {

    companion object {

        //Liste mit allen Workouts (aus DB + neu hinzugefügte)
        var allWorkouts: ArrayList<Workout> = arrayListOf()

        //sorgt dafür, dass Elemente nur einmal von DB zur RecyclerView hinzugefügt werden
        var addedFromDb = false

        //Referenz zum Adapter
        lateinit var myAdapter: RoutineDetailFragment.MyRecyclerViewAdapter

        //Wenn addedFromDb false ist, werden der Liste workoutentriesFromDb die Elemente aus der DB hinzugefügt.
        //Dadurch wird vermieden, dass Liste immer erneut die Elemente übernimmt
        fun addElementsFromDbIfNotDoneToBeginning(routine: Routine){
            if(!addedFromDb){
                allWorkouts.addAll(0, routine.workouts)
            }
            addedFromDb = true
        }

        fun setAdapter(adapter: RoutineDetailFragment.MyRecyclerViewAdapter){
            myAdapter = adapter
        }




        fun deleteWorkout(id: Int?) {

            for ((index, value) in allWorkouts.withIndex()) {
                if (value.wid == id) {
                    allWorkouts.removeAt(index)
                    //myAdapter.addDataToBeginning()
                    myAdapter.removeElement(value)
                    break
                }
            }


        }
    }


}

