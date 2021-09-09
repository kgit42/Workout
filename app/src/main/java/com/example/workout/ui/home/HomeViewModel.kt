package com.example.workout.ui.home

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.*
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.workout.R
import com.example.workout.db.AppDatabase
import com.example.workout.db.ExerciceDao
import com.example.workout.db.Routine
import com.example.workout.db.RoutineDao

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    // Create a LiveData with a String
    val currentName: MutableLiveData<String> by lazy {
        //db = AppDatabase.getInstance()
        MutableLiveData<String>()
    }

    //Referenz zur Datenbank
    val db = AppDatabase.getInstance(app.applicationContext)


    //val exercicesWithBreak: LiveData<List<Routine>> = db.routineDao().getAll()





    inner class SimpleStringRecyclerViewAdapter(
    ) : RecyclerView.Adapter<SimpleStringRecyclerViewAdapter.ViewHolder>() {

        //Aufruf Datenbank
        private val values: LiveData<List<Routine>> = db.routineDao().getAll()

        inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
            var boundString: String? = null
            //val image: ImageView = view.findViewById(R.id.avatar)
            val text: TextView = view.findViewById(R.id.workout_title)

            override fun toString(): String {
                return super.toString() + " '" + text.text
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(
                R.layout.routines_view_item, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            //Elvis-Operator für Null-Check:
            holder.boundString = values.value!![position].name ?: ""
            holder.text.text = values.value!![position].name ?: ""

            holder.view.setOnLongClickListener { v ->
                val context = v.context
                /*val intent = Intent(context, CheeseDetailActivity::class.java)
                intent.putExtra(CheeseDetailActivity.EXTRA_NAME, holder.boundString)
                context.startActivity(intent)*/

                //navigiert zur Detail-Seite und übergibt das jeweilige Workout/die Routine
                val args = Bundle()
                args.putParcelable("workout", null)
                holder.view.findNavController().navigate(R.id.navigation_routine_detail, args)
                return@setOnLongClickListener true
            }

        }

        override fun getItemCount(): Int {
            if(values.value == null){
                return 0
            }else{
                return values.value!!.size
            }

        }

    }
}


