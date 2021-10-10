package com.example.workout

import android.util.Log
import com.example.workout.db.Exercice
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry
import com.example.workout.ui.home.WorkoutDetailFragment

//Klasse HelperClass dient als Zwischenspeicher von Daten, die der Nutzer anlegt,
// die aber noch nicht endgültig in die DB geschrieben werden sollen
class HelperClass {

    companion object {

        //Liste mit eventl. hinzuzufügenden Übungen (Häkchen gesetzt, aber noch nicht gespeichert)
        var _listToAdd: ArrayList<Exercice> = arrayListOf()

        //Liste mit hinzuzufügenden Übungen
        var listToAdd: ArrayList<Exercice> = arrayListOf()

        fun submitList(){
            listToAdd.addAll(_listToAdd)
        }

        /*
        //Liste mit hinzuzufügenden WorkoutEntries
        val workoutentriesToAdd = MutableLiveData<ArrayList<WorkoutEntry>>().apply {
            value = arrayListOf()
        }
         */

        //Liste mit hinzuzufügenden WorkoutEntries
        var workoutentriesToAdd: ArrayList<WorkoutEntry> = arrayListOf()

        //Liste mit ursprünglichen WorkoutEntries, die ggf. bearbeitet werden
        var workoutentriesFromDb: ArrayList<WorkoutEntry> = arrayListOf()

        //sorgt dafür, dass Elemente nur einmal von DB zur RecyclerView hinzugefügt werden
        var addedFromDb = false

        //Referenz zum Adapter
        lateinit var myAdapter: WorkoutDetailFragment.MyRecyclerViewAdapter

        //Wenn addedFromDb false ist, werden der Liste workoutentriesFromDb die Elemente aus der DB hinzugefügt.
        //Dadurch wird vermieden, dass Liste immer erneut die Elemente übernimmt
        fun addElementsFromDbIfNotDone(workout: Workout){
            if(!addedFromDb){
                workoutentriesFromDb.addAll(workout.exercices)
            }
            addedFromDb = true
        }

        fun setAdapter(adapter: WorkoutDetailFragment.MyRecyclerViewAdapter){
            myAdapter = adapter
        }

        fun updateWorkoutEntry(id: Int?, dauer: Int?, mehrsatz: Boolean?, prio: Int?, pause: Int?){
            //Element suchen in beiden Listen. Wenn gefunden, Schleife abbrechen für bessere Performance

            var found = false

            for ((index, value) in workoutentriesFromDb.withIndex()){
                if(value.weid == id){
                    workoutentriesFromDb[index].length = dauer
                    workoutentriesFromDb[index].multipleSets = mehrsatz
                    workoutentriesFromDb[index].priority = prio
                    workoutentriesFromDb[index].innerRest = pause
                    found = true
                    break
                }
            }

            if(!found){
                for ((index, value) in workoutentriesToAdd.withIndex()){
                    if(value.weid == id){
                        workoutentriesToAdd[index].length = dauer
                        workoutentriesToAdd[index].multipleSets = mehrsatz
                        workoutentriesToAdd[index].priority = prio
                        workoutentriesToAdd[index].innerRest = pause

                        break
                    }
                }
            }

        }



        fun getWorkoutEntry(id: Int?): WorkoutEntry {
            //Element suchen in beiden Listen.

            var found = false

            for ((index, value) in workoutentriesFromDb.withIndex()) {
                if (value.weid == id) {
                    return workoutentriesFromDb[index]
                }
            }

            for ((index, value) in workoutentriesToAdd.withIndex()) {
                if (value.weid == id) {
                    return workoutentriesToAdd[index]
                }
            }

            return WorkoutEntry(exercice = Exercice())
        }



        fun deleteWorkoutEntry(id: Int?) {
            //Element suchen in beiden Listen. Wenn gefunden, Schleife abbrechen für bessere Performance

            var found = false

            for ((index, value) in workoutentriesFromDb.withIndex()) {
                if (value.weid == id) {
                    workoutentriesFromDb.removeAt(index)
                    //myAdapter.addDataToBeginning()
                    myAdapter.removeElement(value)
                    found = true
                    break
                }
            }

            if(!found){
                for ((index, value) in workoutentriesToAdd.withIndex()) {
                    if (value.weid == id) {
                        workoutentriesToAdd.removeAt(index)
                        //myAdapter.addDataToBeginning()
                        myAdapter.removeElement(value)
                        break
                    }
                }
            }


        }
    }


}

