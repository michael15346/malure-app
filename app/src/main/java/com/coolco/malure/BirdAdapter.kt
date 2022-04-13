package com.coolco.malure

import android.R.attr.fragment
import android.content.Intent
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.Target
import com.coolco.malure.databinding.BirdItemBinding


class BirdAdapter : RecyclerView.Adapter<BirdAdapter.BirdHolder>() {
    private val birdList = ArrayList<BirdResult>()
    class BirdHolder(BirdView: View) : RecyclerView.ViewHolder(BirdView) {
        private val binding = BirdItemBinding.bind(BirdView)

        fun bind(birdResult: BirdResult){
            binding.birdName.text = birdResult.Name
            binding.latinName.text = birdResult.Latin
            Glide.with(binding.birdView)
                .load(birdResult.URL)
                .into(binding.birdView)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BirdHolder {
        return BirdHolder(LayoutInflater.from(parent.context).inflate(R.layout.bird_item, parent, false))
    }

    override fun onBindViewHolder(holder: BirdHolder, position: Int) {

        //val imageView: ImageView = holder.imageView
        //val currentUrl: String = myUrls.get(position)
//
//        Glide.with(holder.birdView)
//            .load(birdList[position].URL)
//            .into(holder.)
        holder.bind(birdList[position])

        holder.itemView.setOnClickListener{
            val intent = Intent(holder.itemView.context, BirdActivity::class.java)
            intent.putExtra("id", birdList[position].id)
            Log.d("CARD", birdList[position].id)
            startActivity(holder.itemView.context, intent, null)
            Log.w("CARD", "${birdList[position]}")
        }
    }



    override fun getItemCount(): Int {
        return birdList.size
    }

    fun addBird(birdResult: BirdResult){
        birdList.add(birdResult)
        notifyItemInserted(birdList.size-1)

    }
    fun clear(){
        val tmp = birdList.size
        birdList.clear()
        notifyItemRangeRemoved(0, tmp)
    }
}