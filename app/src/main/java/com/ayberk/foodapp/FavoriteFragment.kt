package com.ayberk.foodapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import androidx.room.Room
import com.ayberk.foodapp.Adapter.FavoriteRecyclerAdapter

import com.ayberk.foodapp.Models.Populer.Meal
import com.ayberk.foodapp.Room.DataDao
import com.ayberk.foodapp.Room.RoomDataBase
import com.ayberk.foodapp.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private lateinit var db: RoomDataBase
    private lateinit var adventDao: DataDao
   private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater,container,false)
        val view = binding.root

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        db = Room.databaseBuilder(requireContext().applicationContext, RoomDataBase::class.java, "Meal")
            .allowMainThreadQueries()
            .build()
        adventDao = db.dataDao()

        val recyclerViewAdapter = FavoriteRecyclerAdapter(adventDao.getAll() as ArrayList<Meal>)
        binding.rcylerFavorite.adapter = recyclerViewAdapter

        binding.rcylerFavorite.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerViewAdapter.notifyDataSetChanged()
    }
}