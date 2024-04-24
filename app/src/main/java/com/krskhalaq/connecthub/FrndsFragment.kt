package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FrndsFragment : Fragment() {

    private lateinit var frndsRV: RecyclerView

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_frnds, container, false)

        (activity as AppCompatActivity).supportActionBar?.title = "Notifications"

        val reqListString = ArrayList<String>()
        val reqListUser = ArrayList<User>()
        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqReceived").addValueEventListener(object :
            ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                reqListString.clear()
                reqListUser.clear()
                for (ds in snapshot.children) {
                    val uid = ds.value.toString()
                    if (uid != SignUpActivity.uId) {
                        reqListString.add(uid)
                    }
                }

                SignUpActivity.dbFirebase.getReference("Users").addValueEventListener(object : ValueEventListener {
                    @SuppressLint("NotifyDataSetChanged")
                    override fun onDataChange(snapshot: DataSnapshot) {
                        reqListUser.clear()
                        for (ds in snapshot.children) {
                            val uid = ds.child("userId").value.toString()
                            for (req in reqListString) {
                                if (uid != SignUpActivity.uId && req == uid) {
                                    val name = ds.child("userName").value.toString()
                                    val profPic = ds.child("profileImage").value.toString()
                                    val user = User(uid, name, profPic)
//                                    Log.i("FrndsFragment", "UID: ${user.id}, NAME: ${user.name}")
                                    reqListUser.add(user)
                                    break
                                }
                            }
                        }
                        if (activity != null) {
                            frndsRV = view.findViewById(R.id.frndsRV)
                            frndsRV.setHasFixedSize(true)
                            frndsRV.layoutManager = LinearLayoutManager(requireContext())
                            frndsRV.adapter = FrndsFragmentAdapter(requireContext(), reqListUser)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {

                    }

                })
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return view
    }

}