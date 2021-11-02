package com.example.workout.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.workout.HelperClass
import com.example.workout.HelperClassRoutine
import com.example.workout.R
import com.example.workout.databinding.FragmentHomeBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupViewPagerWithTabs()

        /*
        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/



        //Floating Button Listener
        binding.fab.setOnClickListener { view ->

            if (tabLayout.selectedTabPosition == ROUTINES_PAGE_INDEX){
                findNavController().navigate(R.id.navigation_routine_detail)
            }else{
                //Dialog: Normales Workout oder Supersatz-Workout

                val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
                builder.setMessage("Welcher Workout-Typ?")
                    .setCancelable(true)
                    .setPositiveButton("Normal",
                        DialogInterface.OnClickListener { dialog, id ->  findNavController().navigate(R.id.navigation_workout_detail)})
                    .setNegativeButton("Supersatz",
                        DialogInterface.OnClickListener { dialog, id ->  findNavController().navigate(R.id.navigation_workout_detail_superset)})
                val alert: AlertDialog = builder.create()
                alert.show()
            }

        }

        HelperClass.listToAdd.clear()
        HelperClass.workoutentriesToAdd.clear()
        HelperClass.workoutentriesFromDb.clear()
        HelperClass.addedFromDb = false
        HelperClass.listToAdd2.clear()
        HelperClass.workoutentriesToAdd2.clear()
        HelperClass.workoutentriesFromDb2.clear()

        HelperClassRoutine.allWorkouts.clear()
        HelperClassRoutine.addedFromDb = false

        return root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }



    private fun setupViewPagerWithTabs() {
        val viewPager = binding.viewpager
        tabLayout = binding.tabs

        viewPager.adapter = ViewPagerAdapter(this)

        // Set the title for each tab
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()

        /*
        tabLayout.setOnTabSelectedListener(
            object : TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    super.onTabSelected(tab)

                }
            })
        tabLayout.addOnTabSelectedListener(OnTabSelectedListener {

        })


        tabLayout.setOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }
        })*/
    }

    private fun getTabTitle(position: Int): String? {
        return when(position){
            ROUTINES_PAGE_INDEX -> "Routinen"
            WORKOUTS_PAGE_INDEX -> "Workouts"
            else -> null
        }
    }

}