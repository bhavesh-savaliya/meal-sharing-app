package com.capstone.group6.ui.fragments

import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.group6.R
import com.capstone.group6.databinding.FragmentFeedBinding
import com.capstone.group6.feature_meal.domain.model.Meal
import com.capstone.group6.feature_meal.presentation.MealsViewModel
import com.capstone.group6.ui.adapters.FeedsAdapter
import kotlinx.coroutines.launch
import org.openjdk.javax.tools.Tool


class FeedFragment : Fragment() {

    private val TAG: String? = "FeedFragment"
    private val mealsViewModel: MealsViewModel by activityViewModels()
    private lateinit var feedsAdapter: FeedsAdapter
    private lateinit var binding: FragmentFeedBinding
    lateinit var toolbar: Toolbar
    private var mealMutableList: ArrayList<Meal> = arrayListOf()
    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

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

    private fun setToolbarMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu_search, menu);
                val menuItem = menu.findItem(R.id.item_search)
                val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView
                searchView.setQuery(getString(R.string.search_feeds_here), true)

                searchView.setOnQueryTextListener(object :
                    androidx.appcompat.widget.SearchView.OnQueryTextListener {
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

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return false
            }


        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setUpSearch() {
        toolbar = binding.toolbar
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
        (requireActivity() as AppCompatActivity).title = getString(R.string.app_name)
        toolbar.setTitleTextColor(Color.WHITE)
        setToolbarMenu()

    }

    private fun filterList(query: String?) {
        val queryTo = query?.lowercase() ?: ""
        var filterList = arrayListOf<Meal>()
        mealMutableList.filter { item ->
            item.title?.lowercase()?.contains(queryTo) == true ||
                    item.cuisineType.name?.lowercase()?.contains(queryTo) == true ||
                    item.dietarytags.name?.lowercase()?.contains(queryTo) == true
        }.forEach { filteredItem ->
            filterList.add(filteredItem)
        }

        if (filterList.isEmpty()) {
//            Toast.makeText(activity, "No Feed found..", Toast.LENGTH_LONG)
//                .show()
        } else {
            feedsAdapter.setFilterList(filterList)
        }

    }


    private fun setUpRecyclerView() {
        feedsAdapter = FeedsAdapter(mealMutableList, activity!!)
        binding.rvFeeds.apply {
            adapter = feedsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

    private fun readFirebaseData() {
        mealsViewModel.readFireStoreData()
        lifecycleScope.launch {
            mealsViewModel.fetchMeals(true).collect { meals ->
                Log.d(TAG, "readFirebaseData: ${meals}")
                mealMutableList = meals as ArrayList<Meal>
                feedsAdapter.setFilterList(mealMutableList)
            }

        }
    }




}