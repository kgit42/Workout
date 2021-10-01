package com.example.workout.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.db.Routine

class RoutinesFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Referenz zum ViewModel beschaffen
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val rv = inflater.inflate(
            R.layout.fragment_list,
            container,
            false
        ) as RecyclerView
        rv.layoutManager = LinearLayoutManager(rv.context)

        //zunächst leere ArrayList mit Routinen
        val adapter = MyRecyclerViewAdapter(arrayListOf(Routine()))
        rv.adapter = adapter


        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllRoutines().observe(viewLifecycleOwner) { routines -> adapter.setData(routines) }

        return rv
    }


    inner class MyRecyclerViewAdapter(
        private var values: List<Routine>
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Routine>) {
            this.values = newData
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null

            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            val startButton: Button = view.findViewById(R.id.buttonPlay)
            val progressbar: ProgressBar = view.findViewById(R.id.progress_loader)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workouts_routines_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
                val args = Bundle()
                args.putInt("rid", values[position].rid)
                holder.view.findNavController().navigate(R.id.navigation_routine_detail, args)
            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener { v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("rid", values[position].rid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

            //Start-Button OnClickListener
            holder.startButton.setOnClickListener { v ->

                //Ladesymbol einblenden
                holder.startButton.visibility = View.INVISIBLE
                holder.progressbar.visibility = View.VISIBLE

                //Workout generieren.



            }

        }

        override fun getItemCount(): Int {
                return values.size

        }

    }
}

