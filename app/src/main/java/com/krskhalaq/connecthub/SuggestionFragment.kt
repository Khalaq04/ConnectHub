package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SuggestionFragment : Fragment() {

    private lateinit var suggestionRV: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_suggestion, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Suggestions"

        val userList = ArrayList<User>()
        SignUpActivity.dbFirebase.getReference("Users").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                val loc = snapshot.child(SignUpActivity.uId).child("location").value.toString()
                for (ds in snapshot.children) {
                    val uid = ds.child("userId").value.toString()
                    val loc1 = ds.child("location").value.toString()
                    if (uid != SignUpActivity.uId && loc == loc1) {
                        val name = ds.child("userName").value.toString()
                        val profPic = ds.child("profileImage").value.toString()
//                        Log.i("SearchFragment", "UID: $uid, NAME: $name")
                        val user = User(uid, name, profPic)
//                        Log.i("SearchFragment", "UID: ${user.id}, NAME: ${user.name}")
                        userList.add(user)
                    }
                }
                if (activity != null) {
                    suggestionRV = view.findViewById(R.id.suggestionRV)
                    suggestionRV.setHasFixedSize(true)
                    suggestionRV.layoutManager = GridLayoutManager(requireContext(), 3)
                    suggestionRV.adapter = SearchFragmentAdapter(requireContext(), userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return view
    }

}