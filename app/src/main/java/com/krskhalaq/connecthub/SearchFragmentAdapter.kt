package com.krskhalaq.connecthub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchFragmentAdapter(private val context: Context, private val tempList: ArrayList<String>) : RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchFragmentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = tempList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val searchItemTV = itemView.findViewById<TextView>(R.id.searchItemTV)

        fun bind(position: Int) {
            searchItemTV.text = tempList[position]
        }

    }

}
