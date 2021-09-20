package com.example.workout.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.CastActivity
import com.example.workout.R
import com.example.workout.db.Workout

class WorkoutsFragment : Fragment() {
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

        //zunächst leere ArrayList mit Workouts
        val adapter =
            MyRecyclerViewAdapter(arrayListOf(Workout()))
        rv.adapter = adapter


        //Observer --> falls es Änderungen in DB gibt
        homeViewModel.getAllWorkouts().observe(viewLifecycleOwner) { workouts -> adapter.setData(workouts) }

        return rv
    }



    inner class MyRecyclerViewAdapter(
        private var values: List<Workout>
    ) : RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder>() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(newData: List<Workout>) {
            this.values = newData
            notifyDataSetChanged()
        }

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            val startButton: Button = view.findViewById(R.id.buttonPlay)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.workouts_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.boundString = values[position].name
            holder.text.text = values[position].name

            holder.view.setOnClickListener { v ->
                val context = v.context

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine (bzw. die ID)
                val args = Bundle()
                args.putInt("wid", values[position].wid)
                holder.view.findNavController().navigate(R.id.navigation_workout_detail, args)
            }

            //OnLongClickListener zum Löschen
            holder.view.setOnLongClickListener{ v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("wid", values[position].wid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }

            //Start-Button OnClickListener
            holder.startButton.setOnClickListener{v ->
                val intent = Intent(context, CastActivity::class.java)
                //intent.putExtra(CastActivity.EXTRA_NAME, holder.boundString)
                context?.startActivity(intent)
            }

        }

        override fun getItemCount(): Int = values.size




    }
}
