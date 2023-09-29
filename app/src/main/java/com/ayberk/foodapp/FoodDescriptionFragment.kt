package com.ayberk.foodapp

import android.os.Bundle
import android.os.Handler
import android.text.TextUtils.indexOf
import android.text.TextUtils.lastIndexOf
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.currentCompositeKeyHash
import androidx.core.graphics.green
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.ayberk.foodapp.Adapter.RandomFoodAdapter
import com.ayberk.foodapp.Models.Food.Meal
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.ViewModel.RandomFoodVM
import com.ayberk.foodapp.databinding.FragmentFoodDescriptionBinding
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

@AndroidEntryPoint
class FoodDescriptionFragment : Fragment() {


    private var _binding: FragmentFoodDescriptionBinding? = null
    private val binding get() = _binding!!
    lateinit var resultList : Meal
    private val viewModel : RandomFoodVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFoodDescriptionBinding.inflate(inflater,container,false)
        val view = binding.root
        val gelen = FoodDescriptionFragmentArgs.fromBundle(requireArguments()).foodId
        fetchRandomFood(foodId = gelen)
        val loading = LoadingDialog(this)
        loading.startLoading()
        val handler = Handler()
        handler.postDelayed(object :Runnable{
            override fun run() {
                loading.isDismiss()
            }

        },1200)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getPopulerDescLiveData().observe(viewLifecycleOwner, object :
            Observer<RandomFood> {
            override fun onChanged(t: RandomFood?) {

                if (t != null) {

                   // println("if e girdi ${t}")
              arguments?.let {
                 val gelen = FoodDescriptionFragmentArgs.fromBundle(it).foodId
               //  println(gelen)
                 resultList = t.meals[listOf(gelen).size - 1]
                 // println(resultList)
                   binding.txtCategory.text = resultList.strCategory
                 binding.txtFoodName.text = resultList.strMeal
                     binding.txtArea.text = resultList.strArea
                     binding.txtDescription.text =resultList.strInstructions
                   Glide.with(binding.imgFoodDesc)
                       .load(resultList.strMealThumb)
                        .into(binding.imgFoodDesc)

                   }
                }
            }
        })
    }

    fun fetchRandomFood(foodId : String){
        CoroutineScope(Dispatchers.Main).async {
            viewModel.loadPopulerDesc(foodId)
        }
    }
}