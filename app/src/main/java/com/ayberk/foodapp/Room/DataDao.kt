package com.ayberk.foodapp.Room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.ayberk.foodapp.Models.Populer.Meal

@Dao
interface DataDao {

    @Query("SELECT * FROM Meal")
    fun getAll(): List<Meal>
    @Delete
    fun delete(advent: Meal)
    @Insert
    fun insert(advent: Meal)

    @Query("SELECT COUNT(*) FROM Meal WHERE idMeal = :idMeal")
    fun checkIfDataExists(idMeal: String): Int
}