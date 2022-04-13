package com.coolco.malure

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.coolco.malure.databinding.BirdResultsItemBinding

class BirdAdapterResults : RecyclerView.Adapter<BirdAdapterResults.BirdResultsHolder>() {
    private val birdList = ArrayList<BirdResult>()
    class BirdResultsHolder(BirdView: View) : RecyclerView.ViewHolder(BirdView) {
        private val binding = BirdResultsItemBinding.bind(BirdView)

        fun bind(birdResult: BirdResult){
            //binding.birdView.setImageResource(birdResult.ImageID)
            binding.birdName.text = birdResult.Name
            binding.latinName.text = birdResult.Latin
            binding.birdChance.text = birdResult.Chance.toString()

            Glide.with(binding.birdView)
                .load(birdResult.URL)
                .into(binding.birdView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdResultsHolder {
        return BirdResultsHolder(LayoutInflater.from(parent.context).inflate(R.layout.bird_item, parent, false))
    }

    override fun onBindViewHolder(holder: BirdResultsHolder, position: Int) {
        holder.bind(birdList[position])


        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, BirdActivity::class.java)
            intent.putExtra("id", birdList[position].id)
            Log.d("CARD", birdList[position].id)
            ContextCompat.startActivity(holder.itemView.context, intent, null)
            Log.w("CARD", "${birdList[position]}")
        }
    }



    override fun getItemCount(): Int {
        return birdList.size
    }

    fun     addPlace(birdResult: BirdResult){
        birdList.add(birdResult)
        notifyItemInserted(birdList.size-1)

    }
}