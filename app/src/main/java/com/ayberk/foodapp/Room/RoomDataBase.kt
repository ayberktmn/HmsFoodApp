package com.ayberk.foodapp.Room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ayberk.foodapp.Models.Populer.Meal

@Database(entities = [Meal :: class], version = 1)
abstract class RoomDataBase : RoomDatabase(){

    abstract fun dataDao() : DataDao
}