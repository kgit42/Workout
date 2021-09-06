package com.example.workout.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailAddBinding
import com.example.workout.ui.exercices.SimpleStringRecyclerViewAdapter

class WorkoutDetailAddFragment : Fragment() {

    private lateinit var menuItem: MenuItem
    //private val args: WorkoutDetailAddFragmentArgs by navArgs()
    private lateinit var binding: FragmentWorkoutDetailAddBinding
    private lateinit var adapter: SimpleStringRecyclerViewAdapter
    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar
    private var pausedTime: Long = 0

    //Liste mit IDs der hinzuzufügenden Elemente
    var listToAdd: ArrayList<String> = arrayListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWorkoutDetailAddBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        setupRecyclerView()
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

        //Alle Elemente zunächst löschen, wenn Seite neu geöffnet wird
        listToAdd.clear()

        onOptionsItemSelected()

        /*
        onOptionsItemSelected()
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

    

    private fun onOptionsItemSelected() {
        toolbar = binding.toolbarDetail
        toolbar.setOnMenuItemClickListener {
            //zurück navigieren
            findNavController().navigateUp()

            //TODO: Aufruf der entspr. DB-Funktion
        }
    }


    private fun setupRecyclerView() {
        //siehe https://discuss.kotlinlang.org/t/kotlin-constructor-of-inner-class-nested-can-be-called-only-with-receiver-of-containing-class/7700
        adapter = WorkoutDetailAddFragment().SimpleStringRecyclerViewAdapter(
            arrayListOf(
                "Hallo",
                "Hallo2",
                "Hallo3",
                "Hallo4",
                "Hallo5",
                "Hallo6",
                "dsgsgg",
                "Crunches"
            )
        )
        binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }

    }




    //"inner" Schlüsselwort, um von innen auf Variablen der äußeren Klasse zugreifen zu können
    inner class SimpleStringRecyclerViewAdapter(
        private val values: List<String>
    ) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            val checkbox: CheckBox = view.findViewById(R.id.checkBox)


            override fun toString(): String {
                return super.toString() + " '" + text.text
            }

            //init-Block, um Listener für Checkbox zu setzen
            init {
                checkbox.setOnCheckedChangeListener { checkbox, isChecked ->
                    //Todo: Statt Titel die ID übergeben
                    listToAdd.add(text.toString())
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.add_exercice_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position]
            holder.text.text = values[position]

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

            }


        }

        override fun getItemCount(): Int = values.size
    }

}

