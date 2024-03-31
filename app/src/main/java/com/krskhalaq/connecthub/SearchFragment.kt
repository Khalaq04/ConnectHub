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

class SearchFragment : Fragment() {

    private lateinit var searchRV: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Search"

        val userList = ArrayList<User>()
        SignUpActivity.dbFirebase.getReference("Users").addValueEventListener(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                for (ds in snapshot.children) {
                    val uid = ds.child("userId").value.toString()
                    if (uid != SignUpActivity.uId) {
                        val name = ds.child("userName").value.toString()
//                        Log.i("SearchFragment", "UID: $uid, NAME: $name")
                        val user = User(uid, name)
//                        Log.i("SearchFragment", "UID: ${user.id}, NAME: ${user.name}")
                        userList.add(user)
                    }
                }
                if (activity != null) {
                    searchRV = view.findViewById(R.id.searchRV)
                    searchRV.setHasFixedSize(true)
                    searchRV.layoutManager = GridLayoutManager(requireContext(), 3)
                    searchRV.adapter = SearchFragmentAdapter(requireContext(), userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return view
    }

}