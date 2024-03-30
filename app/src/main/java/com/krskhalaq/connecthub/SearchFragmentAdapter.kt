package com.krskhalaq.connecthub

import android.annotation.SuppressLint
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

        fun bind(position: Int) {
            searchItemTV.text = userList[position].name

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

        @SuppressLint("SetTextI18n")
        private fun sendConnectRequest(user: User) {
            val dbRef = SignUpActivity.dbFirebase.getReference("Users").child(SignUpActivity.uId).child("ConnectReqSent")
            dbRef.push().setValue(user.id).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Connect Request Sent!", Toast.LENGTH_SHORT).show()
                    searchItemBtn.text = "Pending"
                    searchItemBtn.isEnabled = false
                }
                else
                    Toast.makeText(context, "Failed to send request!", Toast.LENGTH_SHORT).show()
            }
        }

    }

}
