package com.krskhalaq.connecthub

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView

class MessageAdapter( private val messages: ArrayList<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT_MESSAGE_VIEW_TYPE) {
            SentMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false))
        } else {
            ReceivedMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_recv, parent, false))
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position].msg
        val time = messages[position].time

        if (holder is SentMessageViewHolder) {
            holder.bind(message, time)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(message, time)
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (messages.elementAt(position).senderId == SignUpActivity.uId) {
            SENT_MESSAGE_VIEW_TYPE
        } else {
            RECEIVED_MESSAGE_VIEW_TYPE
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textsentMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.sentTextDateTime)

        fun bind(message : String, time : String) {
            messageText.text = message
            timestampText.text = time
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textrecvMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.recvTextDateTime)

        fun bind(message : String, time : String) {
            messageText.text = message
            timestampText.text = time
        }
    }

    companion object {
        private const val SENT_MESSAGE_VIEW_TYPE = 1
        private const val RECEIVED_MESSAGE_VIEW_TYPE = 2
    }
}
