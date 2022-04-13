package com.coolco.malure

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.PlaceCardItemBinding
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlaceCardAdapter : RecyclerView.Adapter<PlaceCardAdapter.PlaceCardHolder>() {
    private val placeList = ArrayList<PlaceCard>()

    class PlaceCardHolder(PlaceScreenView: View) : RecyclerView.ViewHolder(PlaceScreenView) {
        private val binding = PlaceCardItemBinding.bind(PlaceScreenView)
        fun bind(placeCard: PlaceCard){
            binding.placeCardTextView.text = placeCard.Title
            binding.distanceTextView.text = itemView.context.getString(R.string.distance, placeCard.Distance.toInt())

            Glide.with(binding.root)
                .load("http://$HOST:8080/get_place_pic/${placeCard.ID}")
                .into(binding.placeCardImageView)
            //val data = Project("kotlinx.serialization", "Kotlin")
            val string = Json.encodeToString(placeCard)
            Log.d("JSON", string) // {"name":"kotlinx.serialization","language":"Kotlin"}
            // Deserializing back into objects
             // Project(name=kotlinx.serialization, language=Kotlin)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceCardHolder {
        return PlaceCardHolder(LayoutInflater.from(parent.context).inflate(R.layout.place_card_item, parent, false))
    }

    override fun onBindViewHolder(holder: PlaceCardHolder, position: Int) {
        holder.bind(placeList[position])

        //var launcher: ActivityResultLauncher<Intent>? = null
        /*launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        }*/
        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, PlacePage::class.java)
            intent.putExtra("id", placeList[position].ID)
            startActivity(holder.itemView.context, intent, null)
        }
    }



    override fun getItemCount(): Int {
        return placeList.size
    }

    fun addPlace(placeCard: PlaceCard){
        placeList.add(placeCard)
        notifyItemInserted(placeList.size-1)

    }
}