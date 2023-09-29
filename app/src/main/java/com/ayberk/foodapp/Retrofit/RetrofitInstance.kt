package com.ayberk.foodapp.Retrofit

import com.ayberk.foodapp.Models.Category.Categories
import com.ayberk.foodapp.Models.Food.RandomFood
import com.ayberk.foodapp.Models.Populer.PopulerList
import retrofit2.http.GET
import retrofit2.http.Query

interface RetrofitInstance {

    @GET("random.php")
    fun getRandomFood()  : retrofit2.Call<RandomFood>

    @GET("categories.php")
    fun getCategories() : retrofit2.Call<Categories>

    @GET("filter.php?")
    fun getPopuler(@Query ("c") categoryName :String) : retrofit2.Call<PopulerList>

    @GET("lookup.php?")
    fun getPopulerDescription(@Query ("i") id :String) : retrofit2.Call<RandomFood>


}