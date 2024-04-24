package com.krskhalaq.connecthub

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class SearchFragmentAdapter(private val context: Context, private val userList: ArrayList<User>) : RecyclerView.Adapter<SearchFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.search_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SearchFragmentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = userList.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val searchItemTV: TextView = itemView.findViewById(R.id.searchItemTV)
        private val searchItemBtn: Button = itemView.findViewById(R.id.searchItemBtn)
        private val searchItemIV: ImageView = itemView.findViewById(R.id.searchItemIV)

        fun bind(position: Int) {
            searchItemTV.text = userList[position].name

            var profileImg = ""
            SignUpActivity.dbFirebase.getReference("Users").child(userList[position].id).child("profileImage").get().addOnSuccessListener {
                profileImg = it.value.toString()
                if (profileImg.isNotEmpty()) {
                    val imgRef = SignUpActivity.storage.getReferenceFromUrl(profileImg)
                    imgRef.getBytes(10 * 1024 * 1024).addOnSuccessListener {it1 ->
                        val bitmap = BitmapFactory.decodeByteArray(it1, 0, it1.size)
                        searchItemIV.setImageBitmap(bitmap)
//                Log.i("HmAdapter", "Image Set!!!!!!!!!")
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
            }

            var received = false
            var connected = false

            SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections").addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        if (ds.value.toString() == userList[position].id) {
                            searchItemBtn.text = "Connected"
                            searchItemBtn.isEnabled = false
                            connected = true
                        }
                    }
                    if (!connected) {
                        SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqReceived").addValueEventListener(object : ValueEventListener {
                            @SuppressLint("SetTextI18n")
                            override fun onDataChange(snapshot: DataSnapshot) {
                                for (ds in snapshot.children) {
                                    if (ds.value.toString() == userList[position].id) {
                                        searchItemBtn.text = "Accept"
                                        searchItemBtn.isEnabled = true
                                        received = true
                                    }
                                }
                                if (received) {
                                    searchItemBtn.setOnClickListener {
                                        acceptConnectRequest(userList[position])
                                    }
                                }
                                else {
                                    SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqSent").addValueEventListener(object : ValueEventListener {
                                        @SuppressLint("SetTextI18n")
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (ds in snapshot.children) {
                                                if (ds.value.toString() == userList[position].id) {
                                                    searchItemBtn.text = "Pending"
                                                    searchItemBtn.isEnabled = false
                                                }
                                            }
                                            searchItemBtn.setOnClickListener {
                                                sendConnectRequest(userList[position])
                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                    })
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {

                            }

                        })
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        }

        private fun acceptConnectRequest(user: User) {
            var dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections")
            dbRef.push().setValue(user.id).addOnCompleteListener {it1 ->
                if (it1.isSuccessful) {
                    Toast.makeText(context, "Accepting Connect Request!", Toast.LENGTH_SHORT).show()
                    searchItemBtn.text = "Connected"
                    searchItemBtn.isEnabled = false

                    dbRef = SignUpActivity.dbFirebase.getReference("Users").child(user.id).child("Connections")
                    dbRef.push().setValue(SignUpActivity.uId).addOnCompleteListener {it2 ->
                        if (it2.isSuccessful) {
//                            Toast.makeText(context, "Connect Request Sent!", Toast.LENGTH_SHORT).show()
                            searchItemBtn.text = "Connected"
                            searchItemBtn.isEnabled = false

                            dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqReceived")
                            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ds in snapshot.children) {
//                                        Log.i("SearchFragmentAdapter", ds.value.toString())
                                        if (ds.value.toString() == user.id) {
//                                            Log.i("SearchFragmentAdapter", ds.value.toString())
                                            ds.ref.removeValue()
                                        }
                                    }

                                    dbRef = SignUpActivity.dbFirebase.getReference("Users").child(user.id).child("ConnectReqSent")
                                    dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (ds in snapshot.children) {
//                                                Log.i("SearchFragmentAdapter", ds.value.toString())
                                                if (ds.value.toString() == SignUpActivity.uId) {
//                                                    Log.i("SearchFragmentAdapter", ds.value.toString())
                                                    ds.ref.removeValue()
                                                }
                                            }

                                            Toast.makeText(context, "Accepted Connect Request!", Toast.LENGTH_SHORT).show()
                                        }

                                        override fun onCancelled(error: DatabaseError) {

                                        }

                                    })
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                        }
                        else {
                            Toast.makeText(context, "Failed to send request!", Toast.LENGTH_SHORT).show()
                            searchItemBtn.text = "Accept"
                            searchItemBtn.isEnabled = true
                            dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections")
                            dbRef.removeEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ds in snapshot.children) {
                                        if (ds.value == user.id) {
                                            ds.ref.removeValue()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                        }
                    }
                }
                else {
                    Toast.makeText(context, "Failed to send request!", Toast.LENGTH_SHORT).show()
                    searchItemBtn.text = "Accept"
                    searchItemBtn.isEnabled = true
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun sendConnectRequest(user: User) {
            var dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqSent")
            dbRef.push().setValue(user.id).addOnCompleteListener {it1 ->
                if (it1.isSuccessful) {
                    Toast.makeText(context, "Sending Connect Request!", Toast.LENGTH_SHORT).show()
                    searchItemBtn.text = "Pending"
                    searchItemBtn.isEnabled = false

                    dbRef = SignUpActivity.dbFirebase.getReference("Users").child(user.id).child("ConnectReqReceived")
                    dbRef.push().setValue(SignUpActivity.uId).addOnCompleteListener {it2 ->
                        if (it2.isSuccessful) {
                            Toast.makeText(context, "Connect Request Sent!", Toast.LENGTH_SHORT).show()
                            searchItemBtn.text = "Pending"
                            searchItemBtn.isEnabled = false
                        }
                        else {
                            Toast.makeText(context, "Failed to send request!", Toast.LENGTH_SHORT).show()
                            searchItemBtn.text = "Connect"
                            searchItemBtn.isEnabled = true
                            dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqSent")
                            dbRef.removeEventListener(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ds in snapshot.children) {
                                        if (ds.value == user.id) {
                                            ds.ref.removeValue()
                                        }
                                    }
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }

                            })
                        }
                    }
                }
                else {
                    Toast.makeText(context, "Failed to send request!", Toast.LENGTH_SHORT).show()
                    searchItemBtn.text = "Connect"
                    searchItemBtn.isEnabled = true
                }
            }
        }

    }

}
