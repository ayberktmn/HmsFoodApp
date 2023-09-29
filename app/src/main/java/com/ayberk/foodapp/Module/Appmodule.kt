package com.ayberk.foodapp.Module

import android.content.Context
import android.provider.SyncStateContract.Constants
import com.ayberk.foodapp.Retrofit.RetrofitInstance
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class Appmodule {

    var baseURL = "https://www.themealdb.com/api/json/v1/1/"

    @Provides
    @Singleton
    fun provideSharedPreferences (@ApplicationContext context: Context) = context.getSharedPreferences(Constants.ACCOUNT_NAME,Context.MODE_PRIVATE)


    @Provides
    @Singleton
    fun getRetrofitServiceInstance(retrofit: Retrofit) : RetrofitInstance
    {
        return retrofit.create(RetrofitInstance::class.java)
    }

    @Singleton
    @Provides
    fun getRetroInstance(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseURL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


}