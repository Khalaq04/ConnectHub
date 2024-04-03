package com.krskhalaq.connecthub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

class ChatFragmentAdapter(private val context: Context, private val tempList: List<String>, private val idList: List<String>) : RecyclerView.Adapter<ChatFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_fragment_item, parent, false)
        return ViewHolder(view)
    }
    override fun getItemCount() = tempList.size

    override fun onBindViewHolder(holder: ChatFragmentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val chatItem = itemView.findViewById<TextView>(R.id.chatuserTV)

        init {
            itemView.setOnClickListener(this)
        }


        fun bind(position: Int) {
            chatItem.text = tempList[position]
        }

        override fun onClick(v: View?) {
            val username = tempList[adapterPosition]
            val recvId = idList[adapterPosition]
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("RECVID",recvId)
            context.startActivity(intent)
        }

    }


}