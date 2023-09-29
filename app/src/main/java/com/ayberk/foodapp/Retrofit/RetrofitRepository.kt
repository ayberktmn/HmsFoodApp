package com.ayberk.foodapp.Retrofit

import androidx.lifecycle.MutableLiveData
import com.ayberk.foodapp.Models.Category.Categories
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.Models.Populer.PopulerList
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject

class RetrofitRepository@Inject constructor(private val retroService: RetrofitInstance) {

    fun getRandomFood(liveData: MutableLiveData<RandomFood>){
        retroService.getRandomFood().enqueue(object : retrofit2.Callback<RandomFood>{
            override fun onResponse(call: Call<RandomFood>, response: Response<RandomFood>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<RandomFood>, t: Throwable) {
                liveData.postValue(null)
            }
        })
    }

    fun getCategories(liveData: MutableLiveData<Categories>){

        retroService.getCategories().enqueue(object : retrofit2.Callback<Categories>{
            override fun onResponse(call: Call<Categories>, response: Response<Categories>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<Categories>, t: Throwable) {
                liveData.postValue(null)
            }

        })
    }

    fun getPopuler(liveData: MutableLiveData<PopulerList>){

        retroService.getPopuler("Seafood").enqueue(object : retrofit2.Callback<PopulerList>{
            override fun onResponse(call: Call<PopulerList>, response: Response<PopulerList>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<PopulerList>, t: Throwable) {
                liveData.postValue(null)
            }

        })
    }

    fun getPopulerDesc(id: String,liveData: MutableLiveData<RandomFood>){

        retroService.getPopulerDescription(id).enqueue(object : retrofit2.Callback<RandomFood>{
            override fun onResponse(call: Call<RandomFood>, response: Response<RandomFood>) {
                liveData.postValue(response.body())
            }

            override fun onFailure(call: Call<RandomFood>, t: Throwable) {
                liveData.postValue(null)
            }

        })
    }


}