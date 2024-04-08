package com.krskhalaq.connecthub

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FriendListFragment : Fragment() {

    private lateinit var friendRecyclerView: RecyclerView
    private lateinit var friendAdapter: FriendListAdapter
    val FriendList = ArrayList<String>()
    val FriendIdList = ArrayList<String>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)
        (activity as AppCompatActivity).supportActionBar?.title = "Connections"

        friendRecyclerView = view.findViewById(R.id.friendRV)
        friendRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        fetchFriendsFromFirebase()


        friendAdapter = FriendListAdapter(requireContext(),FriendList,FriendIdList)
        friendRecyclerView.adapter = friendAdapter

        val backbtn = view.findViewById<ImageView>(R.id.backbtn)
        backbtn.setOnClickListener{
            val fragmentManager = requireActivity().supportFragmentManager

            fragmentManager.popBackStack()

        }
        return view
    }

    private fun fetchFriendsFromFirebase() {

        val currentUserID = SignUpActivity.uId
        val usersRef = SignUpActivity.dbFirebase.getReference("Users")

        currentUserID?.let { uid ->
            usersRef.child(uid).child("Connections").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    FriendList.clear()
                    for (friendSnapshot in dataSnapshot.children) {
                        val friendID = friendSnapshot.value.toString()
                        friendID.let { id ->
                            usersRef.child(id).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(userDataSnapshot: DataSnapshot) {
                                    val username = userDataSnapshot.child("userName").getValue(String::class.java)
                                    val userID = userDataSnapshot.child("userId").getValue(String::class.java)
                                    username?.let {
                                        FriendList.add(it)
                                        friendAdapter.notifyDataSetChanged()
                                    }
                                    userID?.let{
                                        FriendIdList.add(it)
                                        friendAdapter.notifyDataSetChanged()
                                    }

                                }
                                override fun onCancelled(databaseError: DatabaseError) {
                                    Log.e("FriendListFragment", "Failed to read user data", databaseError.toException())
                                }
                            })
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.e("FriendListFragment", "Failed to read user data", databaseError.toException())
                }
            })
        }
    }


}