package com.capstone.group6.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.group6.Constant
import com.capstone.group6.R
import com.capstone.group6.databinding.FragmentFavBinding
import com.capstone.group6.databinding.FragmentFeedBinding
import com.capstone.group6.databinding.FragmentUserBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.MainActivity
import com.capstone.group6.ui.adapters.FeedsAdapter
import com.capstone.group6.ui.interfaces.BookmarkClickEvent
import kotlinx.coroutines.launch


class FavFragment : Fragment(), BookmarkClickEvent {
    private val TAG: String? = "FavFragment"
    private lateinit var binding: FragmentFavBinding
    private val mealsViewModel: MealsViewModel by activityViewModels()
    private var mealMutableList: ArrayList<Meal> = arrayListOf()
    private lateinit var feedsAdapter: FeedsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavBinding.inflate(inflater)
        setUpRecyclerView()
        readFirebaseData()
        return binding.root
    }


    private fun setUpRecyclerView() {
        feedsAdapter = FeedsAdapter(mealMutableList, activity!!, Constant.VIEW_TYPE_DETAILS)
        binding.rvFeeds.apply {
            adapter = feedsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun readFirebaseData() {

        mealsViewModel.readFireStoreData()
        lifecycleScope.launch {
            mealsViewModel.fetchFavMeals(true).collect { meals ->
                binding.favTitle.isVisible = meals.isEmpty()
                Log.d(TAG, "readFavFirebaseData: ${meals}")
                mealMutableList = meals as ArrayList<Meal>
                feedsAdapter.setFilterList(mealMutableList)
            }

        }
    }

    override fun onBookMarkSaved(position: Int, feed: Meal) {
        (activity as MainActivity).update(feed)

    }

    override fun feedShare(feed: Meal) {

    }
}