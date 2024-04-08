package com.krskhalaq.connecthub

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FriendsProfile : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        val username = intent.getStringExtra("USERNAME")
        val frndId = intent.getStringExtra("FRNDID")
        val name = findViewById<TextView>(R.id.fprofileName)
        name.text = "$username"
        val noOfFriends = findViewById<TextView>(R.id.fnoOfConnects)
        val goBack = findViewById<ImageView>(R.id.back)


        SignUpActivity.dbFirebase.getReference("Users").child("$frndId").child("Connections").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connectionsCount = snapshot.childrenCount.toInt()
                noOfFriends.text = connectionsCount.toString()
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Failed to read connections data", error.toException())
            }
        })

        goBack.setOnClickListener{
            finish()
        }

    }
}