package com.krskhalaq.connecthub

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FriendListAdapter(private val context: Context, private val friendList: List<String>, private val FriendIdList: List<String>) : RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.friend_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = friendList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
       private val friendItem: TextView = itemView.findViewById<TextView>(R.id.friendTV)

        init {
            itemView.setOnClickListener(this)
        }

        fun bind(position: Int) {
            friendItem.text = friendList[position]
        }

        override fun onClick(v: View?) {
            val username = friendList[adapterPosition]
            val friendId = FriendIdList[adapterPosition]
            val intent = Intent(context, FriendsProfile::class.java)
            intent.putExtra("USERNAME", username)
            intent.putExtra("FRNDID",friendId)
            context.startActivity(intent)


        }
    }

}
