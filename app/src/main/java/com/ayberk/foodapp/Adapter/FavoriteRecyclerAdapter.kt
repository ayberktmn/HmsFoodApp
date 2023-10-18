package com.ayberk.foodapp.Adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ayberk.foodapp.Models.Populer.Meal
import com.ayberk.foodapp.R
import com.ayberk.foodapp.Room.DataDao
import com.ayberk.foodapp.Room.RoomDataBase
import com.ayberk.foodapp.databinding.PopulerItemBinding
import com.bumptech.glide.Glide


class FavoriteRecyclerAdapter(private val FavoriteFoodList:ArrayList<Meal>) : RecyclerView.Adapter<FavoriteRecyclerAdapter.FoodFavoriHolder>() {

        private lateinit var db : RoomDataBase
        private lateinit var adventDao: DataDao
        private var livedata : List<Meal>? = null


        class FoodFavoriHolder (val binding : PopulerItemBinding): RecyclerView.ViewHolder(binding.root) {

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodFavoriHolder {
            val binding = PopulerItemBinding.inflate(LayoutInflater.from(parent.context))
            return FoodFavoriHolder(binding)

        }

        override fun getItemCount(): Int {
            return FavoriteFoodList.size
        }

        override fun onBindViewHolder(holder: FoodFavoriHolder, position: Int) {

           holder.binding.txtPopler.text = FavoriteFoodList[position].strMeal
            Glide.with(holder.binding.imgPopuler)
                .load(FavoriteFoodList[position].strMealThumb)
                .centerCrop()
                .into(holder.binding.imgPopuler)

                db =
                    Room.databaseBuilder(
                        holder.binding.root.context,
                        RoomDataBase::class.java,
                        "Meal"
                    )
                        .allowMainThreadQueries()
                        .build()
                adventDao = db.dataDao()


            holder.binding.imgFavorite.setOnClickListener {
                val favoriFood = FavoriteFoodList[position]
                adventDao.delete(favoriFood)
                Toast.makeText(holder.itemView.context, "Deleted from Favorites", Toast.LENGTH_SHORT)
                    .show()

                FavoriteFoodList.removeAt(position)
                notifyItemRemoved(position)

                holder.itemView.findNavController().navigate(R.id.action_favoriteFragment_self)
            }
        }
}