package com.example.workout.ui.home

import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.HelperClass
import com.example.workout.R
import com.example.workout.databinding.FragmentWorkoutDetailAddBinding
import com.example.workout.db.Exercice

class WorkoutDetailAddFragment : Fragment() {

    private lateinit var menuItem: MenuItem
    //private val args: WorkoutDetailAddFragmentArgs by navArgs()
    private lateinit var binding: FragmentWorkoutDetailAddBinding
    private lateinit var adapter: MyRecyclerViewAdapter
    /*private val workout: Workout by lazy {
        args.workout
    }*/
    private lateinit var toolbar: Toolbar
    private var pausedTime: Long = 0

    private lateinit var homeViewModel: HomeViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        binding = FragmentWorkoutDetailAddBinding.inflate(inflater, container, false)
        /*binding.apply {
            viewModel = detailViewModel
            lifecycleOwner = viewLifecycleOwner
        }*/

        setupRecyclerView()
        setHasOptionsMenu(true)

        //Observer --> falls es Änderungen in DB gibt
        //Filter-Methode, um nur neue Exercices anzuzeigen
        homeViewModel.getAllExercices().observe(viewLifecycleOwner) { exercices ->
            adapter.setData(exercices.filter { exercice -> arguments?.getIntArray("eidArray")?.contains(exercice.eid) == false }) }

        //zunächst Liste leeren, da anfangs nichts ausgewählt
        HelperClass._listToAdd.clear()

        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbarWithNavigation()

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

    
    //Speichern-Button
    private fun onOptionsItemSelected() {
        toolbar = binding.toolbarDetail
        toolbar.setOnMenuItemClickListener {

            //Der folgende Abschnitt funktioniert so nicht, da onOptionsSelected und damit setOnMenuItemClickListener
            // augerufen wird, wenn listToAdd noch leer ist
/*
            Log.v("hhh", listToAdd.size.toString())
            //Hinzufügen der gewählten Elemente aus listToAdd zur RecyclerView, aber noch immer nicht zur DB.
            homeViewModel.listToAdd.addAll(listToAdd)
*/

            HelperClass.submitList()

            //zurück navigieren
            findNavController().navigateUp()

        }
    }


    private fun setupRecyclerView() {
        //Warum Instanziierung der Klasse WorkoutDetailAddFragment? --> siehe https://discuss.kotlinlang.org/t/kotlin-constructor-of-inner-class-nested-can-be-called-only-with-receiver-of-containing-class/7700
        //zunächst leere ArrayList erzeugen
        adapter = WorkoutDetailAddFragment().MyRecyclerViewAdapter(
            arrayListOf()
        )
        binding.apply {
            listExercices.adapter = adapter
            listExercices.layoutManager = LinearLayoutManager(listExercices.context)
        }

    }




    //"inner" Schlüsselwort, um von innen auf Variablen der äußeren Klasse zugreifen zu können
    inner class MyRecyclerViewAdapter(
        private var values: List<Exercice>
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Exercice>) {
            this.values = newData
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            val checkbox: CheckBox = view.findViewById(R.id.checkBox)


            override fun toString(): String {
                return super.toString() + " '" + text.text
            }

            /*
            //init-Block, um Listener für Checkbox zu setzen
            init {
                checkbox.setOnCheckedChangeListener { checkbox, isChecked ->
                }
            }*/
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.add_exercice_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

            }

            //Listener für Checkboxes
            holder.checkbox.setOnCheckedChangeListener {checkbox, isChecked ->
                if(isChecked){
                    //Hinzufügen der gewählten Elemente aus listToAdd zur RecyclerView, aber noch immer nicht zur DB.
                    HelperClass._listToAdd.add(values[position])
                }else{
                    //Hinzufügen der gewählten Elemente aus listToAdd zur RecyclerView, aber noch immer nicht zur DB.
                    HelperClass._listToAdd.remove(values[position])
                }

            }

        }

        override fun getItemCount(): Int = values.size
    }

}

