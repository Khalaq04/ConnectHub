package com.krskhalaq.connecthub

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FrndsFragmentAdapter(private val context: Context, private val reqListUser: ArrayList<User>) : RecyclerView.Adapter<FrndsFragmentAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FrndsFragmentAdapter.ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.frnds_fragment_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: FrndsFragmentAdapter.ViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = reqListUser.size

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        private val frndsItemTV = itemView.findViewById<TextView>(R.id.frndsItemTV)
        private val acceptBtn: Button = itemView.findViewById(R.id.acceptBtn)

        fun bind(position: Int) {
            frndsItemTV.text = reqListUser[position].name

            acceptBtn.setOnClickListener {
                acceptConnectRequest(reqListUser[position])
            }
        }

        private fun acceptConnectRequest(user: User) {
            var dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("Connections")
            dbRef.push().setValue(user.id).addOnCompleteListener {it1 ->
                if (it1.isSuccessful) {
                    Toast.makeText(context, "Accepting Connect Request!", Toast.LENGTH_SHORT).show()

                    dbRef = SignUpActivity.dbFirebase.getReference("Users").child(user.id).child("Connections")
                    dbRef.push().setValue(SignUpActivity.uId).addOnCompleteListener {it2 ->
                        if (it2.isSuccessful) {
                            dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqReceived")
                            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    for (ds in snapshot.children) {
                                        if (ds.value.toString() == user.id) {
                                            ds.ref.removeValue()
                                        }
                                    }

                                    dbRef = SignUpActivity.dbFirebase.getReference("Users").child(user.id).child("ConnectReqSent")
                                    dbRef.addListenerForSingleValueEvent(object :
                                        ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (ds in snapshot.children) {
                                                if (ds.value.toString() == SignUpActivity.uId) {
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
                }
            }
        }

    }

}
