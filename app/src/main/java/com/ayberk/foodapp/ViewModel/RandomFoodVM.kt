package com.ayberk.foodapp.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ayberk.foodapp.Models.Category.Categories
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.Models.Populer.PopulerList
import com.ayberk.foodapp.Retrofit.RetrofitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RandomFoodVM @Inject constructor(private val repository: RetrofitRepository) : ViewModel() {

    var FoodList: MutableLiveData<RandomFood>
    var Categories : MutableLiveData<Categories>
    var PopulerList : MutableLiveData<PopulerList>
    var PopulerDescList : MutableLiveData<RandomFood>

    init {
        FoodList = MutableLiveData()
        Categories = MutableLiveData()
        PopulerList = MutableLiveData()
        PopulerDescList = MutableLiveData()
    }

    fun getFoodLiveData(): MutableLiveData<RandomFood>{
        return FoodList
    }
    fun  loadFood(){
        repository.getRandomFood(FoodList)
    }
    fun getCategoriesLiveData() : MutableLiveData<Categories>{
        return  Categories
    }
    fun loadCategories(){
        repository.getCategories(Categories)
    }
    fun getPopulerLiveData() : MutableLiveData<PopulerList>{
        return  PopulerList
    }
    fun loadPopuler(){
        repository.getPopuler(PopulerList)
    }
    fun getPopulerDescLiveData() : MutableLiveData<RandomFood>{
        return  PopulerDescList
    }
    fun loadPopulerDesc(id:String){
        repository.getPopulerDesc(id,PopulerDescList)
    }
}