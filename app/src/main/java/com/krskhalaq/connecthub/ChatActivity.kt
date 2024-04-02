package com.krskhalaq.connecthub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {

    private lateinit var adapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val username = intent.getStringExtra("USERNAME")
        val recvId = intent.getStringExtra("RECVID")
        val uName = findViewById<TextView>(R.id.textName)
        uName.text = "$username"


        val messagesRef = SignUpActivity.dbFirebase.getReference("Messages")


        val recyclerView = findViewById<RecyclerView>(R.id.messagesRV)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MessageAdapter(recvId)
        recyclerView.adapter = adapter


        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    adapter.addMessage(it)
                    recyclerView.scrollToPosition(adapter.itemCount - 1)
                }

                val sendBtn = findViewById<AppCompatImageView>(R.id.send_btn)
                sendBtn.setOnClickListener {
                    val sentMessage = findViewById<EditText>(R.id.inputMess).text.toString()
                    if (recvId != null) {
                        sendMessage(recvId, sentMessage)
                    }
                }

                val backBtn = findViewById<AppCompatImageView>(R.id.imageBack)
                backBtn.setOnClickListener {
                    finish()
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}


            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    private fun sendMessage(receiverId: String, message: String) {
        val currentUserID = SignUpActivity.uId
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
        val messagesRef = SignUpActivity.dbFirebase.getReference("Messages")

        messagesRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                for (ds in snapshot.children) {
                    if (ds.child("senderId").value.toString() == currentUserID && ds.child("receiverId").value.toString() == receiverId) {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap[timestamp] = message
                        ds.ref.setValue(hashMap).addOnSuccessListener {
                            findViewById<EditText>(R.id.inputMess).text = null
                        }
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

//        messagesRef.push().setValue(messageData)
//            .addOnSuccessListener {
//                findViewById<EditText>(R.id.inputMess).text = null
//            }
//            .addOnFailureListener { e ->
//
//            }
    }
}
