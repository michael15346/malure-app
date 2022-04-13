package com.coolco.malure

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.coolco.malure.databinding.DossierItemBinding

class DossierAdapter : RecyclerView.Adapter<DossierAdapter.DossierHolder>() {
    private val dossierList = ArrayList<DossierPoint>()
    class DossierHolder(DossierView: View) : RecyclerView.ViewHolder(DossierView) {
        private val binding = DossierItemBinding.bind(DossierView)

        fun bind(dp: DossierPoint){
            binding.imageView.setImageResource(dp.Object)
            binding.textView3.text = dp.Name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DossierHolder {
        return DossierHolder(LayoutInflater.from(parent.context).inflate(R.layout.dossier_item, parent, false))
    }

    override fun onBindViewHolder(holder: DossierHolder, position: Int) {
        holder.bind(dossierList[position])

        holder.itemView.setOnClickListener{
            Log.w("CARD", "${dossierList[position]}")
        }
    }



    override fun getItemCount(): Int {
        return dossierList.size
    }

    fun addDossierPoint(dp: DossierPoint){
        dossierList.add(dp)
        notifyItemInserted(dossierList.size-1)

    }
}