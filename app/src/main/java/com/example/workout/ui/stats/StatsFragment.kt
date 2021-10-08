package com.example.workout.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.workout.databinding.FragmentStatsBinding
import com.example.workout.R
import android.content.Context
import android.graphics.Typeface
import android.util.Log
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.workout.db.Routine
import com.example.workout.db.RoutineWorkoutStatsElement
import com.example.workout.db.Workout
import com.example.workout.ui.exercices.ExercicesFragment

import android.widget.ExpandableListView.OnChildClickListener
import android.widget.ExpandableListView.OnGroupCollapseListener
import java.util.*
import kotlin.collections.ArrayList


class StatsFragment : Fragment() {

    private lateinit var statsViewModel: StatsViewModel
    private var _binding: FragmentStatsBinding? = null
    private lateinit var adapter: CustomExpandableListAdapter

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        statsViewModel =
            ViewModelProvider(this).get(StatsViewModel::class.java)

        _binding = FragmentStatsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupRecyclerView()


        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupRecyclerView() {
        //HashMap, die die Wochennummer einem Objekt zuordnet
        val expandableListDetail = HashMap<Int, List<RoutineWorkoutStatsElement>>()

        val cricket: MutableList<RoutineWorkoutStatsElement> = ArrayList()
        cricket.add("India")
        cricket.add("Pakistan")
        cricket.add("Australia")
        cricket.add("England")
        cricket.add("South Africa")

        val football: MutableList<String> = ArrayList()
        football.add("Brazil")
        football.add("Spain")
        football.add("Germany")
        football.add("Netherlands")
        football.add("Italy")

        val basketball: MutableList<String> = ArrayList()
        basketball.add("United States")
        basketball.add("Spain")
        basketball.add("Argentina")
        basketball.add("France")
        basketball.add("Russia")

        expandableListDetail["CRICKET TEAMS"] = cricket
        expandableListDetail["FOOTBALL TEAMS"] = football
        expandableListDetail["BASKETBALL TEAMS"] = basketball

        val expandableListTitle = ArrayList<Int>(expandableListDetail.keys)
        adapter = CustomExpandableListAdapter(requireActivity(), expandableListTitle,
            expandableListDetail)
        _binding?.apply {
            expandableListView.setAdapter(adapter)

            expandableListView.setOnGroupCollapseListener { groupPosition ->
                Log.v("hhh", "List collapsed")

            }

            expandableListView.setOnChildClickListener { parent, v, groupPosition, childPosition, id ->
                    Log.v("hhh", "onChildClick")

                false
            }


        }

    }



}



class CustomExpandableListAdapter(
    private val context: Context, private val expandableListTitle: List<Int>,
    private val expandableListDetail: HashMap<Int, List<RoutineWorkoutStatsElement>>
) : BaseExpandableListAdapter() {
    override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
        return expandableListDetail[expandableListTitle[listPosition]]
            ?.get(expandedListPosition)
    }

    override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
        return expandedListPosition.toLong()
    }

    override fun getChildView(
        listPosition: Int, expandedListPosition: Int,
        isLastChild: Boolean, convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        val expandedListText = getChild(listPosition, expandedListPosition) as String
        if (convertView == null) {
            val layoutInflater = context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.view_item, null)
        }

        //val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = convertView.findViewById(R.id.item_title)

        text.text = expandedListText


        //Date-Klasse nutzen, um aus timestamp Datum abzuleiten

        return convertView
    }

    override fun getChildrenCount(listPosition: Int): Int {
        return expandableListDetail[expandableListTitle[listPosition]]
            ?.size!!
    }

    override fun getGroup(listPosition: Int): Any {
        return expandableListTitle[listPosition]
    }

    override fun getGroupCount(): Int {
        return expandableListTitle.size
    }

    override fun getGroupId(listPosition: Int): Long {
        return listPosition.toLong()
    }

    override fun getGroupView(
        listPosition: Int, isExpanded: Boolean,
        convertView: View, parent: ViewGroup
    ): View {
        var convertView = convertView
        val listTitle = getGroup(listPosition) as String
        if (convertView == null) {
            val layoutInflater =
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = layoutInflater.inflate(R.layout.view_item, null)
        }

        //val image: ImageView = view.findViewById(R.id.item_image)
        val text: TextView = convertView.findViewById(R.id.item_title)

        text.text = listTitle

        return convertView
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
        return true
    }
}
