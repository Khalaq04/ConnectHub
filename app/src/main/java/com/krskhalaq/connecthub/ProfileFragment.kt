package com.krskhalaq.connecthub

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.values
import java.util.concurrent.CountDownLatch

class ProfileFragment : Fragment() {

    private lateinit var profileTV: TextView

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Profile"

        profileTV = view.findViewById(R.id.profileTV)

        var userName: String?
        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userName = snapshot.child("userName").getValue(String::class.java)
                userName?.let { Log.i("ProfileFragment", it) }
                userName?.let { profileTV.text = userName }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return view
    }

}