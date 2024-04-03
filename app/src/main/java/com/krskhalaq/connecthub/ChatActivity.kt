package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.snapshots
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val username = intent.getStringExtra("USERNAME")
        val recvId = intent.getStringExtra("RECVID")
        val uName = findViewById<TextView>(R.id.textName)
        uName.text = "$username"

        val messages = ArrayList<Message>()

        val messagesRef = SignUpActivity.dbFirebase.getReference("Messages")

        val recyclerView = findViewById<RecyclerView>(R.id.messagesRV)
        recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        adapter = MessageAdapter(messages)
        recyclerView.adapter = adapter

        val backBtn = findViewById<AppCompatImageView>(R.id.imageBack)
        backBtn.setOnClickListener {
            finish()
        }

        val sendBtn = findViewById<AppCompatImageView>(R.id.send_btn)
        sendBtn.setOnClickListener {
            val sentMessage = findViewById<EditText>(R.id.inputMess).text.toString()
            if (recvId != null) {
                if (sentMessage.isNotEmpty() && recvId.isNotEmpty()) {
                    sendMessage(recvId, sentMessage)
//                    Toast.makeText(this@ChatActivity, "Message sent successfully!", Toast.LENGTH_SHORT).show()
//                    Log.i("ChatActivity", "Message sent : $sentMessage")
                }
            }
        }

        messagesRef.addChildEventListener(object : ChildEventListener{
            @SuppressLint("NotifyDataSetChanged")
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                if ((snapshot.child("senderId").value.toString() == SignUpActivity.uId && snapshot.child("receiverId").value.toString() == recvId) || (snapshot.child("senderId").value.toString() == recvId && snapshot.child("receiverId").value.toString() == SignUpActivity.uId)) {
                    for (ds1 in snapshot.children) {
                        if (ds1.key.toString() != "senderId" && ds1.key.toString() != "receiverId") {
                            val message = Message(snapshot.child("senderId").value.toString(), snapshot.child("receiverId").value.toString(), ds1.value.toString(), ds1.key.toString())
                            val found = messages.find { t -> message.time === t.time }
                            if (found == null) {
                                messages.add(message)
                                messages.sortBy {
                                    it.time
                                }
                                messages.distinctBy {
                                    it.time
                                }
                                adapter.notifyDataSetChanged()
                                recyclerView.scrollToPosition(adapter.itemCount - 1)
                            }
                        }
                    }
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                if ((snapshot.child("senderId").value.toString() == SignUpActivity.uId && snapshot.child("receiverId").value.toString() == recvId) || (snapshot.child("senderId").value.toString() == recvId && snapshot.child("receiverId").value.toString() == SignUpActivity.uId)) {
                    for (ds1 in snapshot.children) {
                        if (ds1.key.toString() != "senderId" && ds1.key.toString() != "receiverId") {
                            val message = Message(snapshot.child("senderId").value.toString(), snapshot.child("receiverId").value.toString(), ds1.value.toString(), ds1.key.toString())
                            val found = messages.find { t -> message.time === t.time }
                            if (found == null) {
                                messages.add(message)
                                messages.sortBy {
                                    it.time
                                }
                                messages.distinctBy {
                                    it.time
                                }
                                adapter.notifyDataSetChanged()
                                recyclerView.scrollToPosition(adapter.itemCount - 1)
                            }
                        }
                    }
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun sendMessage(receiverId: String, message: String) {
        val currentUserID = SignUpActivity.uId
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val messagesRef = SignUpActivity.dbFirebase.getReference("Messages")

        var sent = false

        messagesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                for (ds in snapshot.children) {
                    if (ds.child("senderId").value.toString() == currentUserID && ds.child("receiverId").value.toString() == receiverId) {

                        ds.ref.child(timestamp).setValue(message).addOnSuccessListener {
                            findViewById<EditText>(R.id.inputMess).text = null
                        }
                        sent = true
//                        Log.i("ChatActivity", "sent = true")
                    }
                }
                if (!sent) {
                    val newRef = snapshot.ref.push()
                    newRef.child("senderId").setValue(currentUserID).addOnSuccessListener {
//                        Log.i("ChatActivity", "new sender created")
                    }
                    newRef.child("receiverId").setValue(receiverId).addOnSuccessListener {
//                        Log.i("ChatActivity", "new receiver created")
                    }
                    newRef.child(timestamp).setValue(message).addOnSuccessListener {
//                        Log.i("ChatActivity", "1st msg sent")
                        findViewById<EditText>(R.id.inputMess).text = null
                    }
                }
                sent = false
            }

            override fun onCancelled(error: DatabaseError) {
//                Log.i("onCancelled", "ERROR")
            }

        })
    }
}
