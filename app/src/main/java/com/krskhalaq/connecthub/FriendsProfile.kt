package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FriendsProfile : AppCompatActivity() {

    private lateinit var profileRV: RecyclerView
    private lateinit var profilePic: ImageView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends_profile)

        val username = intent.getStringExtra("USERNAME")
        val frndId = intent.getStringExtra("FRNDID")
        val name = findViewById<TextView>(R.id.fprofileName)
        name.text = "$username"
        val noOfFriends = findViewById<TextView>(R.id.fnoOfConnects)
        val goBack = findViewById<ImageView>(R.id.back)
        profileRV = findViewById(R.id.frndProfileRV)
        profilePic = findViewById(R.id.fprofilePic)

        var profileImg = ""
        SignUpActivity.dbFirebase.getReference("Users").child(frndId!!).child("profileImage").get().addOnSuccessListener {
            profileImg = it.value.toString()
            if (profileImg.isNotEmpty()) {
                val imgRef = SignUpActivity.storage.getReferenceFromUrl(profileImg)
                imgRef.getBytes(10 * 1024 * 1024).addOnSuccessListener {it1 ->
                    val bitmap = BitmapFactory.decodeByteArray(it1, 0, it1.size)
                    profilePic.setImageBitmap(bitmap)
//                Log.i("HmAdapter", "Image Set!!!!!!!!!")
                }.addOnFailureListener {
                    // Handle any errors
                }
            }
        }

        profileRV.setHasFixedSize(true)
        profileRV.layoutManager = GridLayoutManager(this, 3)
        var imgUrls = ArrayList<String>()

        val adapter = ProfileAdapter(this, imgUrls)
        profileRV.adapter = adapter

        SignUpActivity.dbFirebase.getReference("Users").child(frndId!!).child("Posts").get().addOnSuccessListener {
            for (ds in it.children) {
                imgUrls.add(ds.value.toString())
            }
            adapter.notifyDataSetChanged()
        }

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