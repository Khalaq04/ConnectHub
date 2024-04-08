package com.krskhalaq.connecthub

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {

    private lateinit var profileTV: TextView
    private lateinit var friends: ImageView
    private lateinit var noOfFriends: TextView
    private lateinit var signOutBtn: Button


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        profileTV = view.findViewById(R.id.profileName)
        noOfFriends = view.findViewById(R.id.noOfConnects)
        signOutBtn = view.findViewById(R.id.signOutBtn)


        friends = view.findViewById(R.id.connections)

        friends.setOnClickListener{

            friends.setOnClickListener {
                // Get the MainActivity and access mainFL
                val mainActivity = activity as MainActivity
                val mainFL = mainActivity.findViewById<FrameLayout>(R.id.mainFL)

                val transaction = mainActivity.supportFragmentManager.beginTransaction()
                transaction.replace(mainFL.id, FriendListFragment())
                transaction.addToBackStack(null)
                transaction.commit()
            }

        }

        signOutBtn.setOnClickListener {
            SignUpActivity.auth.signOut()
            val intent = Intent(activity, MainActivity::class.java)
            startActivity(intent)
            activity?.finish()
        }

        var userName: String?
        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("userName").getValue(String::class.java)
                userName?.let { Log.i("ProfileFragment", it) }
                userName?.let { profileTV.text = userName }

            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Failed to read connections data", error.toException())
            }

        })

        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connectionsCount = snapshot.childrenCount.toInt()
                noOfFriends.text = connectionsCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("ProfileFragment", "Failed to read connections data", error.toException())
            }
        })

        return view
    }


}