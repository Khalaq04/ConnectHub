package com.krskhalaq.connecthub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FrndsFragmentAdapter(private val context: Context, private val tempList: ArrayList<String>) : RecyclerView.Adapter<FrndsFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrndsFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.frnds_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FrndsFragmentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = tempList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val frndsItemTV = itemView.findViewById<TextView>(R.id.frndsItemTV)

        fun bind(position: Int) {
            frndsItemTV.text = tempList[position]
        }

    }

}
