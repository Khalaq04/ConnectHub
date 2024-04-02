package com.krskhalaq.connecthub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class ChatFragment : Fragment() {

    private lateinit var usersRecyclerView: RecyclerView
    private lateinit var chatAdapter: ChatFragmentAdapter
    val tempList = ArrayList<String>()
    val idList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        (requireActivity() as AppCompatActivity).supportActionBar?.title = "Chat Box"

        usersRecyclerView = view.findViewById(R.id.chatRV)
        usersRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        fetchFriendsFromFirebase()

        //for (i in 1..10)
          //  tempList.add("Friend $i")

        chatAdapter = ChatFragmentAdapter(requireContext(), tempList, idList)
        usersRecyclerView.adapter = chatAdapter

        return view
    }

    private fun fetchFriendsFromFirebase() {

        val currentUserID = SignUpActivity.uId
        val usersRef = SignUpActivity.dbFirebase.getReference("Users")

        currentUserID?.let { uid ->
            usersRef.child(uid).child("Connections").addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    tempList.clear()
                    idList.clear()
                    for (friendSnapshot in dataSnapshot.children) {
                        val friendID = friendSnapshot.value.toString()
                        friendID.let { id ->
                            usersRef.child(id).addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                    val username = userDataSnapshot.child("userName").getValue(String::class.java)
                                    val userID = userDataSnapshot.child("userId").getValue(String::class.java)
                                    username?.let {
                                        tempList.add(it)
                                        chatAdapter.notifyDataSetChanged()
                                    }
                                    userID?.let{
                                        idList.add(it)
                                        chatAdapter.notifyDataSetChanged()
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e("ChatFragment", "Failed to read user data", databaseError.toException())
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("ChatFragment", "Failed to read user data", databaseError.toException())
                }
            })
        }
    }

}

