package com.example.workout.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClass
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailExerciceBinding
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.Exception

class WorkoutDetailExerciceFragment : Fragment() {

    private lateinit var menuItem: MenuItem

    //private val args: WorkoutDetailExerciceFragmentArgs by navArgs()
    private lateinit var binding: FragmentWorkoutDetailExerciceBinding

    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentWorkoutDetailExerciceBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        binding.chip1.setOnClickListener {
            // Responds to chip click
        }


        fillWithData(HelperClass.getWorkoutEntry(arguments?.getInt("weid")))

        /*homeViewModel.getWorkoutEntryById(arguments?.getInt("weid"))
            .observe(viewLifecycleOwner) { workoutentry ->
                fillWithData(workoutentry)
            }*/


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithNavigation()
        onOptionsItemSelected()
    }


    fun fillWithData(workoutentry: WorkoutEntry) {
        binding.dauer.setText(workoutentry.length.toString())

        //nur anzeigen, wenn bilaterale Übung
        if (!workoutentry.exercice.bilateral!!) {
            binding.pause3.visibility = INVISIBLE
        } else {
            binding.pause3.setText(workoutentry.innerRest.toString())
        }

        binding.mehrsatz.isChecked = workoutentry.multipleSets == true

        when (workoutentry.priority) {
            0 -> binding.prio.check(binding.chip1.id)
            1 -> binding.prio.check(binding.chip2.id)
            2 -> binding.prio.check(binding.chip3.id)
        }

        binding.toolbarDetail.title = workoutentry.exercice.name

        if(arguments?.getBoolean("superset") != true){
            if(workoutentry.customRest != -1) {
                binding.restCustom.setText(workoutentry.customRest.toString())
            }
        }else {
            binding.restCustom.visibility = INVISIBLE
        }
    }

    private fun setupToolbarWithNavigation() {
        toolbar = binding.toolbarDetail
        toolbar.navigationContentDescription = "Navigate Up"
        toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }


    //Speichern-Button
    private fun onOptionsItemSelected() {
        toolbar = binding.toolbarDetail
        toolbar.setOnMenuItemClickListener {

            //Zusammensuchen der nötigen Daten. Abfangen von fehlerhaften Eingaben
            try {
                val pause: Int?
                if (binding.pause3.visibility == VISIBLE) {
                    pause = Integer.parseInt(binding.pause3.text.toString())
                } else {
                    pause = null
                }
                val dauer = Integer.parseInt(binding.dauer.text.toString())
                val mehrsatz = binding.mehrsatz.isChecked
                var prio = 0

                val customRest: Int
                if(binding.restCustom.text.toString() != ""){
                    customRest = Integer.parseInt(binding.restCustom.text.toString())
                }else{
                    customRest = -1
                }

                when (binding.prio.checkedChipId) {
                    binding.chip1.id -> prio = 0
                    binding.chip2.id -> prio = 1
                    binding.chip3.id -> prio = 2
                }

                if (pause != null && pause < 5 || dauer < 5 || customRest in 0..4) {
                    throw Exception()
                }

                //Noch kein DB-Aufruf, da Änderungen noch verworfen werden können

                HelperClass.updateWorkoutEntry(
                    arguments?.getInt("weid"),
                    dauer,
                    mehrsatz,
                    prio,
                    pause,
                    customRest
                )


            } catch (e: Exception) {
                Log.v("hhh", "Error", e)
                Snackbar.make(requireView(), "Die Eingaben sind fehlerhaft.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }


            //zurück navigieren
            findNavController().navigateUp()

        }
    }

}

