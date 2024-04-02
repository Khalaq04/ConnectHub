package com.krskhalaq.connecthub

import android.R.attr.key
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class MessageAdapter( private val receiverId: String?) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messages = HashMap<String, String>()
    private var sId = ""
    private var rId = ""
    private var time = ""
    private var m = ""

    fun addMessage(message: Message) {
        val currentUserID = SignUpActivity.uId
       if ((currentUserID == message.senderId &&  receiverId == message.receiverId) ||( message.receiverId==currentUserID &&  receiverId == message.senderId)) {
            sId = message.senderId
            rId = message.receiverId
            messages = message.hashMap
            notifyDataSetChanged()

       }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == SENT_MESSAGE_VIEW_TYPE) {
            SentMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_sent, parent, false))
        } else {
            ReceivedMessageViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_message_recv, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
//        val message = messages[position]
        var i = 0
        var k : String? = null
        var v : String = ""
        val iterator: Iterator<*> = messages.keys.iterator()
        while (iterator.hasNext()) {
            if (i == position) {
                k = iterator.next() as String?
                v = messages.getValue(k!!)
                time = k
                m = v
                break
            }
            i++
        }

        if (holder is SentMessageViewHolder) {
            holder.bind(k!!, v)
        } else if (holder is ReceivedMessageViewHolder) {
            holder.bind(k!!, v)
        }
    }

    override fun getItemCount(): Int = messages.size

    override fun getItemViewType(position: Int): Int {
        return if (sId == SignUpActivity.uId) {
            SENT_MESSAGE_VIEW_TYPE
        } else {
            RECEIVED_MESSAGE_VIEW_TYPE
        }
    }

    inner class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textsentMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.sentTextDateTime)

        fun bind(timestamp : String, message : String) {
            messageText.text = message
            timestampText.text = timestamp
        }
    }

    inner class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val messageText: TextView = itemView.findViewById(R.id.textrecvMessage)
        private val timestampText: TextView = itemView.findViewById(R.id.recvTextDateTime)

        fun bind(timestamp : String, message : String) {
            messageText.text = message
            timestampText.text = timestamp
        }
    }

    companion object {
        private const val SENT_MESSAGE_VIEW_TYPE = 1
        private const val RECEIVED_MESSAGE_VIEW_TYPE = 2
    }
}
