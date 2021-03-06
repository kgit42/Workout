package com.example.workout.ui.home

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation.findNavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.workout.HelperClass
import com.example.workout.HelperClassRoutine
import com.example.workout.R
import com.example.workout.db.Routine
import com.example.workout.db.Workout
import com.example.workout.ui.stats.StatsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DeleteDialogFragment : DialogFragment() {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var statsViewModel: StatsViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        statsViewModel = ViewModelProvider(this).get(StatsViewModel::class.java)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Löschen?")
                .setPositiveButton("Ja",
                    DialogInterface.OnClickListener { dialog, id ->
                        //Fallunterscheidung, je nachdem, was gelöscht werden soll

                        //Workout löschen
                        if (arguments?.getInt("wid") != 0) {
                            //DB-Aufruf
                                runBlocking {
                                    launch(Dispatchers.IO) {
                                        homeViewModel.deleteWorkout(arguments?.getInt("wid")!!)

                                        //Workout auch aus allen Routinen löschen
                                        val routines = homeViewModel.getAllRoutinesAsync()
                                        Log.v("hhh", routines.toString())

                                        routines.forEach { routine ->
                                            var workouts = routine.workouts

                                            workouts.forEach { workout ->
                                                if (workout.wid == arguments?.getInt("wid")!!) {
                                                    workouts.remove(workout)
                                                }
                                            }

                                            val newRoutine = Routine(
                                                routine.rid, routine.name,
                                                routine.restWorkouts, workouts
                                            )

                                            homeViewModel.updateRoutine(newRoutine)

                                        }

                                    }
                                }

                        }

                        //Workoutentry löschen (noch nicht aus DB)
                        if (arguments?.getInt("weid") != 0) {
                            HelperClass.deleteWorkoutEntry(arguments?.getInt("weid"))

                        }

                        //Routine löschen
                        if (arguments?.getInt("rid") != 0) {
                            //DB-Aufruf
                            lifecycleScope.launch {
                                homeViewModel.deleteRoutine(arguments?.getInt("rid")!!)
                            }
                        }


                        //Workout aus Routine löschen (noch nicht aus DB)
                        if (arguments?.getInt("wid+") != 0) {
                            HelperClassRoutine.deleteWorkout(arguments?.getInt("wid+"))

                        }

                        //RoutineWorkoutStatsElement löschen
                        if (arguments?.getInt("seid") != 0) {
                            //DB-Aufruf
                            lifecycleScope.launch {
                                statsViewModel.deleteRoutineWorkoutStatsElement(arguments?.getInt("seid")!!)
                            }
                        }


                    })
                .setNegativeButton("Nein",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            // Create the AlertDialog object and return it
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}