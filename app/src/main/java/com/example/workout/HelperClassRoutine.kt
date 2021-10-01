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

        //Liste mit hinzuzufügenden Workouts
        var workoutsToAdd: ArrayList<Workout> = arrayListOf()

        //Liste mit ursprünglichen Workouts aus DB, die ggf. neu angeordnet werden
        var workoutsFromDb: ArrayList<Workout> = arrayListOf()

        //sorgt dafür, dass Elemente nur einmal von DB zur RecyclerView hinzugefügt werden
        var addedFromDb = false

        //Referenz zum Adapter
        lateinit var myAdapter: RoutineDetailFragment.MyRecyclerViewAdapter

        //Wenn addedFromDb false ist, werden der Liste workoutentriesFromDb die Elemente aus der DB hinzugefügt.
        //Dadurch wird vermieden, dass Liste immer erneut die Elemente übernimmt
        fun addElementsFromDbIfNotDone(routine: Routine){
            if(!addedFromDb){
                workoutsFromDb.addAll(routine.workouts)
            }
            addedFromDb = true
        }

        fun setAdapter(adapter: RoutineDetailFragment.MyRecyclerViewAdapter){
            myAdapter = adapter
        }




        fun deleteWorkout(id: Int?) {
            //Element suchen in beiden Listen. Wenn gefunden, Schleife abbrechen für bessere Performance

            var found = false

            for ((index, value) in workoutsFromDb.withIndex()) {
                if (value.wid == id) {
                    workoutsFromDb.removeAt(index)
                    //myAdapter.addDataToBeginning()
                    myAdapter.removeElement(value)
                    found = true
                    break
                }
            }

            if(!found){
                for ((index, value) in workoutsToAdd.withIndex()) {
                    if (value.wid == id) {
                        workoutsToAdd.removeAt(index)
                        //myAdapter.addDataToBeginning()
                        myAdapter.removeElement(value)
                        break
                    }
                }
            }


        }
    }


}

