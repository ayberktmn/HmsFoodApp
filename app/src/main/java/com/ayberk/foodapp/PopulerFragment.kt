package com.ayberk.foodapp

import PopulerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.ayberk.foodapp.Models.Populer.Meal
import com.ayberk.foodapp.ViewModel.RandomFoodVM
import com.ayberk.foodapp.databinding.FragmentPopulerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@AndroidEntryPoint
class PopulerFragment : Fragment() {

    private var _binding: FragmentPopulerBinding? = null
    private val binding get() = _binding!!
    private lateinit var populerAdapter: PopulerAdapter
    private val viewModel: RandomFoodVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPopulerBinding.inflate(inflater, container, false)
        val view = binding.root
        initRecycler()
        fetchPopuler()

        viewModel.getPopulerLiveData().observe(viewLifecycleOwner, Observer { t ->
            t?.let {
                this.populerAdapter.setPopulerList(t.meals)

            }
        })

        return view
    }
    private fun initRecycler() {
        populerAdapter = PopulerAdapter(ArrayList()) // PopulerAdapter'i oluşturun
        binding.rcylerPopuler.adapter = populerAdapter // Adapter'i RecyclerView'e ata
        binding.rcylerPopuler.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    }

    private fun fetchPopuler() {
        CoroutineScope(Dispatchers.Main).async {
            viewModel.loadPopuler()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}