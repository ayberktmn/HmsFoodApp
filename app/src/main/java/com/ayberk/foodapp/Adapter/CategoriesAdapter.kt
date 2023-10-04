package com.ayberk.foodapp.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ayberk.foodapp.Models.Category.Category
import com.ayberk.foodapp.databinding.CategoryItemBinding
import com.bumptech.glide.Glide

class CategoriesAdapter: RecyclerView.Adapter<CategoriesAdapter.MyCategories>() {

    private var livedata: List<Category>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCategories {
       val binding = CategoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return MyCategories(binding)
    }

    override fun onBindViewHolder(holder: MyCategories, position: Int) {
        livedata?.let {
            holder.bind(it[position])
        }
    }

    override fun getItemCount(): Int {

        return livedata?.size ?: 0
    }

    inner class MyCategories(private val binding: CategoryItemBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(data : Category){
            binding.txtCategories.text = data.strCategory
            Glide.with(binding.imgCategories)
                .load(data.strCategoryThumb)
                //.centerInside()
                .centerCrop()
                .into(binding.imgCategories)
        }
    }

    fun setCategoriesList(liveData : List<Category>){
        livedata = liveData
        notifyDataSetChanged()
    }
}