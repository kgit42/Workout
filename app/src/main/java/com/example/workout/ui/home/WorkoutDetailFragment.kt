package com.example.workout.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailBinding
import android.R
import android.util.Log
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import com.example.workout.HelperClass
import com.example.workout.db.Exercice
import com.example.workout.db.Workout
import com.example.workout.db.WorkoutEntry


class WorkoutDetailFragment : Fragment() {

    private lateinit var menuItem: MenuItem

    private lateinit var binding: FragmentWorkoutDetailBinding
    private lateinit var adapter: SimpleStringRecyclerViewAdapter

    private lateinit var homeViewModel: HomeViewModel

    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar
    private var pausedTime: Long = 0


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
            //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
            val args = Bundle()
            args.putParcelable("workout", null)
            findNavController().navigate(
                com.example.workout.R.id.navigation_workout_detail_add,
                args
            )
        })

        //Observer --> falls es Änderungen in DB gibt
        //nur wenn bestehendes Workout bearbeitet werden soll, muss mit der Datenbank abgeglichen werden
        if(arguments?.getInt("wid") != null){
            homeViewModel.getById(arguments?.getInt("wid"))
                .observe(viewLifecycleOwner) { workout -> adapter.setData(workout) }
        }else{
            binding.toolbarDetail.title = "Neues Workout"
        }


        setupRecyclerView()
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

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

/*
    private fun onOptionsItemSelected() {
        toolbar.setOnMenuItemClickListener {
            detailViewModel.setFavourite(workout)
            true
        }
    }*/

    private fun setupRecyclerView() {
        adapter = SimpleStringRecyclerViewAdapter(Workout())
        binding.apply {
            addExerciceList.adapter = adapter
            addExerciceList.layoutManager = LinearLayoutManager(addExerciceList.context)

            HelperClass.listToAdd.forEach{
                //TODO: DB-Add von WorkoutEntry und ID zurückgeben lassen und in HelperClass zwischenspeichern, aus listToAdd löschen
                adapter.addElement(WorkoutEntry(0, 55, false, 0, 0, it))
            }

            //Todo: noch eine foreach-Schleife für die WorkoutEntries

        }
    }


    class SimpleStringRecyclerViewAdapter(
        private var values: Workout
    ) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: Workout) {
            this.values = newData
            notifyDataSetChanged()
        }

        //um einzelne Elemente hinzuzufügen, BEVOR sie evtl. zur DB hinzugefügt werden
        fun addElement(element: WorkoutEntry){
            this.values.exercices.add(element)
            notifyItemInserted(values.exercices.size - 1);
        }

        class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null

            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(com.example.workout.R.id.workout_title)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                com.example.workout.R.layout.routines_view_item, parent, false
            )
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values.exercices[position].exercice.name
            holder.text.text = values.exercices[position].exercice.name

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt die jeweilige Übung
                val args = Bundle()
                args.putParcelable("workout", null)
                holder.view.findNavController()
                    .navigate(com.example.workout.R.id.navigation_workout_detail_exercice, args)
            }

        }

        override fun getItemCount(): Int = values.exercices.size
    }
}


