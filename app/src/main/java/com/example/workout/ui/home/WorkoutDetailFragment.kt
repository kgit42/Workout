package com.example.workout.ui.home

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailBinding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.workout.HelperClass
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.Exception
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.example.workout.WorkoutDetailFragmentAdapterInterface
import com.example.workout.db.Routine
import kotlinx.coroutines.Dispatchers


class WorkoutDetailFragment : Fragment() {

    private lateinit var menuItem: MenuItem

    private lateinit var binding: FragmentWorkoutDetailBinding
    private lateinit var adapter: MyRecyclerViewAdapter

    private lateinit var homeViewModel: HomeViewModel

    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentWorkoutDetailBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        //Listener für den Hinzufügen-Button:
        binding.button.setOnClickListener(View.OnClickListener {
            //navigiert zur Add-Seite und übergibt ein Array mit den aktuell vorhandenen Exercice IDs,
            // damit keine doppelt hinzugefügt werden können
            val args = Bundle()
            var intarray: IntArray = IntArray(adapter.getElements().size)

            for ((index, value) in adapter.getElements().withIndex()) {
                intarray[index] = value.exercice.eid
            }

            args.putIntArray("eidArray", intarray)
            findNavController().navigate(
                com.example.workout.R.id.navigation_workout_detail_add,
                args
            )
        })

        //Observer --> falls es Änderungen in DB gibt
        //nur wenn bestehendes Workout bearbeitet werden soll, muss mit der Datenbank abgeglichen werden.
        //Es werden Funktionen zum Füllen der EditTexts sowie der RecyclerView ausgeführt. Außerdem wird Inhalt von DB in HelperClass
        //abgelegt.
        if (arguments?.getInt("wid") != null) {

            homeViewModel.getWorkoutById(arguments?.getInt("wid"))
                .observe(viewLifecycleOwner) { workout ->
                    HelperClass.addElementsFromDbIfNotDone(workout)

                    //Bestehende Daten an den Anfang der Liste setzen, dahinter kommen die neu hinzuzufügenden Elemente.
                    adapter.addDataToBeginning()

                    //Textboxen befüllen
                    fillWithData(workout)
                }

        } else {
            binding.toolbarDetail.title = "Neues Workout"
        }


        setupRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbarWithNavigation()
        onOptionsItemSelected()
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
                val name = binding.name.text.toString()
                var anzahl = Integer.parseInt(binding.anzahl1.text.toString())
                val pause1 = Integer.parseInt(binding.pause1.text.toString())
                val pause2 = Integer.parseInt(binding.pause2.text.toString())

                if (pause1 < 5 || pause2 < 5) {
                    throw Exception()
                }

                if (anzahl < 0 || anzahl > 1000) {
                    throw Exception()
                }

                if (name == "") {
                    throw Exception()
                }

                val exercices = adapter.getElements()

                //Leere Liste vermeiden
                if (exercices.size == 0) {
                    throw Exception()
                }


                //Fallunterscheidung je nachdem, ob neues Workout oder Änderung eines bestehenden
                if (arguments?.getInt("wid") != null) {

                    //DB-Aufruf
                    var newWorkout = Workout(
                        arguments?.getInt("wid")!!,
                        name,
                        0,
                        anzahl,
                        pause1,
                        pause2,
                        exercices,
                        arrayListOf()
                    )
                    lifecycleScope.launch(Dispatchers.IO) {
                        homeViewModel.updateWorkout(
                            newWorkout
                        )

                        //Workout auch in allen Routinen updaten
                        val routines = homeViewModel.getAllRoutinesAsync()
                        Log.v("hhh", routines.toString())

                        routines.forEach { routine ->
                            var workouts = routine.workouts

                            workouts.forEachIndexed { index, workout ->
                                if (workout.wid == arguments?.getInt("wid")!!) {
                                    workouts[index] = newWorkout
                                }
                            }

                            val newRoutine = Routine(
                                routine.rid, routine.name,
                                routine.restWorkouts, workouts
                            )

                            homeViewModel.updateRoutine(newRoutine)

                        }
                    }
                } else {
                    //DB-Aufruf
                    lifecycleScope.launch {
                        homeViewModel.createWorkout(
                            Workout(
                                0,
                                name,
                                0,
                                anzahl,
                                pause1,
                                pause2,
                                exercices,
                                arrayListOf()
                            )
                        )
                    }
                }
            } catch (e: Exception) {
                Log.v("hhh", "Error", e)
                Snackbar.make(requireView(), "Die Eingaben sind fehlerhaft.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }


            //zurück navigieren
            findNavController().navigateUp()

        }
    }

    private fun setupRecyclerView() {
        adapter = MyRecyclerViewAdapter(Workout())
        binding.apply {
            addExerciceList.adapter = adapter
            addExerciceList.isNestedScrollingEnabled = false
            addExerciceList.layoutManager = LinearLayoutManager(addExerciceList.context)

            HelperClass.setAdapter(adapter)

            lifecycleScope.launch {

                HelperClass.listToAdd.forEach {

                    //Ein neues WorkoutEntry dieser Übung in DB anlegen und ID zurückgeben lassen
                    var id = homeViewModel.createWorkoutEntry(WorkoutEntry(exercice = it))

                    //Neues WorkoutEntry mit der erhaltenen ID der HelperClass hinzufügen
                    val workoutentry = WorkoutEntry(id.toInt(), exercice = it)
                    HelperClass.workoutentriesToAdd.add(workoutentry)

                }

                //HelperClass.workoutentriesToAdd.setValue(newWorkoutentriesToAdd)
                //Log.v("hhh", HelperClass.workoutentriesToAdd.toString())


                //alle hinzuzufügenden Elemente aus HelperClass dem Adapter der RecyclerView hinzufügen
                HelperClass.workoutentriesToAdd.forEach {
                    adapter.addElement(it)
                }

                //Elemente aus listToAdd löschen. Verwendung eines Iterators, da es
                // sonst zu ConcurrentModificationException kommt
                val iterator = HelperClass.listToAdd.iterator()
                while (iterator.hasNext()) {
                    var ex = iterator.next()
                    iterator.remove()
                }

            }

        }
    }

    //füllt EditTexts aus mit bestehenden Daten aus DB
    fun fillWithData(workout: Workout) {
        binding.name.setText(workout.name)
        binding.anzahl1.setText(workout.numberSets.toString())
        binding.pause1.setText(workout.restExercices.toString())
        binding.pause2.setText(workout.restSets.toString())

        binding.toolbarDetail.title = workout.name
    }


    inner class MyRecyclerViewAdapter(
        private var values: Workout
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>(),
        WorkoutDetailFragmentAdapterInterface {

        fun addDataToBeginning() {
            this.values.exercices.addAll(0, HelperClass.workoutentriesFromDb)
            notifyDataSetChanged()
        }

        /*
        fun setData(newData: Workout) {
            this.values = newData
            notifyDataSetChanged()
        }
         */

        //um einzelne Elemente hinzuzufügen, BEVOR sie evtl. zur DB hinzugefügt werden
        fun addElement(element: WorkoutEntry) {
            this.values.exercices.add(element)
            notifyItemInserted(values.exercices.size - 1)
        }

        override fun removeElement(element: WorkoutEntry) {
            this.values.exercices.remove(element)
            notifyDataSetChanged()
        }

        fun getElements(): ArrayList<WorkoutEntry> {
            return values.exercices
        }


        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null

            val image: ImageView = view.findViewById(com.example.workout.R.id.item_image)
            val text: TextView = view.findViewById(com.example.workout.R.id.item_title)
            val category: TextView = view.findViewById(com.example.workout.R.id.item_category)
            val time: TextView = view.findViewById(com.example.workout.R.id.item_time)

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
            holder.boundString = values.exercices[position].exercice.name
            holder.text.text = values.exercices[position].exercice.name
            holder.category.text = values.exercices[position].exercice.category
            holder.time.text =
                values.exercices[position].length.toString() + "s " + (if(values.exercices[position].exercice.bilateral == true) "(" + values.exercices[position].innerRest + "s) " else ("")) + "| " + values.exercices[position].priority + if (values.exercices[position].multipleSets == true) "M" else ("")

            //Bild suchen
            val res: Resources = resources
            val mDrawableName1 = values.exercices[position].exercice.animation
            if (!mDrawableName1.equals("")) {
                //Dateiendung entfernen
                val mDrawableName = mDrawableName1?.substring(0, mDrawableName1.lastIndexOf('.'))
                val resID: Int =
                    res.getIdentifier(mDrawableName, "drawable", context?.getPackageName())
                val drawable: Drawable? = ContextCompat.getDrawable(context!!, resID)
                //Bild setzen
                holder.image.setImageDrawable(drawable)
            } else {
                holder.image.setImageResource(com.example.workout.R.drawable.ic_baseline_image_24)
            }


            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt die jeweilige Übung
                val args = Bundle()
                args.putInt("weid", values.exercices[position].weid)
                holder.view.findNavController()
                    .navigate(com.example.workout.R.id.navigation_workout_detail_exercice, args)
            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener { v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("weid", values.exercices[position].weid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int = values.exercices.size

    }
}


