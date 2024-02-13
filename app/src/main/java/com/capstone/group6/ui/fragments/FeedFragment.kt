package com.capstone.group6.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.group6.databinding.FragmentFeedBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.adapters.FeedsAdapter
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [FeedFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FeedFragment : Fragment() {

    private val TAG: String? = "FeedFragment"
    private val mealsViewModel: MealsViewModel by activityViewModels()
    private lateinit var feedsAdapter: FeedsAdapter
    private lateinit var binding: FragmentFeedBinding
    private lateinit var searchView: SearchView
    private var mealMutableList: ArrayList<Meal> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFeedBinding.inflate(inflater)
        setUpRecyclerView()
        readFirebaseData()
        setUpSearch()
        return binding.root
    }

    private fun setUpSearch() {
        searchView = binding.searchBar
        searchView.clearFocus()
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterList(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                filterList(newText)

                return false
            }

        })

    }

    private fun filterList(query: String?) {
        var filterList = arrayListOf<Meal>()
        for (item in mealMutableList) {
            if (item.title?.lowercase()!!.contains(query!!.lowercase())) {
                filterList.add(item)
            }
        }
        if (filterList.isEmpty()) {
            Toast.makeText(activity, "No Feed found..", Toast.LENGTH_LONG)
                .show()
        } else {
            feedsAdapter.setFilterList(filterList)
        }

    }


    private fun setUpRecyclerView() {
        feedsAdapter = FeedsAdapter(mealMutableList, context!!)
        binding.rvFeeds.apply {
            adapter = feedsAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun readFirebaseData() {
        mealsViewModel.readFireStoreData()
        lifecycleScope.launch {
            mealsViewModel.fetchMeals().collect { meals ->
                mealMutableList = meals as ArrayList<Meal>
                feedsAdapter.setFilterList(mealMutableList)
            }

        }
    }


}