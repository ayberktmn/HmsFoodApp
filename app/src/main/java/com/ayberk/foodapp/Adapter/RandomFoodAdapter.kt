package com.ayberk.foodapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.foodapp.HomeFragmentDirections
import com.ayberk.foodapp.Models.Food.Meal
import com.ayberk.foodapp.R
import com.ayberk.foodapp.databinding.FoodItemBinding
import com.bumptech.glide.Glide

class RandomFoodAdapter : RecyclerView.Adapter<RandomFoodAdapter.MyFood>() {

    private var livedata: List<Meal>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyFood {
        val binding = FoodItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyFood(binding)
    }

    override fun onBindViewHolder(holder: MyFood, position: Int) {
        livedata?.let {
            holder.bind(it[position])
        }
      /*  holder.itemView.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToFoodDescriptionFragment()
            holder.itemView.findNavController().navigate(action)

        } */

    }

    override fun getItemCount(): Int {
        return livedata?.size ?: 0
    }

    inner class MyFood(private val binding: FoodItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Meal) {
           binding.txtFood.text = data.strMeal

            Glide.with(binding.imgFood)
                .load(data.strMealThumb)
                .centerInside()
                .into(binding.imgFood)
        }
    }

    fun setFoodList(liveData: List<Meal>?) {
        livedata = liveData
        notifyDataSetChanged()
    }
}
