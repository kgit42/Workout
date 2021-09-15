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
import com.example.workout.R
import com.example.workout.db.Workout
import kotlinx.coroutines.launch

class DeleteDialogFragment : DialogFragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        return activity?.let {
            // Use the Builder class for convenient dialog construction
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Löschen?")
                .setPositiveButton("Ja",
                    DialogInterface.OnClickListener { dialog, id ->
                        //Fallunterscheidung, je nachdem, was gelöscht werden soll

                        if(arguments?.getInt("wid") != 0){
                            //DB-Aufruf
                            lifecycleScope.launch {
                                homeViewModel.deleteWorkout(arguments?.getInt("wid")!!)
                            }
                        }

                        if(arguments?.getInt("weid") != 0){
                            HelperClass.deleteWorkoutEntry(arguments?.getInt("weid"))

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