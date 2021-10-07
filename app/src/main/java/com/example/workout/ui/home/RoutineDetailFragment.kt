package com.example.workout.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClassRoutine
import com.example.workout.R
import com.example.workout.databinding.FragmentRoutineDetailBinding
import com.example.workout.db.Routine
import com.example.workout.db.Workout
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import java.lang.Exception

class RoutineDetailFragment : Fragment(), OnDragStartListener {

    private lateinit var menuItem: MenuItem

    private lateinit var binding: FragmentRoutineDetailBinding
    private lateinit var adapter: MyRecyclerViewAdapter

    private lateinit var homeViewModel: HomeViewModel

    private lateinit var toolbar: Toolbar

    private var mItemTouchHelper: ItemTouchHelper? = null


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
                R.id.navigation_routine_detail_add
            )
        })

        //Observer --> falls es Änderungen in DB gibt
        //nur wenn bestehende Routine bearbeitet werden soll, muss mit der Datenbank abgeglichen werden.
        //Es werden Funktionen zum Füllen der EditTexts sowie der RecyclerView ausgeführt. Außerdem wird Inhalt von DB in HelperClass
        //abgelegt.
        if (arguments?.getInt("rid") != null) {

            homeViewModel.getRoutineById(arguments?.getInt("rid"))
                .observe(viewLifecycleOwner) { routine ->
                    HelperClassRoutine.addElementsFromDbIfNotDoneToBeginning(routine)

                    //RecyclerView aktualisieren
                    setupRecyclerView()

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

                if(pause1 < 5){
                    throw Exception()
                }

                if(name == "") {
                    throw Exception()
                }

                val exercices = adapter.getElements()

                /*
                //Leere Liste vermeiden, würde später zu Fehler führen
                if(exercices.size == 0){
                    return@setOnMenuItemClickListener false
                }

                 */

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
                Snackbar.make(requireView(), "Die Eingaben sind fehlerhaft.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }


            //zurück navigieren
            findNavController().navigateUp()

        }
    }

    private fun setupRecyclerView() {
        adapter = MyRecyclerViewAdapter(Routine(), this)
        binding.apply {
            addWorkoutsList.adapter = adapter
            addWorkoutsList.isNestedScrollingEnabled = false
            addWorkoutsList.layoutManager = LinearLayoutManager(addWorkoutsList.context)

            HelperClassRoutine.setAdapter(adapter)

            //Drag N Drop-Funktionalität
            val callback: ItemTouchHelper.Callback = SimpleItemTouchHelperCallback(adapter)
            mItemTouchHelper = ItemTouchHelper(callback)
            mItemTouchHelper!!.attachToRecyclerView(addWorkoutsList)


                //alle hinzuzufügenden Elemente aus HelperClass dem Adapter der RecyclerView hinzufügen
                HelperClassRoutine.allWorkouts.forEach {
                    adapter.addElement(it)
                }


        }
    }

    //füllt EditTexts aus mit bestehenden Daten aus DB
    fun fillWithData(routine: Routine) {
        binding.nameRoutine.setText(routine.name)
        binding.restWorkouts.setText(routine.restWorkouts.toString())

        binding.toolbarDetail.title = routine.name
    }


    override fun onDragStarted(viewHolder: RecyclerView.ViewHolder?) {
        mItemTouchHelper!!.startDrag(viewHolder!!)
    }


    inner class MyRecyclerViewAdapter(
        private var values: Routine,
        private var mDragStartListener: OnDragStartListener
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>(), ItemTouchHelperAdapter {

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

            val image: ImageView = view.findViewById(R.id.item_image)
            val text: TextView = view.findViewById(R.id.item_title)
            val category: TextView = view.findViewById(R.id.item_category)

            val handleView: ImageView = view.findViewById((R.id.handle))

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                com.example.workout.R.layout.reorder_view_item, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values.workouts[position].name
            holder.text.text = values.workouts[position].name

            val numberExercices = values.workouts[position].exercices.size
            if(numberExercices > 1){
                holder.category.text = "$numberExercices Übungen"
            }else{
                holder.category.text = "$numberExercices Übung"
            }

            holder.image.setImageResource(R.drawable.ic_baseline_fitness_center_24)

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

            //OnTouchListener registrieren für Drag N Drop
            holder.handleView.setOnTouchListener { v, event ->
                if (event.getActionMasked() ==
                    MotionEvent.ACTION_DOWN
                ) {
                    mDragStartListener.onDragStarted(holder)
                }
                false
            }

        }

        //Callback, wenn Position eines Elementes geändert
        override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
            val prev: Workout = values.workouts.removeAt(fromPosition)
            HelperClassRoutine.allWorkouts.removeAt(fromPosition)
            values.workouts.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, prev)
            HelperClassRoutine.allWorkouts.add(if (toPosition > fromPosition) toPosition - 1 else toPosition, prev)
            notifyItemMoved(fromPosition, toPosition)

            return true
        }

        override fun getItemCount(): Int = values.workouts.size

    }

}


interface OnDragStartListener {
    fun onDragStarted(viewHolder: RecyclerView.ViewHolder?)
}

