package com.example.workout.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.workout.databinding.FragmentStatsBinding
import com.example.workout.R
import android.content.Context
import android.util.Log
import android.widget.*
import com.example.workout.db.RoutineWorkoutStatsElement

import com.example.workout.ui.home.DeleteDialogFragment
import java.text.SimpleDateFormat
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

        setupListView()

        /* //für Testzwecke:
        lifecycleScope.launch {
            statsViewModel.createRoutineWorkoutStatsElement(
                RoutineWorkoutStatsElement(0, 5652, 3, "dfgdg", 1633860131000)
            )
            statsViewModel.createRoutineWorkoutStatsElement(
                RoutineWorkoutStatsElement(0, 184, 5, "dg", 1633168926000)
            )
        }

         */

        //Observer --> falls es Änderungen in DB gibt
        statsViewModel.getAllRoutineWorkoutStatsElements()
            .observe(viewLifecycleOwner) { routineWorkoutStatsElements ->

                //HashMap, die die Wochennummer einem Objekt zuordnet
                val expandableListDetail = HashMap<Int, MutableList<RoutineWorkoutStatsElement>>()

                routineWorkoutStatsElements.forEach {
                    //Calendar-Klasse nutzen, um aus timestamp Wochennummer (KW) abzuleiten:
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = it.timestamp!!

                    val mWeekNumber = calendar[Calendar.WEEK_OF_YEAR]

                    //Falls diese KW schon in der HashMap existiert, füge sie ein in die zugehörige MutableList.
                    //Sonst erstelle neue MutableList und füge ein an den Anfang der Liste.
                    if (expandableListDetail[mWeekNumber] != null) {
                        expandableListDetail[mWeekNumber]?.add(0, it)
                    } else {
                        expandableListDetail[mWeekNumber] = mutableListOf(it)
                    }
                }

                val expandableListTitle = ArrayList<Int>(expandableListDetail.keys)

                //Daten an ExpandableList weitergeben
                adapter.setData(expandableListDetail, expandableListTitle)
            }




        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    private fun setupListView() {

        adapter = CustomExpandableListAdapter(
            requireActivity(), arrayListOf(),
            hashMapOf()
        )
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


    inner class CustomExpandableListAdapter(
        private val context: Context, private var expandableListTitle: List<Int>,
        private var expandableListDetail: HashMap<Int, MutableList<RoutineWorkoutStatsElement>>
    ) : BaseExpandableListAdapter() {

        //um vom ViewModel aus Daten zu ändern
        fun setData(
            newDataDetail: HashMap<Int, MutableList<RoutineWorkoutStatsElement>>,
            newDataTitle: List<Int>
        ) {
            this.expandableListDetail = newDataDetail
            this.expandableListTitle = newDataTitle
            notifyDataSetChanged()
        }

        override fun getChild(listPosition: Int, expandedListPosition: Int): Any? {
            return expandableListDetail[expandableListTitle[expandableListTitle.size - listPosition - 1]]
                ?.get(expandedListPosition)
        }

        override fun getChildId(listPosition: Int, expandedListPosition: Int): Long {
            return expandedListPosition.toLong()
        }

        //View des untergeordneten Elementes
        override fun getChildView(
            listPosition: Int, expandedListPosition: Int,
            isLastChild: Boolean, convertView: View?, parent: ViewGroup
        ): View {
            var convertView = convertView
            val childElement =
                getChild(listPosition, expandedListPosition) as RoutineWorkoutStatsElement
            if (convertView == null) {
                val layoutInflater = context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = layoutInflater.inflate(R.layout.stats_view_item, null)
            }

            val image: ImageView = convertView?.findViewById(R.id.item_image)!!
            val text: TextView = convertView?.findViewById(R.id.item_title)!!
            val category: TextView = convertView?.findViewById(R.id.item_category)!!
            val time: TextView = convertView?.findViewById(R.id.item_time)!!

            text.text = childElement.name

            var string2: String

            if (childElement.numberSetsDone!! > 1 || childElement.numberSetsDone == 0) {
                string2 = "Sätze"
            } else {
                string2 = "Satz"
            }

            val str = String.format(
                "%d:%02d", childElement.length?.div(60),
                childElement.length?.rem(60)
            )

            category.text =
                "$str, ${childElement.numberSetsDone} $string2"


            image.setImageResource(R.drawable.ic_baseline_timeline_24)


            //Calendar-Klasse nutzen, um aus timestamp Datum abzuleiten:
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = childElement.timestamp!!

            /*
            val mYear = calendar[Calendar.YEAR]
            val mMonth = calendar[Calendar.MONTH]
            val mDay = calendar[Calendar.DAY_OF_MONTH]
            val mHours = calendar[Calendar.HOUR]
            val mMinutes = calendar[Calendar.MINUTE]
             */

            //Datum & Zeit formatieren
            val format = SimpleDateFormat("dd.MM.yyyy HH:mm")
            format.timeZone = TimeZone.getTimeZone("Europe/Berlin")

            time.text = format.format(calendar.time)


            //OnLongClickListener zum Löschen
            convertView.setOnLongClickListener { v ->
                val dialog = DeleteDialogFragment()
                val args = Bundle()
                args.putInt("seid", childElement.seid)
                dialog.arguments = args

                dialog.show(childFragmentManager, "")
                return@setOnLongClickListener true
            }



            return convertView
        }

        override fun getChildrenCount(listPosition: Int): Int {
            return expandableListDetail[expandableListTitle[expandableListTitle.size - listPosition - 1]]
                ?.size!!
        }

        override fun getGroup(listPosition: Int): Any {
            return expandableListTitle[expandableListTitle.size - listPosition - 1]
        }

        override fun getGroupCount(): Int {
            return expandableListTitle.size
        }

        override fun getGroupId(listPosition: Int): Long {
            return listPosition.toLong()
        }

        //View des übergeordneteten Elementes
        override fun getGroupView(
            listPosition: Int, isExpanded: Boolean,
            convertView: View?, parent: ViewGroup
        ): View {
            var convertView = convertView
            val groupElement = getGroup(listPosition) as Int
            if (convertView == null) {
                val layoutInflater =
                    context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                convertView = layoutInflater.inflate(R.layout.stats_view_item_group, null)
            }

            //val image: ImageView = view.findViewById(R.id.item_image)
            val text: TextView = convertView?.findViewById(R.id.item_title)!!
            val category: TextView = convertView?.findViewById(R.id.item_category)!!

            //Anzahl der Sätze und Sekunden insgesamt berechnen
            var counterSeconds = 0
            var counterSets = 0

            expandableListDetail[groupElement]?.forEach {
                counterSeconds += it.length!!
                counterSets += it.numberSetsDone!!
            }

            var string2: String

            if (counterSets > 1 || counterSets == 0) {
                string2 = "Sätze"
            } else {
                string2 = "Satz"
            }

            val str = String.format(
                "%d:%02d", counterSeconds?.div(60),
                counterSeconds?.rem(60)
            )

            text.text = "KW ${groupElement.toString()}"
            category.text = "$str, $counterSets $string2"

            return convertView
        }

        override fun hasStableIds(): Boolean {
            return false
        }

        override fun isChildSelectable(listPosition: Int, expandedListPosition: Int): Boolean {
            return true
        }
    }




}

