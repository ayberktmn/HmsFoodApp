import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.ayberk.foodapp.Models.Populer.Meal
import com.ayberk.foodapp.PopulerFragmentDirections
import com.ayberk.foodapp.R
import com.ayberk.foodapp.Room.DataDao
import com.ayberk.foodapp.Room.RoomDataBase
import com.ayberk.foodapp.databinding.PopulerItemBinding
import com.bumptech.glide.Glide
import com.google.android.filament.View

class PopulerAdapter(meals: ArrayList<Meal>) : RecyclerView.Adapter<PopulerAdapter.MyPopuler>(){

    private var livedata : List<Meal>? = null
    private lateinit var db : RoomDataBase
    private lateinit var dataDao: DataDao

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyPopuler {
        val binding = PopulerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyPopuler(binding)
    }

    override fun onBindViewHolder(holder: MyPopuler, position: Int) {
        holder.bind(livedata!!.get(position))
        holder.itemView.setOnClickListener {
            val action = PopulerFragmentDirections.actionPopulerFragmentToFoodDescriptionFragment(livedata!![position].idMeal)
            holder.itemView.findNavController().navigate(action)
        }
        db = Room.databaseBuilder(holder.itemView.context.applicationContext,
            RoomDataBase::class.java,"Meal")
            .allowMainThreadQueries()
            .build()
        dataDao = db.dataDao()

        val idMeal = livedata!![position].idMeal

        if (dataDao.checkIfDataExists(idMeal) > 0) {
            // Veritabanında veri varsa, image görüntülenecek
            holder.imagelike.setImageResource(R.drawable.like)
            holder.imagelike.isClickable = false
        } else {
            // Veritabanında veri yoksa, image gizlenecek
            holder.imagelike.setImageResource(R.drawable.love)
            holder.imagelike.isClickable = true
        }

        holder.imagelike.setOnClickListener {
            if (dataDao.checkIfDataExists(idMeal) > 0) {
                // Veritabanında veri varsa, kaldıracak
                dataDao.delete(Meal(idMeal, "", ""))
                Toast.makeText(holder.itemView.context, "Favorilerden Kaldırıldı", Toast.LENGTH_SHORT).show()
            } else {
                // Veritabanında veri yoksa, ekleme yapılacak
                dataDao.insert(Meal(idMeal, livedata!![position].strMeal, livedata!![position].strMealThumb))
                Toast.makeText(holder.itemView.context, "Favorilere Eklendi", Toast.LENGTH_SHORT).show()
            }
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return livedata?.size ?: 0
    }

    inner class MyPopuler(private val binding: PopulerItemBinding) : RecyclerView.ViewHolder(binding.root){

        val imagelike = binding.imgFavorite

        fun bind(data : Meal){
            binding.txtPopler.text = data.strMeal
            Glide.with(binding.imgPopuler)
                .load(data.strMealThumb)
                .centerCrop()
                .into(binding.imgPopuler)
        }
    }

    fun setPopulerList(liveData: List<Meal>){
        this.livedata = liveData
        notifyDataSetChanged()
    }
}