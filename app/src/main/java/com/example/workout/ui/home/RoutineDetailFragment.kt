package com.example.workout.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClassRoutine
import com.example.workout.databinding.FragmentRoutineDetailBinding
import com.example.workout.db.Routine
import com.example.workout.db.Workout
import kotlinx.coroutines.launch
import java.lang.Exception

class RoutineDetailFragment : Fragment() {

    private lateinit var menuItem: MenuItem

    private lateinit var binding: FragmentRoutineDetailBinding
    private lateinit var adapter: MyRecyclerViewAdapter

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        //Listener für den Hinzufügen-Button:
        binding.button.setOnClickListener(View.OnClickListener {
            //navigiert zur Add-Seite

            findNavController().navigate(
                com.example.workout.R.id.navigation_routine_detail_add
            )
        })

        //Observer --> falls es Änderungen in DB gibt
        //nur wenn bestehende Routine bearbeitet werden soll, muss mit der Datenbank abgeglichen werden.
        //Es werden Funktionen zum Füllen der EditTexts sowie der RecyclerView ausgeführt. Außerdem wird Inhalt von DB in HelperClass
        //abgelegt.
        if (arguments?.getInt("rid") != null) {

            homeViewModel.getRoutineById(arguments?.getInt("rid"))
                .observe(viewLifecycleOwner) { routine ->
                    HelperClassRoutine.addElementsFromDbIfNotDone(routine)

                    //Bestehende Daten an den Anfang der Liste setzen, dahinter kommen die neu hinzuzufügenden Elemente.
                    adapter.addDataToBeginning()
                    fillWithData(routine)
                }

        } else {
            binding.toolbarDetail.title = "Neue Routine"
        }


        setupRecyclerView()
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

        onOptionsItemSelected()


        /*
        detailViewModel.start(workout.id)

        detailViewModel.workout.observe(viewLifecycleOwner) { workout ->
            updateMenuItemIcon(workout.isSaved)
        }

        detailViewModel.workoutTimeMillis.observe(viewLifecycleOwner) { workoutTimeMillis ->
            binding.workoutProgress.setDuration(workoutTimeMillis)
        }

        detailViewModel.savedPausedTime.observe(viewLifecycleOwner) { savedPausedTime ->
            detailViewModel.manageTimer(savedPausedTime)
        }

        detailViewModel.runningTime.observe(viewLifecycleOwner) {
            binding.workoutProgress.updateProgressBar(it)
        }

        detailViewModel.pausedWorkoutTimeMillis.observe(viewLifecycleOwner) {
            pausedTime = it
        }

         */
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
                val name = binding.nameRoutine.text.toString()
                val pause1 = Integer.parseInt(binding.restWorkouts.text.toString())

                val exercices = adapter.getElements()

                //Fallunterscheidung je nachdem, ob neues Workout oder Änderung eines bestehenden
                if (arguments?.getInt("rid") != null) {

                    //DB-Aufruf
                    lifecycleScope.launch {
                        homeViewModel.updateRoutine(
                            Routine(
                                arguments?.getInt("rid")!!,
                                name,
                                pause1,
                                exercices
                            )
                        )
                    }
                } else {
                    //DB-Aufruf
                    lifecycleScope.launch {
                        homeViewModel.createRoutine(
                            Routine(
                                0,
                                name,
                                pause1,
                                exercices
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.v("hhh", "Error", e)
            }


            //zurück navigieren
            findNavController().navigateUp()

        }
    }

    private fun setupRecyclerView() {
        adapter = MyRecyclerViewAdapter(Routine())
        binding.apply {
            addWorkoutsList.adapter = adapter
            addWorkoutsList.isNestedScrollingEnabled = false
            addWorkoutsList.layoutManager = LinearLayoutManager(addWorkoutsList.context)

            HelperClassRoutine.setAdapter(adapter)

            lifecycleScope.launch {

                //alle hinzuzufügenden Elemente aus HelperClass dem Adapter der RecyclerView hinzufügen
                HelperClassRoutine.workoutsToAdd.forEach {
                    adapter.addElement(it)
                }

            }

        }
    }

    //füllt EditTexts aus mit bestehenden Daten aus DB
    fun fillWithData(routine: Routine) {
        binding.nameRoutine.setText(routine.name)
        binding.restWorkouts.setText(routine.restWorkouts.toString())

        binding.toolbarDetail.title = routine.name
    }


    inner class MyRecyclerViewAdapter(
        private var values: Routine
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        fun addDataToBeginning() {
            this.values.workouts.addAll(0, HelperClassRoutine.workoutsFromDb)
            notifyDataSetChanged()
        }

        /*
        fun setData(newData: Workout) {
            this.values = newData
            notifyDataSetChanged()
        }
         */

        //um einzelne Elemente hinzuzufügen, BEVOR sie evtl. zur DB hinzugefügt werden
        fun addElement(element: Workout) {
            this.values.workouts.add(element)
            notifyItemInserted(values.workouts.size - 1);
        }

        fun removeElement(element: Workout){
            this.values.workouts.remove(element)
            notifyDataSetChanged()
        }

        fun getElements(): ArrayList<Workout> {
            return values.workouts
        }



        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null

            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(com.example.workout.R.id.workout_title)
            val category: TextView = view.findViewById(com.example.workout.R.id.workout_category)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                com.example.workout.R.layout.view_item, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values.workouts[position].name
            holder.text.text = values.workouts[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/
            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener{ v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("wid+", values.workouts[position].wid)
                args.putInt("rid+", values.rid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int = values.workouts.size

    }

}

