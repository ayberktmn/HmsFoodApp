package com.ayberk.foodapp

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayberk.foodapp.Adapter.CategoriesAdapter
import com.ayberk.foodapp.Adapter.RandomFoodAdapter
import com.ayberk.foodapp.Models.Category.Categories
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.ViewModel.RandomFoodVM
import com.ayberk.foodapp.databinding.FragmentHomeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private var isBackPressed = false
    private lateinit var adapter: RandomFoodAdapter
    private lateinit var categoriesAdapter: CategoriesAdapter
    private val viewModel: RandomFoodVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        initRecycler()
        fetchCategories()
        fetchRandomFood()
        val layoutManager = LinearLayoutManager(context)
        binding.rcylerFood.layoutManager = layoutManager

        viewModel.getCategoriesLiveData().observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                categoriesAdapter.setCategoriesList(t.categories)
            }
        })

        viewModel.getFoodLiveData().observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                adapter.setFoodList(t.meals)
                Handler(Looper.getMainLooper()).postDelayed({
                    fetchRandomFood()
                }, 6000)
            }
        })
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            isBackPressed = true

        }

        // binding.bottomBar.setOnNavigationItemReselectedListener {}
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {-
                R.id.home -> {
                    Navigation.findNavController(binding.root).navigate(R.id.homeFragment)
                    true
                }

                R.id.favorie -> {
                    Navigation.findNavController(binding.root).navigate(R.id.favoriteFragment)
                    true
                }

                R.id.populer -> {
                    Navigation.findNavController(binding.root).navigate(R.id.populerFragment)
                    true
                }

                R.id.map -> {
                    Navigation.findNavController(binding.root).navigate(R.id.mapFragment)
                    true
                }

                else -> {

                }
            }
            true
        }

    }

    private fun initRecycler() {
        adapter = RandomFoodAdapter()
        binding.rcylerFood.adapter = adapter

        binding.rcylerCategory.layoutManager =
            StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)

        categoriesAdapter = CategoriesAdapter()
        binding.rcylerCategory.adapter = categoriesAdapter
    }

    private fun fetchRandomFood() {
        CoroutineScope(Dispatchers.Main).async {
            viewModel.loadFood()
        }
    }

    private fun fetchCategories() {
        CoroutineScope(Dispatchers.Main).async {
            viewModel.loadCategories()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
